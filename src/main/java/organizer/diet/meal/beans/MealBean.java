package organizer.diet.meal.beans;

import organizer.diet.ingredient.dtos.IngredientVolume;
import organizer.diet.meal.dtos.MealDTO;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

public class MealBean {
    MealDTO dto;

    DataModel<IngredientVolume> mealIngredientsDataModel = new ListDataModel(dto.getMealIngredients());
}
