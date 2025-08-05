package com.mycompany.dishcover.Recipe;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Recipe {

    private int id;
    private String name;
    private String description;

    private boolean vegetarian;
    private boolean vegan;

    private int calories;

    @JsonProperty("prep_time")
    private String prepTime;

    @JsonProperty("cook_time")
    private String cook_time;

    private int servings;
    private String difficulty;

    private List<String> ingredients;
    private List<String> steps;
    private List<String> tags;

    @JsonProperty("image_path")
    private String imagePath;

    // --- Default constructor ---
    public Recipe() {
        this.ingredients = new ArrayList<>();
        this.steps = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    // --- Constructor used by ApiService (11 params) ---
    public Recipe(int id, String name, String description, String ingredientsStr, String stepsStr,
                  int calories, String prepTime, String difficulty,
                  boolean vegetarian, boolean vegan, String imagePath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ingredients = parseList(ingredientsStr);
        this.steps = parseList(stepsStr);
        this.calories = calories;
        this.prepTime = prepTime;
        this.difficulty = difficulty;
        this.vegetarian = vegetarian;
        this.vegan = vegan;
        this.imagePath = imagePath;
        this.servings = 0; // default
        this.tags = new ArrayList<>();
    }

    // --- Full constructor (14 params) ---
    public Recipe(int id, String name, String description, String ingredientsStr, String stepsStr,
                  int calories, String prepTime, String cookTime, String difficulty,
                  boolean vegetarian, boolean vegan, String imagePath, int servings, String tagsStr) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ingredients = parseList(ingredientsStr);
        this.steps = parseList(stepsStr);
        this.calories = calories;
        this.prepTime = prepTime;
        this.cook_time = cookTime;
        this.difficulty = difficulty;
        this.vegetarian = vegetarian;
        this.vegan = vegan;
        this.imagePath = imagePath;
        this.servings = servings;
        this.tags = parseList(tagsStr);
    }

    // --- Helper method to convert string to list ---
    private List<String> parseList(String str) {
        if (str == null || str.trim().isEmpty()) return new ArrayList<>();
        return Arrays.asList(str.split("(?m)\\r?\\n|,"));
    }

    // --- Getters and Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isVegetarian() { return vegetarian; }
    public void setVegetarian(boolean vegetarian) { this.vegetarian = vegetarian; }

    public boolean isVegan() { return vegan; }
    public void setVegan(boolean vegan) { this.vegan = vegan; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public String getPrep_time() { return prepTime; }
    public void setPrep_time(String prepTime) { this.prepTime = prepTime; }

    public String getCook_time() { return cook_time; }
    public void setCook_time(String cookTime) { this.cook_time = cookTime; }

    public int getServings() { return servings; }
    public void setServings(int servings) { this.servings = servings; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getImage_path() { return imagePath; }
    public void setImage_path(String imagePath) { this.imagePath = imagePath; }

    // --- Matching logic for filtering ---
    public boolean matchesPrepTime(String maxTime) {
        return parseTimeString(prepTime) <= parseTimeString(maxTime);
    }

    public int parseTimeString(String timeString) {
        if (timeString == null) return 0;

        int totalMinutes = 0;
        timeString = timeString.toLowerCase();

        try {
            if (timeString.contains("hr")) {
                String[] parts = timeString.split("hr");
                int hours = Integer.parseInt(parts[0].trim());
                totalMinutes += hours * 60;

                if (parts.length > 1 && parts[1].contains("min")) {
                    int minutes = Integer.parseInt(parts[1].replace("min", "").trim());
                    totalMinutes += minutes;
                }
            } else if (timeString.contains("min")) {
                totalMinutes += Integer.parseInt(timeString.replace("min", "").trim());
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Error parsing time: " + timeString);
        }

        return totalMinutes;
    }
}
