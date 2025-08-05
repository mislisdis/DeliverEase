package com.mycompany.dishcover.Util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.dishcover.Recipe.Recipe;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ApiService {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    // --- GET Favorites ---
    public static CompletableFuture<List<Recipe>> getFavoritesAsync(int userId) {
        String url = "http://localhost/dishcovery-api/get_favorites.php?user_id=" + userId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(json -> {
                    try {
                        return mapper.readValue(json, new TypeReference<List<Recipe>>() {});
                    } catch (JsonProcessingException e) {
                        System.err.println("❌ JSON parsing error: " + e.getMessage());
                        return List.of(); // return empty list on failure
                    }
                });
    }

    // --- SAVE Favorite Asynchronously (returns "success", "duplicate", or "error") ---
    public static CompletableFuture<String> saveRecipeToFavoritesAsync(int recipeId, int userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<Object, Object> data = new HashMap<>();
                data.put("recipe_id", recipeId);
                data.put("user_id", userId);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost/dishcovery-api/add_favorite.php"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(ofFormData(data))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("🔁 POST to: /add_favorite.php");
                System.out.println("📨 Response: " + response.statusCode() + " -> " + response.body());

                Map<String, Object> result = mapper.readValue(response.body(), new TypeReference<>() {});
                return String.valueOf(result.get("status")).toLowerCase();

            } catch (IOException | InterruptedException e) {
                System.err.println("❌ Error saving favorite: " + e.getMessage());
                return "error";
            }
        });
    }

    // --- Helper to encode form data ---
    private static HttpRequest.BodyPublisher ofFormData(Map<Object, Object> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) builder.append("&");
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}
