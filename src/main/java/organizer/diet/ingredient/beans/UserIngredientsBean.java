package organizer.diet.ingredient.beans;


import lombok.Getter;
import lombok.Setter;
import organizer.diet.ingredient.daos.IngredientDAO;
import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.diet.system.IngredientSearch;
import organizer.system.exceptions.DuplicateException;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("userIngredientBean")
@ViewScoped
@Getter
@Setter
public class UserIngredientsBean implements Serializable {

    private List<IngredientDTO> u_I_L;
    private DataModel u_I_DM;

    private String s_W;
    private List<IngredientDTO> s_R_L;
    private DataModel<IngredientDTO> s_R_DM;

    private IngredientDAO i_DAO;


    @Inject
    private UserBean userBean;

    @PostConstruct
    public void init() {

        this.i_DAO = new IngredientDAO();
        this.build();
    }


    public void search() {


        this.s_R_L = new ArrayList<>();
        this.s_R_DM = new ListDataModel<>(this.s_R_L);

        List<Integer> ids = IngredientSearch.getInstance().search(s_W);


        if (!ids.isEmpty()) {
            for (Integer i : ids) {

                for (IngredientDTO to_Add : IngredientSearch.getInstance().getI_L()) {
                    if (i == to_Add.getIID()) {
                        this.s_R_L.add(to_Add);
                    }
                }
            }
        }
        this.s_R_DM = new ListDataModel<>(this.s_R_L);


    }

    public void add() {

        IngredientDTO to_Add = this.s_R_DM.getRowData();

        to_Add.setAmount(100);

        if (!this.contains(to_Add)) {


            try {
                i_DAO.addToUserIngredients(this.userBean.getDto(), to_Add);
            } catch (DuplicateException e) {
                e.printStackTrace();
            }


            this.build();

        } else {

            FacesMessage facesMessage = new FacesMessage("You already have that in your list!");
            FacesContext.getCurrentInstance().addMessage("messages", facesMessage);

        }


    }


    private boolean contains(IngredientDTO i_DTO) {


        for (IngredientDTO pointer_DTO : this.u_I_L) {
            if (i_DTO.getIID() == pointer_DTO.getIID()) {
                return true;
            }
        }
        return false;
    }

    public void changeAmount() {
        IngredientDTO i_DTO = (IngredientDTO) this.u_I_DM.getRowData();

        i_DAO.updateUserIngredientAmount(this.userBean.getDto(), i_DTO);

        this.build();
    }


    private void build() {
        this.u_I_L = i_DAO.getUserIngredients(this.userBean.getDto());
        this.u_I_DM = new ListDataModel(this.u_I_L);
    }
}
