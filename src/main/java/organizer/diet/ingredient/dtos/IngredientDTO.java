package organizer.diet.ingredient.dtos;

import organizer.diet.nutrients.macronutrient.dtos.MacroDTO;
import organizer.diet.nutrients.micronutrient.dtos.MicroDTO;

import java.util.List;

public class IngredientDTO {
    List<MacroDTO> macroNutrients;
    List<MicroDTO> microNutrients;
}

