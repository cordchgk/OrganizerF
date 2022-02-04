package organizer.diet.ingredient.beans;


import lombok.Getter;
import lombok.Setter;
import organizer.diet.ingredient.daos.IngredientDAO;
import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * CDI BEAN for the Ingredient Page
 *
 */
@ViewScoped
@Named("i_Bean")
@Getter
@Setter
public class IngredientBean implements Serializable {


    private IngredientDTO ingredientDTO;
    private IngredientDAO ingredientDAO;

    private List<IngredientDTO> ingredients;
    private DataModel<IngredientDTO> ingredientsDataModel;

    @Inject
    private UserBean userBean;


    @PostConstruct
    public void init() {
        int id = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("iID"));

        this.ingredientDTO = new IngredientDTO(id);
        this.ingredientDAO = new IngredientDAO();
        this.ingredientDTO = ingredientDAO.getIngredientByDTO(this.ingredientDTO);
        this.ingredients = new ArrayList<IngredientDTO>();
        this.ingredients.add(this.ingredientDTO);
        this.ingredientsDataModel = new ListDataModel<>(this.ingredients);



    }


}
