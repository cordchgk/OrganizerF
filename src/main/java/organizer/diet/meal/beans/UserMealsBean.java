package organizer.diet.meal.beans;


import lombok.Getter;
import lombok.Setter;
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


/**
 *
 * CDI Bean providing basic methods for managing the user's meals
 */
@Named("userMealBean")
@ViewScoped
@Getter
@Setter
public class UserMealsBean implements Serializable {
    private MealDTO newMealDTO;
    private MealDAO mealDAO;

    List<MealDTO> userMeals;
    DataModel<MealDTO> mealsDataModel;

    @Inject
    UserBean u_Bean;


    @PostConstruct
    public void init() {
        this.mealDAO = new MealDAO();
        this.build();


    }


    public String select() {
        return Utility.getURL() + FaceletPath.MEAL.getPath() + "?mID=" + this.mealsDataModel.getRowData().getMID();
    }

    public void create() {

        mealDAO.createMeal(this.newMealDTO, this.u_Bean.getU_DTO());

        this.build();
    }


    public void delete() {


        mealDAO.deleteMeal(this.mealsDataModel.getRowData());

        this.build();


    }


    public boolean isEmpty() {
        return !this.userMeals.isEmpty();
    }


    private void build() {
        this.newMealDTO = new MealDTO();
        this.userMeals = mealDAO.getMealsByUserDTO(this.u_Bean.getU_DTO());
        this.mealsDataModel = new ListDataModel<>(this.userMeals);
    }

}
