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

@Named("userMealBean")
@ViewScoped
@Getter
@Setter
public class UserMealsBean implements Serializable {
    private MealDTO n_M_DTO;
    private MealDAO m_DAO;

    List<MealDTO> m_DTO_L;
    DataModel<MealDTO> m_DM;

    @Inject
    UserBean u_Bean;


    @PostConstruct
    public void init() {
        this.m_DAO = new MealDAO();
        this.build();


    }


    public String select() {
        return Utility.getURL() + FaceletPath.MEAL.getPath() + "?mID=" + this.m_DM.getRowData().getMID();
    }

    public void create() {

        m_DAO.createMeal(this.n_M_DTO, this.u_Bean.getDto());

        this.build();
    }


    public void delete() {


        m_DAO.deleteMeal(this.m_DM.getRowData());

        this.build();


    }


    public boolean isEmpty() {
        return !this.m_DTO_L.isEmpty();
    }


    private void build() {
        this.n_M_DTO = new MealDTO();
        this.m_DTO_L = m_DAO.getMealsByUserDTO(this.u_Bean.getDto());
        this.m_DM = new ListDataModel<>(this.m_DTO_L);
    }

}
