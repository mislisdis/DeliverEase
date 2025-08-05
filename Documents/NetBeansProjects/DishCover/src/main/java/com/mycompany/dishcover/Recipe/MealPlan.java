package com.mycompany.dishcover.Recipe;

public class MealPlan {
    private final String dayOfWeek;
    private Recipe breakfast;
    private Recipe lunch;
    private Recipe dinner;

    public MealPlan(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDayOfWeek() { return dayOfWeek; }
    public Recipe getBreakfast() { return breakfast; }
    public Recipe getLunch() { return lunch; }
    public Recipe getDinner() { return dinner; }

    public void setBreakfast(Recipe breakfast) { this.breakfast = breakfast; }
    public void setLunch(Recipe lunch) { this.lunch = lunch; }
    public void setDinner(Recipe dinner) { this.dinner = dinner; }
}
