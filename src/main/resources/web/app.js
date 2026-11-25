// Course Marketplace website: plain JavaScript, it only talks to our REST API (ADR-0011).
"use strict";

const state = { courses: [], cart: null, myCourses: [], formats: ["Text"] };

// ---------- helpers ----------

/** Calls the API and returns the JSON. A 4xx answer becomes an Error with the server's message. */
async function api(method, path, params) {
  const query = params ? "?" + new URLSearchParams(params) : "";
  const response = await fetch(path + query, { method });
  const body = await response.json();
  if (!response.ok) {
    throw new Error(body.error || "Something went wrong");
  }
  return body;
}

/** Makes an element. Text is always set with textContent, so data from the database is never run as HTML. */
function el(tag, props = {}, ...children) {
  const node = document.createElement(tag);
  for (const [key, value] of Object.entries(props)) {
    if (key.startsWith("on")) node.addEventListener(key.slice(2), value);
    else if (key === "class") node.className = value;
    else if (key.includes("-")) node.setAttribute(key, value);
    else node[key] = value;
  }
  for (const child of children) {
    if (child != null) node.append(child);
  }
  return node;
}

const $ = (id) => document.getElementById(id);
const money = (n) => Number(n).toLocaleString("en-US", { maximumFractionDigits: 2 }) + " KZT";

let toastTimer;
function toast(message, isError = false) {
  const box = $("toast");
  box.textContent = message;
  box.className = "show" + (isError ? " error" : "");
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => (box.className = ""), 2800);
}

/** Runs an action; any error is shown to the user instead of breaking the page. */
async function run(action) {
  try {
    await action();
  } catch (e) {
    toast(e.message, true);
  }
}

// ---------- tabs ----------

function showPage(name) {
  document.querySelectorAll(".tab").forEach((t) => t.classList.toggle("active", t.dataset.page === name));
  document.querySelectorAll(".page").forEach((p) => p.classList.toggle("active", p.id === "page-" + name));
  if (name === "learning") run(loadLearning);
}

// ---------- catalogue ----------

const owned = (id) => state.myCourses.some((c) => c.id === id);
const inCart = (id) => state.cart && state.cart.courses.some((c) => c.id === id);

function renderCatalogue() {
  const text = $("search").value.trim().toLowerCase();
  const list = state.courses.filter((c) =>
    (c.title + " " + c.instructor).toLowerCase().includes(text));
  const grid = $("course-grid");
  grid.replaceChildren();
  if (list.length === 0) {
    grid.append(el("p", { class: "empty", textContent: state.courses.length ? "No course matches your search" : "No courses yet" }));
    return;
  }
  for (const c of list) {
    let button;
    if (owned(c.id)) button = el("span", { class: "done", textContent: "✓ In My courses" });
    else if (inCart(c.id)) button = el("button", { textContent: "In cart", disabled: true });
    else button = el("button", { class: "primary", textContent: "Add to cart", onclick: () => run(() => addToCart(c.id)) });
    grid.append(el("article", { class: "card" },
      el("h3", { textContent: c.title }),
      el("p", { class: "by", textContent: c.instructor }),
      el("p", { class: "desc", textContent: c.description }),
      el("div", { class: "foot" }, el("span", { class: "price", textContent: money(c.price) }), button)));
  }
}

async function loadCourses() {
  state.courses = await api("GET", "/courses");
  renderCatalogue();
  renderAdmin();
}

// ---------- cart (every change is a Command on the server, so it can be undone) ----------

async function addToCart(id) {
  setCart(await api("POST", "/cart/" + id));
  toast("Added to cart");
}

function setCart(cart) {
  state.cart = cart;
  $("cart-count").textContent = cart.courses.length;
  const items = $("cart-items");
  items.replaceChildren();
  if (cart.courses.length === 0) {
    items.append(el("li", { class: "empty", textContent: "Your cart is empty. Add a course from the catalogue." }));
  }
  for (const c of cart.courses) {
    items.append(el("li", {},
      el("div", {}, el("strong", { textContent: c.title }), el("div", { class: "by", textContent: c.instructor })),
      el("div", { class: "row" },
        el("span", { class: "price", textContent: money(c.price) }),
        el("button", { class: "ghost danger", textContent: "Remove",
          onclick: () => run(async () => setCart(await api("DELETE", "/cart/" + c.id))) }))));
  }
  $("subtotal").textContent = money(cart.subtotal);
  $("discount").textContent = cart.discount || "none";
  $("total").textContent = money(cart.total);
  $("undo").disabled = !cart.canUndo;
  $("checkout").disabled = cart.courses.length === 0;
  renderCatalogue();
}

async function loadPaymentMethods() {
  const methods = await api("GET", "/payment-methods");
  const box = $("payment-methods");
  methods.forEach((name, i) => box.append(el("label", {},
    el("input", { type: "radio", name: "method", value: name, checked: i === 0 }), name)));
}

async function checkout() {
  const method = document.querySelector('input[name="method"]:checked').value;
  const result = await api("POST", "/checkout", { method });
  const box = $("checkout-result");
  if (result.status === "PAID") {
    box.replaceChildren(el("div", { class: "result ok",
      textContent: "Paid! Order " + result.order + ", total " + money(result.total) + ". The course is in My learning." }));
  } else {
    box.replaceChildren(el("div", { class: "result bad", textContent: "Payment failed. Your cart is kept, please try again." }));
  }
  setCart(await api("GET", "/cart"));
  await loadLearning();
}

// ---------- my learning ----------

async function loadLearning() {
  const [mine, orders] = await Promise.all([api("GET", "/my-courses"), api("GET", "/orders")]);
  state.myCourses = mine;
  const list = $("my-courses");
  list.replaceChildren();
  if (mine.length === 0) list.append(el("li", { class: "empty", textContent: "You have no courses yet." }));
  for (const c of mine) {
    const format = el("select", { "aria-label": "Certificate format" },
      ...state.formats.map((f) => el("option", { value: f, textContent: f })));
    list.append(el("li", {},
      el("div", {}, el("strong", { textContent: c.title }), el("div", { class: "by", textContent: c.instructor })),
      el("div", { class: "row" }, format,
        el("button", { class: "primary", textContent: "Complete & get certificate",
          onclick: () => run(() => certificate(c.id, format.value)) }))));
  }
  const rows = $("orders");
  rows.replaceChildren();
  if (orders.length === 0) rows.append(el("tr", {}, el("td", { colSpan: 3, class: "empty", textContent: "No orders yet." })));
  for (const o of orders.slice().reverse()) {
    rows.append(el("tr", {},
      el("td", { textContent: o.reference }),
      el("td", { textContent: o.courses.map((c) => c.title).join(", ") }),
      el("td", { class: "num", textContent: money(o.total) })));
  }
  renderCatalogue();
}

async function certificate(courseId, format) {
  const result = await api("POST", "/my-courses/" + courseId + "/certificate", { format });
  const body = $("certificate-body");
  if (format === "HTML") {
    // sandbox: the certificate HTML is shown, but no script in it can run
    body.replaceChildren(el("iframe", { srcdoc: result.certificate, sandbox: "", title: "Certificate" }));
  } else {
    body.replaceChildren(el("pre", { textContent: result.certificate }));
  }
  $("certificate").showModal();
}

// ---------- admin: CRUD ----------

function renderAdmin() {
  const rows = $("admin-courses");
  rows.replaceChildren();
  for (const c of state.courses) {
    const del = el("button", { class: "danger", textContent: "Delete" });
    del.onclick = () => {
      if (!del.classList.contains("sure")) {        // first click asks, second click deletes
        del.classList.add("sure");
        del.textContent = "Sure?";
        setTimeout(() => { del.classList.remove("sure"); del.textContent = "Delete"; }, 3000);
        return;
      }
      run(async () => {
        await api("DELETE", "/courses/" + c.id);
        toast("Course deleted");
        await loadCourses();
      });
    };
    rows.append(el("tr", {},
      el("td", { textContent: c.id }),
      el("td", { textContent: c.title }),
      el("td", { textContent: c.instructor }),
      el("td", { class: "num", textContent: money(c.price) }),
      el("td", { class: "actions" }, el("button", { textContent: "Edit", onclick: () => editCourse(c) }), del)));
  }
}

function editCourse(c) {
  $("course-id").value = c.id;
  $("f-title").value = c.title;
  $("f-instructor").value = c.instructor;
  $("f-description").value = c.description;
  $("f-price").value = c.price;
  $("form-title").textContent = "Edit course #" + c.id;
  $("save-course").textContent = "Save changes";
  $("cancel-edit").hidden = false;
  $("f-title").focus();
}

function resetForm() {
  $("course-form").reset();
  $("course-id").value = "";
  $("form-title").textContent = "Add a course";
  $("save-course").textContent = "Add course";
  $("cancel-edit").hidden = true;
}

async function saveCourse(event) {
  event.preventDefault();
  const id = $("course-id").value;
  const data = {
    title: $("f-title").value.trim(),
    instructor: $("f-instructor").value.trim(),
    description: $("f-description").value.trim(),
    price: $("f-price").value,
  };
  if (id) {
    await api("PUT", "/courses/" + id, data);
    toast("Course updated");
  } else {
    await api("POST", "/courses", data);
    toast("Course added");
  }
  resetForm();
  await loadCourses();
}

// ---------- start ----------

document.querySelectorAll(".tab").forEach((t) => t.addEventListener("click", () => showPage(t.dataset.page)));
$("search").addEventListener("input", renderCatalogue);
$("show-patterns").addEventListener("change", (e) => document.body.classList.toggle("patterns", e.target.checked));
$("undo").addEventListener("click", () => run(async () => {
  const cart = await api("POST", "/cart/undo");
  toast("Undone: " + cart.undone);
  setCart(cart);
}));
$("discount-form").addEventListener("submit", (e) => {
  e.preventDefault();
  run(async () => {
    setCart(await api("POST", "/cart/discount", { code: $("discount-code").value.trim() }));
    toast("Discount applied");
  });
});
$("checkout").addEventListener("click", () => run(checkout));
$("course-form").addEventListener("submit", (e) => run(() => saveCourse(e)));
$("cancel-edit").addEventListener("click", resetForm);

run(async () => {
  state.formats = await api("GET", "/certificate-formats");
  await Promise.all([loadCourses(), loadPaymentMethods(), loadLearning()]);
  setCart(await api("GET", "/cart"));
});
