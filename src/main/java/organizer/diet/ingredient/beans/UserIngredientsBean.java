package organizer.diet.ingredient.beans;


import lombok.Getter;
import lombok.Setter;
import organizer.diet.ingredient.daos.IngredientDAO;
import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.diet.system.IngredientSearch;
import organizer.system.exceptions.DuplicateException;
import organizer.system.localization.LocalConfig;
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
import java.util.Collections;
import java.util.List;

/**
 * CDI BEAN providing methods for the user to manage his own ingredients
 */

@Named("userIngredientBean")
@ViewScoped
@Getter
@Setter
public class UserIngredientsBean implements Serializable {

    private List<IngredientDTO> userIngredients;
    private DataModel userIngredientsDataModel;
    private String searchWord;
    private List<IngredientDTO> results;
    private DataModel<IngredientDTO> resultsDataModel;

    private IngredientDAO ingredientDAO;


    @Inject
    private UserBean userBean;

    @PostConstruct
    public void init() {
        this.ingredientDAO = new IngredientDAO();
        this.build();
    }




    //TODO create own search class

    /**
     * Searches for the string which is put in {@String s_W} (in the current search TRIE)
     * and populates the search result DataModel
     *
     */
    public void search() {
        this.results = new ArrayList<>();
        this.resultsDataModel = new ListDataModel<>(this.results);

        List<Integer> ids = IngredientSearch.getInstance().search(searchWord);
        if (!ids.isEmpty()) {
            for (Integer i : ids) {

                for (IngredientDTO to_Add : IngredientSearch.getInstance().getI_L()) {
                    if (i == to_Add.getIID()) {
                        this.results.add(to_Add);
                    }
                }
            }
        }
        this.resultsDataModel = new ListDataModel<>(this.results);
    }


    /**
     * Adds the selected ingredient to the users ingredients or produces a FacesMessage
     * if the selected ingredient is already on the user's ingredient list
     *
     */
    public void add() {
        IngredientDTO to_Add = this.resultsDataModel.getRowData();

        to_Add.setAmount(100);

        if (!this.contains(to_Add)) {

            try {
                ingredientDAO.addToUserIngredients(this.userBean.getU_DTO(), to_Add);
            } catch (DuplicateException e) {
                e.printStackTrace();
            }


            this.build();

        } else {
            String msg = LocalConfig.getEntryForMessages("youalreadyhavethatonyourlist", this.userBean);
            FacesMessage facesMessage = new FacesMessage(msg);
            FacesContext.getCurrentInstance().addMessage("messages", facesMessage);

        }


    }

    /**
     * Remnoves the selected ingredient from the user's ingredient list
     */
    public void remove() {
        IngredientDTO to_Remove = (IngredientDTO) this.userIngredientsDataModel.getRowData();
        this.ingredientDAO.removeFromUserIngredients(this.userBean.getU_DTO(), to_Remove);
        this.build();
    }


    private boolean contains(IngredientDTO i_DTO) {

        for (IngredientDTO pointer_DTO : this.userIngredients) {
            if (i_DTO.getIID() == pointer_DTO.getIID()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Changes amount of the selected ingredient
     */
    public void changeAmount() {
        IngredientDTO i_DTO = (IngredientDTO) this.userIngredientsDataModel.getRowData();

        ingredientDAO.updateUserIngredientAmount(this.userBean.getU_DTO(), i_DTO);

        this.build();
    }

    /**
     * Creates and populates the user's ingredient DataModel
     */
    private void build() {
        this.userIngredients = ingredientDAO.getUserIngredients(this.userBean.getU_DTO());
        Collections.sort(this.userIngredients);
        this.userIngredientsDataModel = new ListDataModel(this.userIngredients);
    }


}
