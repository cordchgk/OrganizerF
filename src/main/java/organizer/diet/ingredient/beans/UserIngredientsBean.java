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

    private List<IngredientDTO> userIngredients;
    private DataModel userIngredientsDataModel;

    private String searchWord;
    private List<IngredientDTO> results;
    private DataModel<IngredientDTO> resultDataModel;


    @Inject
    private UserBean userBean;

    @PostConstruct
    public void init() {

        IngredientDAO ingredientDAO = new IngredientDAO();
        this.userIngredients = ingredientDAO.getUserIngredients(this.userBean.getDto());
        this.userIngredientsDataModel = new ListDataModel(this.userIngredients);
    }


    public void search() {

        this.results = new ArrayList<>();
        this.resultDataModel = new ListDataModel<>(this.results);

        List<Integer> ids = IngredientSearch.getInstance().search(searchWord);


        if (!ids.isEmpty()) {
            for (Integer i : ids) {

                for (IngredientDTO dto : IngredientSearch.getInstance().getAllIngredients()) {
                    if (i == dto.getIID()) {
                        this.results.add(dto);
                    }
                }
            }
        }
        this.resultDataModel = new ListDataModel<>(this.results);


    }

    public void add() {

        IngredientDTO toAdd = this.resultDataModel.getRowData();

        toAdd.setAmount(100);

        if (!this.contains(toAdd)) {

            IngredientDAO ingredientDAO = new IngredientDAO();


            try {
                ingredientDAO.addToUserIngredients(this.userBean.getDto(), toAdd);
            } catch (DuplicateException e) {
                e.printStackTrace();
            }


            this.userIngredients = ingredientDAO.getUserIngredients(this.userBean.getDto());
            this.userIngredientsDataModel = new ListDataModel(this.userIngredients);

        } else {

            FacesMessage facesMessage = new FacesMessage("You already have that in your list!!");
            FacesContext.getCurrentInstance().addMessage("messages", facesMessage);

        }


    }


    private boolean contains(IngredientDTO ingredientDTO) {


        for (IngredientDTO dto : this.userIngredients) {
            if (ingredientDTO.getIID() == dto.getIID()) {
                return true;
            }
        }
        return false;
    }

    public void changeAmount() {
        IngredientDTO ingredientDTO = (IngredientDTO) this.userIngredientsDataModel.getRowData();
        IngredientDAO ingredientDAO = new IngredientDAO();
        ingredientDAO.updateUserIngredientAmount(this.userBean.getDto(), ingredientDTO);
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
