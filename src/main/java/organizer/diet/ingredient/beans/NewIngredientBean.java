package organizer.diet.ingredient.beans;


import organizer.diet.ingredient.daos.IngredientDAO;
import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.diet.nutrients.macronutrient.dtos.CarbDTO;
import organizer.diet.nutrients.macronutrient.dtos.FatDTO;
import organizer.diet.nutrients.macronutrient.dtos.MacroDTO;
import organizer.diet.nutrients.macronutrient.dtos.ProteinDTO;
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

@Named("newIngredient")
@ViewScoped
public class NewIngredientBean implements Serializable {
    private IngredientDTO dto;
    private List<IngredientDTO> ingredientDTOList;
    private DataModel<IngredientDTO> ingredientDataModel;


    @PostConstruct
    public void init() {
        this.dto = new IngredientDTO();
        this.ingredientDTOList = new ArrayList<>();
        this.ingredientDTOList.add(this.dto);
        MacroDTO macroDTO = new MacroDTO();

        FatDTO fatDTO = new FatDTO();
        ProteinDTO proteinDTO = new ProteinDTO();
        CarbDTO carbDTO = new CarbDTO();

        macroDTO.setFatDTO(fatDTO);
        macroDTO.setProteinDTO(proteinDTO);
        macroDTO.setCarbDTO(carbDTO);

        dto.setMacroDTO(macroDTO);

        this.ingredientDataModel = new ListDataModel<>(this.ingredientDTOList);
    }

    public void create() {
        IngredientDAO dao = new IngredientDAO();
        for (IngredientDTO dto : this.ingredientDTOList) {

            try {
                dao.createNewIngredient(dto);


            } catch (DuplicateException e) {
                e.printStackTrace();
            }

            IngredientSearch.getInstance().add(dto.getName(), dto.getiID());
            IngredientSearch.getInstance().addToList(dto.getName(), dto.getiID());
        }
        this.init();
    }


    public IngredientDTO getDto() {
        return dto;
    }

    public void setDto(IngredientDTO dto) {
        this.dto = dto;
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
