package organizer.diet.meal.beans;


import organizer.diet.meal.daos.MealDAO;
import organizer.diet.meal.dtos.MealDTO;
import organizer.system.Utility;
import organizer.system.enums.FaceletPath;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("userMealBean")
@ViewScoped
public class UserMealsBean implements Serializable {
    private MealDTO newMealDTO;

    List<MealDTO> mealDTOs;
    DataModel<MealDTO> mealDataModel;

    @Inject
    UserBean userBean;


    @PostConstruct
    public void init() {
        MealDAO mealDAO = new MealDAO();
        this.newMealDTO = new MealDTO();

        this.mealDTOs = mealDAO.getMealsByUserDTO(this.userBean.getDto());
        this.mealDataModel = new ListDataModel<>(this.mealDTOs);


    }


    public String select() {
        return Utility.getURL() + FaceletPath.MEAL.getPath() + "?mID=" + this.mealDataModel.getRowData().getmID();
    }

    public void create() {
        MealDAO mealDAO = new MealDAO();
        mealDAO.createMeal(this.newMealDTO, this.userBean.getDto());

        this.mealDTOs = mealDAO.getMealsByUserDTO(this.userBean.getDto());

        this.newMealDTO = new MealDTO();
        this.mealDataModel = new ListDataModel<>(this.mealDTOs);
    }


    public void delete() {
        MealDAO mealDAO = new MealDAO();

        mealDAO.deleteMeal(this.mealDataModel.getRowData());

        this.mealDTOs = mealDAO.getMealsByUserDTO(this.userBean.getDto());
        this.mealDataModel = new ListDataModel<>(this.mealDTOs);


    }


    public boolean isEmpty() {
        return !this.mealDTOs.isEmpty();
    }


    public MealDTO getNewMealDTO() {
        return newMealDTO;
    }

    public void setNewMealDTO(MealDTO newMealDTO) {
        this.newMealDTO = newMealDTO;
    }

    public List<MealDTO> getMealDTOs() {
        return mealDTOs;
    }

    public void setMealDTOs(List<MealDTO> mealDTOs) {
        this.mealDTOs = mealDTOs;
    }

    public DataModel<MealDTO> getMealDataModel() {
        return mealDataModel;
    }

    public void setMealDataModel(DataModel<MealDTO> mealDataModel) {
        this.mealDataModel = mealDataModel;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
}
