package com.mycompany.dishcover.Recipe;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
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

    public boolean matchesPrepTime(int cookTime) {
        return cookTime <= prepTime;
    }
}


    
