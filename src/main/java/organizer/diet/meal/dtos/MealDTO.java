package organizer.diet.meal.dtos;

import organizer.diet.ingredient.dtos.IngredientDTO;

import java.util.List;


public class MealDTO {
    private List<IngredientDTO> mealIngredients;
    private int mID;
    private String name;


    private float calories;
    private float fats;
    private float protein;
    private float carbs;



    public void calculateCalories() {


        for (IngredientDTO dto : mealIngredients) {

            this.calories = this.calories + (dto.getCalories() * dto.getAmount() / 100);
            this.fats = this.fats + (dto.getMacroDTO().getFatDTO().getAmount() * dto.getAmount() / 100);
            this.protein = this.protein + (dto.getMacroDTO().getProteinDTO().getAmount() * dto.getAmount() / 100);
            this.carbs = this.carbs + (dto.getMacroDTO().getCarbDTO().getAmount() * dto.getAmount() / 100);
        }

    }

    public void add(IngredientDTO dto) {
        this.mealIngredients.add(dto);


    }


    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }


    public int getmID() {
        return mID;
    }

    public void setmID(int mID) {
        this.mID = mID;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IngredientDTO> getMealIngredients() {
        return mealIngredients;
    }

    public void setMealIngredients(List<IngredientDTO> mealIngredients) {
        this.mealIngredients = mealIngredients;
    }

    public float getFats() {
        return fats;
    }

    public void setFats(float fats) {
        this.fats = fats;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getCarbs() {
        return carbs;
    }

    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }
}
