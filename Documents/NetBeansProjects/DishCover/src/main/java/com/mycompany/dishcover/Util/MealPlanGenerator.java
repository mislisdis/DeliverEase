package com.mycompany.dishcover.Util;

import com.mycompany.dishcover.Recipe.MealPlan;
import com.mycompany.dishcover.Recipe.Recipe;

import java.util.*;
import java.util.stream.Collectors;

public class MealPlanGenerator {

    public static List<MealPlan> generate(int days, List<Recipe> allRecipes, boolean vegetarian, boolean vegan) {
        List<Recipe> filtered = allRecipes.stream()
                .filter(r -> !vegetarian || r.isVegetarian())
                .filter(r -> !vegan || r.isVegan())
                .collect(Collectors.toList());

        List<MealPlan> plans = new ArrayList<>();
        Random rand = new Random();

        String[] dayNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        for (int i = 0; i < days; i++) {
            String dayName = (days == 1) ? "Today" : dayNames[i];
            MealPlan plan = new MealPlan(dayName);
            plan.setBreakfast(filtered.get(rand.nextInt(filtered.size())));
            plan.setLunch(filtered.get(rand.nextInt(filtered.size())));
            plan.setDinner(filtered.get(rand.nextInt(filtered.size())));
            plans.add(plan);
        }

        return plans;
    }
}
