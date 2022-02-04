package organizer.diet.ingredient.beans;


import lombok.Getter;
import lombok.Setter;
import organizer.diet.ingredient.daos.IngredientDAO;
import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.diet.system.IngredientSearch;
import organizer.system.exceptions.DuplicateException;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * CDI BEAN providing information and methods for creating a new ingredient
 */


@Named("newIngredientBean")
@ViewScoped
@Getter
@Setter
public class NewIngredientBean implements Serializable {
    private IngredientDTO ingredientDTO;
    private List<IngredientDTO> ingredients;
    private DataModel<IngredientDTO> ingredientsDataModel;
    private IngredientDAO ingredientDAO;


    @Inject
    private UserBean u_Bean;

    @PostConstruct
    public void init() {


        this.build();
        this.ingredientDAO = new IngredientDAO();
    }


    /**
     * Creates the new ingredient,loads it into the database
     * and rebuilds the DataModels
     */
    public void create() {
        for (IngredientDTO pointerDTO : this.ingredients) {
            try {
                pointerDTO.setIID(this.ingredientDAO.createNewIngredient(pointerDTO, u_Bean.getU_DTO()));

            } catch (DuplicateException e) {
                e.printStackTrace();
            }

            IngredientSearch.getInstance().add(pointerDTO.getName(), pointerDTO.getIID());
            IngredientSearch.getInstance().addToList(pointerDTO);
        }
        this.build();
    }


    private void build() {
        this.ingredientDTO = new IngredientDTO();
        this.ingredients = new ArrayList<>();
        this.ingredients.add(ingredientDTO);
        this.ingredientsDataModel = new ListDataModel<>(this.ingredients);

    }

}
