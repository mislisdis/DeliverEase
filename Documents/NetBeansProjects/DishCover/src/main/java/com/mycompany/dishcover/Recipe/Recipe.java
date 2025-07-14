package com.mycompany.dishcover.Recipe;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private int id;
    private String name;
    private String description;
    private boolean vegetarian;
    private boolean vegan;
    private int calories;

    @JsonProperty("prep_time")
    private int prepTime;

    private String difficulty;
    private List<String> ingredients;
    private List<String> steps;

    @JsonProperty("image_path")
    private String imagePath;

    public Recipe() {
        this.ingredients = new ArrayList<>();
        this.steps = new ArrayList<>();
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

    public int getPrep_time() {
        return this.prepTime;
    }

    public void setPrep_time(int prepTime) {
        this.prepTime = prepTime;
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

    // ===== Utility Method =====

    public boolean matchesPrepTime(int cookTime) {
        return cookTime <= prepTime;
    }
}
