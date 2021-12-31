package organizer.diet.ingredient.beans;


import organizer.diet.ingredient.daos.IngredientDAO;
import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("userIngredientBean")
@ViewScoped
public class UserIngredientsBean implements Serializable {

    private List<IngredientDTO> userIngredients;
    private DataModel userIngredientsDataModel;

    @Inject
    private UserBean userBean;

    @PostConstruct
    public void init() {

        IngredientDAO ingredientDAO = new IngredientDAO();
        this.userIngredients = ingredientDAO.getUserIngredients(this.userBean.getDto());
        this.userIngredientsDataModel = new ListDataModel(this.userIngredients);
    }


    public List<IngredientDTO> getUserIngredients() {
        return userIngredients;
    }

    public void setUserIngredients(List<IngredientDTO> userIngredients) {
        this.userIngredients = userIngredients;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }


    public DataModel getUserIngredientsDataModel() {
        return userIngredientsDataModel;
    }

    public void setUserIngredientsDataModel(DataModel userIngredientsDataModel) {
        this.userIngredientsDataModel = userIngredientsDataModel;
    }
}
