package organizer.diet.ingredient.beans;


import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.user.beans.UserBean;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("userIngredientBean")
@ViewScoped
public class UserIngredientsBean implements Serializable {
    @Inject
    UserBean userBean;
    private List<IngredientDTO> userIngredients;


}
