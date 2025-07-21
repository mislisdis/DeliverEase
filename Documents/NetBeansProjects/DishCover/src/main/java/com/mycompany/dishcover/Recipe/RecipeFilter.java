package com.mycompany.dishcover.Recipe;

import java.util.List;

public class RecipeFilter {
    private String name;
    private List<String> ingredients;
    private int maxCookTime;  // in minutes
    private String difficulty;
    private boolean vegetarian;
    private boolean vegan;

    // Getter and setter for name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for ingredients
    public List<String> getIngredients() {
        return ingredients;
    }
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    // Getter and setter for maxCookTime
    public int getMaxCookTime() {
        return maxCookTime;
    }
    public void setMaxCookTime(int maxCookTime) {
        this.maxCookTime = maxCookTime;
    }

    // Getter and setter for difficulty
    public String getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    // Getter and setter for vegetarian
    public boolean isVegetarian() {
        return vegetarian;
    }
    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    // Getter and setter for vegan
    public boolean isVegan() {
        return vegan;
    }
    public void setVegan(boolean vegan) {
        this.vegan = vegan;
    }
}
