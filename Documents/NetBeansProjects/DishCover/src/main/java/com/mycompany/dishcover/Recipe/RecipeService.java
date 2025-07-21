package com.mycompany.dishcover.Recipe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeService {
    private List<Recipe> recipes;
    private static final String RECIPE_JSON_PATH = "/data/recipes.json"; // classpath-relative

    public RecipeService() {
        loadRecipes();
        System.out.println("✅ Loaded " + recipes.size() + " recipes");
        recipes.forEach(r -> System.out.println("🔸 " + r.getName()));
    }

    private void loadRecipes() {
        try (InputStream inputStream = getClass().getResourceAsStream(RECIPE_JSON_PATH)) {
            ObjectMapper objectMapper = new ObjectMapper();
            recipes = objectMapper.readValue(inputStream, new TypeReference<List<Recipe>>() {});
        } catch (Exception e) {
            System.err.println("Failed to load recipes: " + e.getMessage());
            recipes = new ArrayList<>();
        }
    }

    public List<Recipe> getAllRecipes() {
        return recipes;
    }

    public List<Recipe> filterRecipes(RecipeFilter filter) {
        return recipes.stream().filter(recipe -> {
            //  Name filter
            if (filter.getName() != null && !recipe.getName().toLowerCase().contains(filter.getName().toLowerCase())) {
                return false;
            }

            //  Vegetarian
            if (filter.isVegetarian() && !recipe.isVegetarian()) {
                return false;
            }

            //  Vegan
            if (filter.isVegan() && !recipe.isVegan()) {
                return false;
            }

            // 🔥 Max Cook Time
            if (filter.getMaxCookTime() > 0) {
                int recipeMinutes = parseTimeString(recipe.getCook_time());
                if (recipeMinutes > filter.getMaxCookTime()) return false;
            }

            // 🧪 Difficulty
            if (filter.getDifficulty() != null && !filter.getDifficulty().equalsIgnoreCase(recipe.getDifficulty())) {
                return false;
            }

            //Ingredient match (any match)
            if (filter.getIngredients() != null && !filter.getIngredients().isEmpty()) {
                boolean matches = recipe.getIngredients().stream()
                    .map(String::toLowerCase)
                    .anyMatch(ing -> filter.getIngredients().stream()
                        .map(String::toLowerCase)
                        .anyMatch(ing::contains));

                if (!matches) return false;
            }
            // ✅ Passed all filters
            return true;
        }).collect(Collectors.toList());
    }

    // ✅ Parses strings like "1 hr 15 min", "45 min", etc.
    private int parseTimeString(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return Integer.MAX_VALUE;

        timeStr = timeStr.toLowerCase();
        int totalMinutes = 0;

        try {
            if (timeStr.contains("hr")) {
                String[] parts = timeStr.split("hr");
                String hourPart = parts[0].trim();
                int hours = Integer.parseInt(hourPart);
                totalMinutes += hours * 60;

                if (parts.length > 1 && parts[1].contains("min")) {
                    String minutePart = parts[1].replace("min", "").trim();
                    if (!minutePart.isEmpty()) {
                        totalMinutes += Integer.parseInt(minutePart);
                    }
                }
            } else if (timeStr.contains("min")) {
                String minutePart = timeStr.replace("min", "").trim();
                totalMinutes += Integer.parseInt(minutePart);
            }
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Error parsing time string: " + timeStr);
            return Integer.MAX_VALUE;
        }

        return totalMinutes;
    }
}
