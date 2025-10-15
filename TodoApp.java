import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TodoApp {
    static class Todo {
        int id;
        String title;
        boolean done;
        Todo(int id, String title) {
            this.id = id;
            this.title = title;
            this.done = false;
        }
    }

    static List<Todo> todos = Collections.synchronizedList(new ArrayList<>());
    static AtomicInteger idCounter = new AtomicInteger(1);

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", exchange -> {
            String response = "Simple Java TODO App is running!";
            sendResponse(exchange, 200, response);
        });

        server.createContext("/todos", exchange -> {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET" -> handleGet(exchange);
                case "POST" -> handlePost(exchange);
                default -> sendResponse(exchange, 405, "Method Not Allowed");
            }
        });

        server.start();
        System.out.println("Server started on port 8080");
    }

    private static void handleGet(HttpExchange exchange) throws IOException {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < todos.size(); i++) {
            Todo t = todos.get(i);
            json.append(String.format("{\"id\":%d,\"title\":\"%s\",\"done\":%b}", t.id, t.title, t.done));
            if (i < todos.size() - 1) json.append(",");
        }
        json.append("]");
        sendResponse(exchange, 200, json.toString());
    }

    private static void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        // Beklenen format: title=Yapılacak iş
        String title = body.replace("title=", "").trim();
        Todo todo = new Todo(idCounter.getAndIncrement(), title);
        todos.add(todo);
        String response = String.format("{\"id\":%d,\"title\":\"%s\",\"done\":false}", todo.id, todo.title);
        sendResponse(exchange, 201, response);
    }

    private static void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
