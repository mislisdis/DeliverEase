package com.mycompany.dishcover.Recipe;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private int id;
    private boolean vegetarian;
    private boolean vegan;
    private int calories;
    private String name;
    private String description;

    @JsonProperty("prep_time")
    private String prepTime;
    
    @JsonProperty("cook_time")
    private String cook_time;
    
    private int servings;
    
    private List<String> tags;
    
    private String difficulty;
    private List<String> ingredients;
    private List<String> steps;

    @JsonProperty("image_path")
    private String imagePath;

    public Recipe() {
        this.ingredients = new ArrayList<>();
        this.steps = new ArrayList<>();
        this.tags=new ArrayList<>();
    }

    // ===== Getter and Setter Methods =====

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
    

    public String getName(){
        return this.name;
    }

    public void setName(String newname){
        this.name = newname;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVegetarian() {
        return this.vegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public boolean isVegan() {
        return this.vegan;
    }

    public void setVegan(boolean vegan) {
        this.vegan = vegan;
    }
    
    public int getCalories() {
    return this.calories;
}

public void setCalories(int calories) {
    this.calories = calories;
}

    public String getPrep_time() {
        return this.prepTime;
    }

    public void setPrep_time(String prepTime) {
        this.prepTime = prepTime;
    }
    
    public String getCook_time() {
        return this.cook_time;
    }

    public void setCook_time(String cookTime) {
        this.cook_time = cookTime;
    }
    
    public List<String> getTags() { 
        return tags; 
    }
    
    public void setTags(List<String> tags) { 
        this.tags = tags; 
    }
    
     public int getServings() { 
         return servings; 
    }
     
    public void setServings(int servings) { 
        this.servings = servings; 
    }
    
    public String getDifficulty() {
        return this.difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public List<String> getIngredients() {
        return this.ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getSteps() {
        return this.steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public String getImage_path() {
        return this.imagePath;
    }

    public void setImage_path(String imagePath) {
        this.imagePath = imagePath;
    }

  
    public boolean matchesPrepTime(String cookTime) {
        return parseTimeString(cookTime) <= parseTimeString(this.prepTime);
    }

    // ✅ TIME PARSING METHOD
    public int parseTimeString(String timeString) {
        int totalMinutes = 0;
        timeString = timeString.toLowerCase();

        if (timeString.contains("hr")) {
            String[] parts = timeString.split("hr");
            try {
                int hours = Integer.parseInt(parts[0].trim());
                totalMinutes += hours * 60;

                if (parts.length > 1 && parts[1].contains("min")) {
                    int minutes = Integer.parseInt(parts[1].replace("min", "").trim());
                    totalMinutes += minutes;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error parsing hour/minute in: " + timeString);
            }
        } else if (timeString.contains("min")) {
            try {
                totalMinutes += Integer.parseInt(timeString.replace("min", "").trim());
            } catch (NumberFormatException e) {
                System.out.println("Error parsing minutes in: " + timeString);
            }
        }

        return totalMinutes;
    }
}
    

