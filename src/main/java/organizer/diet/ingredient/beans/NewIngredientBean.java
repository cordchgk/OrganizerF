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

@Named("newIngredientBean")
@ViewScoped
@Getter
@Setter

public class NewIngredientBean implements Serializable {
    private IngredientDTO i_DTO;
    private List<IngredientDTO> i_DTO_L;
    private DataModel<IngredientDTO> i_DM;
    private IngredientDAO i_DAO;


    @Inject
    private UserBean u_Bean;

    @PostConstruct
    public void init() {
        this.build();
    }

    public void create() {

        for (IngredientDTO pointerDTO : this.i_DTO_L) {

            try {
                this.i_DAO.createNewIngredient(pointerDTO, u_Bean.getDto());


            } catch (DuplicateException e) {
                e.printStackTrace();
            }

            IngredientSearch.getInstance().add(pointerDTO.getName(), pointerDTO.getIID());

        }
        this.build();
    }


    private void build() {
        this.i_DTO = new IngredientDTO();
        this.i_DTO_L = new ArrayList<>();
        this.i_DTO_L.add(i_DTO);
        this.i_DM = new ListDataModel<>(this.i_DTO_L);
        this.i_DAO = new IngredientDAO();
    }

}
