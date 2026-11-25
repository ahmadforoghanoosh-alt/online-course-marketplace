package kz.kaznaru.capstone.ui;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Connects the website and ApiController to the HTTP server that comes with the JDK (no framework).
 * GET "/" and GET of a web file return the website; every other request goes to the API.
 */
public class HttpApiServer {
    private final ApiController controller;
    private HttpServer server;

    public HttpApiServer(ApiController controller) {
        this.controller = controller;
    }

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this::handle);
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (exchange.getRequestMethod().equals("GET")) {
            Optional<WebFile> page = WebFile.find(path);
            if (page.isPresent()) {   // the website: index.html, style.css, app.js
                send(exchange, 200, page.get().contentType(), page.get().bytes());
                return;
            }
        }
        ApiResponse response = controller.handle(exchange.getRequestMethod(), path,
                exchange.getRequestURI().getRawQuery());
        send(exchange, response.status(), "application/json; charset=utf-8",
                response.body().getBytes(StandardCharsets.UTF_8));
    }

    private static void send(HttpExchange exchange, int status, String type, byte[] bytes) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", type);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
