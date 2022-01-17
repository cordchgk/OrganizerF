package organizer.diet.ingredient.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShoppingListIngredientDTO extends IngredientDTO {


    public ShoppingListIngredientDTO() {
        this.setShoppingListIngredient(true);
    }

}
