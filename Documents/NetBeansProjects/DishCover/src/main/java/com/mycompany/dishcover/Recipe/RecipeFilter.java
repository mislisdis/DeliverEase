package com.mycompany.dishcover.Recipe;

import java.util.List;

public class RecipeFilter {
    private String name;
    private List<String> ingredients;
    private Integer maxPrepTime;
    private Integer maxCookTime;
    private String difficulty;
    private Boolean vegetarian;
    private Boolean vegan;

    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public Integer getMaxPrepTime() { return maxPrepTime; }
    public void setMaxPrepTime(Integer maxPrepTime) { this.maxPrepTime = maxPrepTime; }

    public Integer getMaxCookTime() { return maxCookTime; }
    public void setMaxCookTime(Integer maxCookTime) { this.maxCookTime = maxCookTime; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Boolean getVegetarian() { return vegetarian; }
    public void setVegetarian(Boolean vegetarian) { this.vegetarian = vegetarian; }

    public Boolean getVegan() { return vegan; }
    public void setVegan(Boolean vegan) { this.vegan = vegan; }
}
