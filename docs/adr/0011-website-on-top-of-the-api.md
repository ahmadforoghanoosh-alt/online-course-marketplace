# ADR-0011: A website on top of the REST API, without a framework

- Status: accepted
- Date: 2026-11-25
- Deciders: Forogh Anoosh (Maintainer), Shabir Mohibi (Architect)

## Context
For the final defence the committee wants a working website with frontend and backend code and a live demo.
We already have a REST API (ADR-0009) over the same services as the console, a SQLite database (ADR-0004, ADR-0010)
and CRUD for courses. Typing URLs and PowerShell commands is not a good demo, so users need real pages and buttons.

## Options considered
1. A frontend framework (React, Angular) with its own build (Node.js, npm). Good: components. Bad: a second build tool
   and a second project to install; much more code than four pages need; nobody in the team knows it well.
2. Server-side pages built in Java (HTML strings in the controller). Bad: HTML mixed with Java in the ui layer and the
   API would have to be written twice (JSON and HTML).
3. Plain HTML, CSS and JavaScript files served by the same JDK HTTP server, calling the existing API with `fetch`.
   Good: no new tool or library, one command starts everything, the API stays the only entry to the services.

## Decision
We chose option 3. The files are in `src/main/resources/web` (`index.html`, `style.css`, `app.js`). `HttpApiServer`
returns a web file for `GET /` and `GET /<name>.html|.css|.js` (class `WebFile`, which only accepts simple file names,
so `/../pom.xml` is refused); every other request goes to `ApiController` as before. The page has four parts:
Catalogue, Cart, My learning and Admin (CRUD). All texts from the database are set with `textContent`, never as HTML,
and the HTML certificate is shown in a sandboxed `iframe`, so no script can be injected. A "Show patterns" switch
shows which pattern works behind each part, to help explain the design. One small endpoint was added:
`GET /certificate-formats`.

## Consequences
Good: a full demo in the browser; frontend and backend are separate (the browser only knows the API); no new
dependency; `WebFileTest` checks which files can be served.
Bad / cost: the JavaScript has no automatic tests; it was checked by clicking through every page in a browser
(add to cart, discount, undo, pay, certificate, add / edit / delete a course, an empty title shows the server's error).
There is still one student ("Student") and no login, as in the console version.
