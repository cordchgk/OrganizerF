package organizer.diet.meal.dtos;

import organizer.diet.ingredient.dtos.IngredientVolume;

import java.util.List;


public class MealDTO {
    private List<IngredientVolume> mealIngredients;

    public List<IngredientVolume> getMealIngredients() {
        return mealIngredients;
    }

    public void setMealIngredients(List<IngredientVolume> mealIngredients) {
        this.mealIngredients = mealIngredients;
    }
}
