package organizer.diet.meal.dtos;

import lombok.Getter;
import lombok.Setter;
import organizer.diet.ingredient.dtos.IngredientDTO;

import java.util.List;


@Getter
@Setter
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

}
