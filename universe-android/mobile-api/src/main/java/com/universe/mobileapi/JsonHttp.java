package com.universe.mobileapi;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

final class JsonHttp {

    private static final Gson GSON = new Gson();

    private JsonHttp() {
    }

    static JsonObject readObject(HttpExchange exchange) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(
                exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (!element.isJsonObject()) {
                throw new ServiceException(400, "Nội dung yêu cầu phải là JSON object.");
            }
            return element.getAsJsonObject();
        }
    }

    static void send(HttpExchange exchange, int status, JsonElement payload) throws IOException {
        byte[] bytes = GSON.toJson(payload).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    static JsonObject message(String message) {
        JsonObject json = new JsonObject();
        json.addProperty("message", message);
        return json;
    }

    static Map<String, String> queryParameters(HttpExchange exchange) {
        Map<String, String> result = new HashMap<>();
        String query = exchange.getRequestURI().getRawQuery();
        if (query == null || query.isBlank()) {
            return result;
        }
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            String value = parts.length == 2
                    ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                    : "";
            result.put(key, value);
        }
        return result;
    }
}

