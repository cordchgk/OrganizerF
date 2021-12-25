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

@Named("usermeals")
@ViewScoped
public class UserMealsBean implements Serializable {
    List<MealDTO> mealDTOs;
    DataModel<MealDTO> mealDataModel;
    @Inject
    UserBean userBean;
    private MealDTO newMealDTO;

    @PostConstruct
    public void init() {
        MealDAO mealDAO = new MealDAO();
        this.mealDTOs = mealDAO.selectByDto(this.userBean.getDto());
        this.mealDataModel = new ListDataModel<>(this.mealDTOs);
        this.newMealDTO = new MealDTO();
        for (MealDTO dto : this.mealDTOs) {
            dto.calculateCalories();
        }

    }

    public DataModel<MealDTO> getMealDataModel() {
        return mealDataModel;
    }

    public void setMealDataModel(DataModel<MealDTO> mealDataModel) {
        this.mealDataModel = mealDataModel;
    }


    public String select() {
        MealDTO dto = this.mealDataModel.getRowData();
        int ID;
        ID = dto.getmID();

        return Utility.getURL() + FaceletPath.MEAL.getPath() + "?mID=" + ID;
    }

    public void create() {
        MealDAO mealDAO = new MealDAO();
        mealDAO.createMeal(this.newMealDTO, this.userBean.getDto());

        this.mealDTOs = mealDAO.selectByDto(this.userBean.getDto());
        this.mealDataModel = new ListDataModel<>(this.mealDTOs);

        this.newMealDTO = new MealDTO();
    }


    public void delete() {
        MealDAO mealDAO = new MealDAO();

        MealDTO current = this.mealDataModel.getRowData();


        mealDAO.deleteMeal(current);

        this.mealDTOs = mealDAO.selectByDto(this.userBean.getDto());
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
}
