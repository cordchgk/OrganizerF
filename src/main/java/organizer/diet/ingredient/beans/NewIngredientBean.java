package organizer.diet.ingredient.beans;


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
public class NewIngredientBean implements Serializable {
    private IngredientDTO ingredientDTO;
    private List<IngredientDTO> ingredientDTOList;
    private DataModel<IngredientDTO> ingredientDataModel;


    @PostConstruct
    public void init() {
        this.ingredientDTO = new IngredientDTO();
        this.ingredientDTOList = new ArrayList<>();
        this.ingredientDTOList.add(this.ingredientDTO);
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

            IngredientSearch.getInstance().add(pointerDTO.getName(), pointerDTO.getiID());

        }
        this.init();
    }


    public IngredientDTO getIngredientDTO() {
        return ingredientDTO;
    }

    public void setIngredientDTO(IngredientDTO ingredientDTO) {
        this.ingredientDTO = ingredientDTO;
    }

    public List<IngredientDTO> getIngredientDTOList() {
        return ingredientDTOList;
    }

    public void setIngredientDTOList(List<IngredientDTO> ingredientDTOList) {
        this.ingredientDTOList = ingredientDTOList;
    }

    public DataModel<IngredientDTO> getIngredientDataModel() {
        return ingredientDataModel;
    }

    public void setIngredientDataModel(DataModel<IngredientDTO> ingredientDataModel) {
        this.ingredientDataModel = ingredientDataModel;
    }
}
