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

@ViewScoped
@Named("i_Bean")
@Getter
@Setter
public class IngredientBean implements Serializable {


    private IngredientDTO i_DTO;
    private IngredientDAO i_DAO;

    private List<IngredientDTO> i_DTO_L;
    private DataModel<IngredientDTO> i_DTO_DM;

    @Inject
    private UserBean u_Bean;


    @PostConstruct
    public void init() {
        int id = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("iID"));

        this.i_DTO = new IngredientDTO(id);
        this.i_DAO = new IngredientDAO();
        this.i_DTO = i_DAO.getIngredientByDTO(this.i_DTO);
        this.i_DTO_L = new ArrayList<IngredientDTO>();
        this.i_DTO_L.add(this.i_DTO);
        this.i_DTO_DM = new ListDataModel<>(this.i_DTO_L);



    }


    public boolean isUsersIngredient() {
        return this.i_DTO.getU_ID() == this.u_Bean.getDto().getUserID();
    }


}
