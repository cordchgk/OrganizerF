package organizer.diet.meal.beans;

import lombok.Getter;
import lombok.Setter;
import organizer.diet.ingredient.daos.IngredientDAO;
import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.diet.meal.daos.MealDAO;
import organizer.diet.meal.dtos.MealDTO;
import organizer.diet.system.IngredientSearch;
import organizer.system.exceptions.DuplicateException;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Named("mealBean")
@ViewScoped
@Getter
@Setter
public class MealBean implements Serializable {
    private boolean isAllowed = false;


    private MealDTO m_DTO;
    private String s_W;
    private List<IngredientDTO> r_L;
    private DataModel<IngredientDTO> r_DM;
    private DataModel<IngredientDTO> i_DM;

    private MealDAO m_DAO;

    @Inject
    UserBean u_Bean;

    @PostConstruct
    public void init() {
        this.m_DTO = new MealDTO();
        this.m_DAO = new MealDAO();
        this.s_W = "";
        this.m_DTO.setMID(Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap().get("mID")));

        this.isAllowed = true;

        this.build();

    }


    public void add() {

        IngredientDTO to_Add = this.r_DM.getRowData();

        to_Add.setAmount(100);

        if (!this.contains(to_Add)) {

            IngredientDAO i_DAO = new IngredientDAO();
            try {
                i_DAO.addIngredientToMeal(this.m_DTO, to_Add);
            } catch (DuplicateException e) {
                e.printStackTrace();
            }
            this.build();

        } else {

            FacesMessage facesMessage = new FacesMessage("Already in Meal!");
            FacesContext.getCurrentInstance().addMessage("messages", facesMessage);

        }


    }

    public void updateIngredientAmount() {

        m_DAO.updateIngredientAmount(this.m_DTO, i_DM.getRowData());

        this.build();


    }


    public void removeIngredient() {


        m_DAO.deleteIngredientFromMeal(this.m_DTO, i_DM.getRowData());

        this.build();

    }

    public void search() {

        this.r_L = new ArrayList<>();
        this.r_DM = new ListDataModel<>(this.r_L);

        List<Integer> ids = IngredientSearch.getInstance().search(s_W);


        if (!ids.isEmpty()) {
            for (Integer i : ids) {

                for (IngredientDTO dto : IngredientSearch.getInstance().getI_L()) {
                    if (i == dto.getIID()) {
                        this.r_L.add(dto);
                    }
                }
            }
        }
        this.r_DM = new ListDataModel<>(this.r_L);


    }


    private boolean contains(IngredientDTO i_DTO) {


        for (IngredientDTO pointer_DTO : this.m_DTO.getMealIngredients()) {
            if (i_DTO.getIID() == pointer_DTO.getIID()) {
                return true;
            }
        }
        return false;
    }


    private void build() {
        this.m_DTO = m_DAO.getMealDTO(m_DTO);

        this.m_DTO.calculateCalories();
        Collections.sort(this.m_DTO.getMealIngredients());
        this.i_DM = new ListDataModel<>(this.m_DTO.getMealIngredients());
    }


}
