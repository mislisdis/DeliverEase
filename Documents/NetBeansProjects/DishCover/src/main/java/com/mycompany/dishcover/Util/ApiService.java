package com.mycompany.dishcover.Util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.dishcover.Recipe.MealPlan;
import com.mycompany.dishcover.Recipe.Recipe;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class ApiService {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static List<Recipe> cachedRecipes = null;

    // ============ FAVORITES ============

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
                        return List.of();
                    }
                });
    }

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

                Map<String, Object> result = mapper.readValue(response.body(), new TypeReference<>() {});
                return String.valueOf(result.get("status")).toLowerCase();

            } catch (IOException | InterruptedException e) {
                System.err.println("❌ Error saving favorite: " + e.getMessage());
                return "error";
            }
        });
    }

    // ============ RECIPES FROM JSON ============

    public static List<Recipe> getAllRecipes() {
        if (cachedRecipes != null) return cachedRecipes;

        try (InputStream inputStream = ApiService.class.getResourceAsStream("/data/recipes.json")) {
            if (inputStream == null) {
                System.err.println("❌ recipes.json file not found in /resources/data/");
                return List.of();
            }

            cachedRecipes = mapper.readValue(inputStream, new TypeReference<List<Recipe>>() {});
            return cachedRecipes;

        } catch (IOException e) {
            System.err.println("❌ Failed to load recipes from JSON: " + e.getMessage());
            return List.of();
        }
    }

    public static Recipe getRecipeById(int id) {
        return getAllRecipes().stream()
                .filter(recipe -> recipe.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // ============ MEAL PLANS ============

    public static void saveMealPlanToDatabase(List<MealPlan> plans, int userId, String planName) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String insertPlanSql = "INSERT INTO meal_plan_sets (user_id, plan_name) VALUES (?, ?)";
            PreparedStatement planStmt = conn.prepareStatement(insertPlanSql, Statement.RETURN_GENERATED_KEYS);
            planStmt.setInt(1, userId);
            planStmt.setString(2, planName);
            planStmt.executeUpdate();

            ResultSet generatedKeys = planStmt.getGeneratedKeys();
            int planSetId = -1;
            if (generatedKeys.next()) {
                planSetId = generatedKeys.getInt(1);
            }

            String insertItemSql = "INSERT INTO meal_plan_items (plan_set_id, day_of_week, meal_type, recipe_id) VALUES (?, ?, ?, ?)";
            PreparedStatement itemStmt = conn.prepareStatement(insertItemSql);

            for (MealPlan plan : plans) {
                if (plan.getBreakfast() != null) {
                    itemStmt.setInt(1, planSetId);
                    itemStmt.setString(2, plan.getDayOfWeek());
                    itemStmt.setString(3, "Breakfast");
                    itemStmt.setInt(4, plan.getBreakfast().getId());
                    itemStmt.addBatch();
                }
                if (plan.getLunch() != null) {
                    itemStmt.setInt(1, planSetId);
                    itemStmt.setString(2, plan.getDayOfWeek());
                    itemStmt.setString(3, "Lunch");
                    itemStmt.setInt(4, plan.getLunch().getId());
                    itemStmt.addBatch();
                }
                if (plan.getDinner() != null) {
                    itemStmt.setInt(1, planSetId);
                    itemStmt.setString(2, plan.getDayOfWeek());
                    itemStmt.setString(3, "Dinner");
                    itemStmt.setInt(4, plan.getDinner().getId());
                    itemStmt.addBatch();
                }
            }

            itemStmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("❌ SQL error (saveMealPlanToDatabase): " + e.getMessage());
        }
    }

    public static List<SavedMealPlan> getSavedMealPlans(int userId) {
        List<SavedMealPlan> allPlans = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection()) {
            String planSql = "SELECT id, plan_name, date_saved FROM meal_plan_sets WHERE user_id = ?";
            PreparedStatement planStmt = conn.prepareStatement(planSql);
            planStmt.setInt(1, userId);
            ResultSet planRs = planStmt.executeQuery();

            while (planRs.next()) {
                int planSetId = planRs.getInt("id");
                String planName = planRs.getString("plan_name");
                Date dateSaved = planRs.getTimestamp("date_saved");

                String itemSql = "SELECT * FROM meal_plan_items WHERE plan_set_id = ? " +
                        "ORDER BY FIELD(day_of_week, 'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'), meal_type";
                PreparedStatement itemStmt = conn.prepareStatement(itemSql);
                itemStmt.setInt(1, planSetId);
                ResultSet itemRs = itemStmt.executeQuery();

                Map<String, MealPlan> planMap = new LinkedHashMap<>();

                while (itemRs.next()) {
                    String day = itemRs.getString("day_of_week");
                    String type = itemRs.getString("meal_type");
                    int recipeId = itemRs.getInt("recipe_id");
                    Recipe recipe = getRecipeById(recipeId);

                    planMap.putIfAbsent(day, new MealPlan(day));
                    MealPlan plan = planMap.get(day);

                    switch (type) {
                        case "Breakfast" -> plan.setBreakfast(recipe);
                        case "Lunch" -> plan.setLunch(recipe);
                        case "Dinner" -> plan.setDinner(recipe);
                    }
                }

                allPlans.add(new SavedMealPlan(planSetId, planName, dateSaved, new ArrayList<>(planMap.values())));
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL error (getSavedMealPlans): " + e.getMessage());
        }

        return allPlans;
    }

    public static void deleteSavedMealPlan(int planSetId) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement delItems = conn.prepareStatement("DELETE FROM meal_plan_items WHERE plan_set_id = ?");
            delItems.setInt(1, planSetId);
            delItems.executeUpdate();

            PreparedStatement delPlan = conn.prepareStatement("DELETE FROM meal_plan_sets WHERE id = ?");
            delPlan.setInt(1, planSetId);
            delPlan.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ SQL error (deleteSavedMealPlan): " + e.getMessage());
        }
    }

    public static void renameMealPlan(int planSetId, String newName) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE meal_plan_sets SET plan_name = ? WHERE id = ?");
            stmt.setString(1, newName);
            stmt.setInt(2, planSetId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ SQL error (renameMealPlan): " + e.getMessage());
        }
    }

    // ============ Helper for POST ============

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

    // ============ Inner Class for Metadata ============

    public static class SavedMealPlan {
        private int planSetId;
        private String planName;
        private Date dateSaved;
        private List<MealPlan> mealPlans;

        public SavedMealPlan(int planSetId, String planName, Date dateSaved, List<MealPlan> mealPlans) {
            this.planSetId = planSetId;
            this.planName = planName;
            this.dateSaved = dateSaved;
            this.mealPlans = mealPlans;
        }

        public int getPlanSetId() { return planSetId; }
        public String getPlanName() { return planName; }
        public Date getDateSaved() { return dateSaved; }
        public List<MealPlan> getMealPlans() { return mealPlans; }
    }
}
