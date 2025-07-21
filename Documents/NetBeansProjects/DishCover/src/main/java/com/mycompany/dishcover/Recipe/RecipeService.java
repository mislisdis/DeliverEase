package com.mycompany.dishcover.Recipe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeService {

    private List<Recipe> recipes;
    private static final String RECIPE_JSON_PATH = "/data/recipes.json";  // classpath-relative path

    public RecipeService() {
        loadRecipes();
        System.out.println("Loaded " + recipes.size() + " recipes");
recipes.forEach(r -> System.out.println("🔸 " + r.getName()));
    }

    private void loadRecipes() {
        System.out.println("Attempting to load recipes from: " + RECIPE_JSON_PATH);
        try (InputStream inputStream = getClass().getResourceAsStream(RECIPE_JSON_PATH)) {

            if (inputStream == null) {
                System.err.println("Could not find 'recipes.json' at path: " + RECIPE_JSON_PATH);
                recipes = new ArrayList<>();
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            recipes = mapper.readValue(inputStream, new TypeReference<List<Recipe>>() {});
            System.out.println("Successfully loaded " + recipes.size() + " recipes.");

            // Log first few recipe names
            recipes.stream().limit(5).forEach(r ->
                System.out.println("Loaded recipe: " + r.getName())
            );

        } catch (IOException e) {
            System.err.println("Error loading recipes: " + e.getMessage());
            recipes = new ArrayList<>();
        }
    }

    public List<Recipe> getRecipes() {
        return this.recipes;
    }

    public void setRecipes(List<Recipe> newRecipes) {
        this.recipes = newRecipes;
    }

    public List<Recipe> searchByIngredients(List<String> ingredients) {
        List<String> lowerCaseIngredients = ingredients.stream()
                .map(String::toLowerCase)
                .toList();

        return recipes.stream().filter(recipe -> {
            List<String> recipeLowerCaseIngredients = recipe.getIngredients().stream()
                    .map(String::toLowerCase)
                    .toList();

            return lowerCaseIngredients.stream().anyMatch(ingredient ->
                    recipeLowerCaseIngredients.stream().anyMatch(rIng ->
                            rIng.contains(ingredient)
                    )
            );
        }).collect(Collectors.toList());
    }

    public List<Recipe> searchByName(String name) {
        String lowerCaseName = name.toLowerCase();
        return recipes.stream()
                .filter(recipe -> recipe.getName().toLowerCase().contains(lowerCaseName))
                .collect(Collectors.toList());
    }

    public List<Recipe> searchByPrepTime(int prepTime) {
        return recipes.stream()
                .filter(recipe -> parseTimeString(recipe.getPrep_time()) <= prepTime)
                .collect(Collectors.toList());
    }

    public List<Recipe> searchByCookTime(int cookTime) {
        return recipes.stream()
                .filter(recipe -> parseTimeString(recipe.getCook_time()) <= cookTime)
                .collect(Collectors.toList());
    }

    public List<Recipe> searchByIngredientsAndPrepTime(List<String> ingredients, int prepTime) {
        return searchByIngredients(ingredients).stream()
                .filter(recipe -> parseTimeString(recipe.getPrep_time()) <= prepTime)
                .collect(Collectors.toList());
    }

    public List<Recipe> searchByFilter(RecipeFilter filter) {
        return recipes.stream()
                .filter(recipe -> filter.getName() == null ||
                        recipe.getName().toLowerCase().contains(filter.getName().toLowerCase()))
                .filter(recipe -> filter.getIngredients() == null || filter.getIngredients().stream()
                        .anyMatch(ing -> recipe.getIngredients().stream()
                                .anyMatch(rIng -> rIng.toLowerCase().contains(ing.toLowerCase()))
                        ))
                .filter(recipe -> filter.getMaxPrepTime() == null ||
                        parseTimeString(recipe.getPrep_time()) <= filter.getMaxPrepTime())
                .filter(recipe -> filter.getMaxCookTime() == null ||
                        parseTimeString(recipe.getCook_time()) <= filter.getMaxCookTime())
                .filter(recipe -> filter.getDifficulty() == null ||
                        recipe.getDifficulty().equalsIgnoreCase(filter.getDifficulty()))
                .filter(recipe -> filter.getVegetarian() == null ||
                        recipe.isVegetarian() == filter.getVegetarian())
                .filter(recipe -> filter.getVegan() == null ||
                        recipe.isVegan() == filter.getVegan())
                .collect(Collectors.toList());
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
