package organizer.diet.ingredient.dtos;


import lombok.Getter;
import lombok.Setter;
import organizer.system.image.daos.ImageDAO;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Getter
@Setter
public class ShoppingListIngredientDTO extends IngredientDTO {


    public ShoppingListIngredientDTO() {
        this.setShoppingListIngredient(true);
    }



}
