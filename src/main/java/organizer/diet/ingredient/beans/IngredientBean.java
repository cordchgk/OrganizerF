package organizer.diet.ingredient.beans;

import organizer.diet.ingredient.dtos.IngredientDTO;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named("ingredient")
@ViewScoped
public class IngredientBean implements Serializable {
    IngredientDTO dto;
}
