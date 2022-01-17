package organizer.diet.ingredient.beans;


import lombok.Getter;
import lombok.Setter;
import organizer.diet.ingredient.daos.IngredientDAO;
import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.diet.system.IngredientSearch;
import organizer.system.exceptions.DuplicateException;

import javax.annotation.PostConstruct;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("newIngredientBean")
@ViewScoped
@Getter
@Setter
public class NewIngredientBean implements Serializable {
    private IngredientDTO ingredientDTO;
    private List<IngredientDTO> ingredientDTOList;
    private DataModel<IngredientDTO> ingredientDataModel;


    @PostConstruct
    public void init() {
        this.ingredientDTO = new IngredientDTO();
        this.ingredientDTOList = new ArrayList<>();
        this.ingredientDTOList.add(ingredientDTO);
        this.ingredientDataModel = new ListDataModel<>(this.ingredientDTOList);
    }

    public void create() {
        IngredientDAO ingredientDAO = new IngredientDAO();
        for (IngredientDTO pointerDTO : this.ingredientDTOList) {

            try {
                ingredientDAO.createNewIngredient(pointerDTO);


            } catch (DuplicateException e) {
                e.printStackTrace();
            }

            IngredientSearch.getInstance().add(pointerDTO.getName(), pointerDTO.getIID());

        }
        this.init();
    }


}
