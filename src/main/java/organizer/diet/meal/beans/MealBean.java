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
 * CDI Bean providing methods for managing a meal
 *
 *
 * @author cordch
 */
@Named("mealBean")
@ViewScoped
@Getter
@Setter
public class MealBean implements Serializable {
    private boolean isAllowed = false;

    private MealDTO mealDTO;
    private String searchWord;
    private List<IngredientDTO> results;
    private DataModel<IngredientDTO> resultsDataModel;
    private DataModel<IngredientDTO> ingredientDataModel;

    private MealDAO mealDAO;

    @Inject
    UserBean userBean;

    @PostConstruct
    public void init() {
        this.mealDTO = new MealDTO();
        this.mealDAO = new MealDAO();
        this.searchWord = "";
        this.mealDTO.setMID(Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap().get("mID")));

        this.isAllowed = true;

        this.build();

    }


    /**
     * Adds ingredient from the search result list to the meal,
     * updates the database and rebuilds the DataModel for the current page
     */
    public void add() {

        IngredientDTO to_Add = this.resultsDataModel.getRowData();

        to_Add.setAmount(100);

        if (!this.contains(to_Add)) {

            IngredientDAO i_DAO = new IngredientDAO();
            try {
                i_DAO.addIngredientToMeal(this.mealDTO, to_Add);
            } catch (DuplicateException e) {
                e.printStackTrace();
            }
            this.build();

        } else {

            FacesMessage facesMessage = new FacesMessage("Already in Meal!");
            FacesContext.getCurrentInstance().addMessage("messages", facesMessage);

        }


    }

    /**
     * Upgrades the selected ingredient's amount and rebuilds the DataModel
     *
     */
    public void updateIngredientAmount() {

        mealDAO.updateIngredientAmount(this.mealDTO, ingredientDataModel.getRowData());

        this.build();

    }

    /**
     * Removes the selected ingredient from the meal and rebuilds the DataModel
     */
    public void removeIngredient() {


        mealDAO.deleteIngredientFromMeal(this.mealDTO, ingredientDataModel.getRowData());

        this.build();

    }


    //TODO searchBean
    public void search() {

        this.results = new ArrayList<>();
        this.resultsDataModel = new ListDataModel<>(this.results);

        List<Integer> ids = IngredientSearch.getInstance().search(searchWord);


        if (!ids.isEmpty()) {
            for (Integer i : ids) {

                for (IngredientDTO dto : IngredientSearch.getInstance().getI_L()) {
                    if (i == dto.getIID()) {
                        this.results.add(dto);
                    }
                }
            }
        }
        this.resultsDataModel = new ListDataModel<>(this.results);


    }

    /**
     * Checks if the ingredient {@param IngredientDTO} is alrady in the meal
     *
     * @param i_DTO Ingredient to check if it is already in the meal
     * @return true if ingredient is already in the meal
     */
    private boolean contains(IngredientDTO i_DTO) {


        for (IngredientDTO pointer_DTO : this.mealDTO.getMealIngredients()) {
            if (i_DTO.getIID() == pointer_DTO.getIID()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Loads the meal from the database,calculates the calories,sort the ingredient list
     * and build the DataModel
     */
    private void build() {
        this.mealDTO = mealDAO.getMealDTO(mealDTO);
        this.mealDTO.calculateCalories();
        Collections.sort(this.mealDTO.getMealIngredients());
        this.ingredientDataModel = new ListDataModel<>(this.mealDTO.getMealIngredients());
    }


}
