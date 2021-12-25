package organizer.diet.meal.beans;

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
import java.util.List;
import java.util.Map;


@Named("meal")
@ViewScoped
public class MealBean implements Serializable {
    @Inject
    UserBean userBean;
    private MealDTO dto;
    private DataModel<IngredientDTO> ingredientsDataModel;
    private String word;
    private List<IngredientDTO> results;
    private DataModel<IngredientDTO> resultDataModel;

    @PostConstruct
    public void init() {
        Map<String, String> parameter = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        int mID = Integer.parseInt(parameter.get("mID"));
        this.dto = new MealDTO();
        this.dto.setmID(mID);
        MealDAO dao = new MealDAO();
        this.dto = dao.returnMealDTO(dto);


        this.dto.calculateCalories();


        this.ingredientsDataModel = new ListDataModel<>(this.dto.getMealIngredients());

    }


    public void search() {
        this.resultDataModel = new ListDataModel<>(this.results);
        this.results = new ArrayList<>();

        List<Integer> ids = IngredientSearch.getInstance().search(word);


        if (!ids.isEmpty()) {
            for (Integer i : ids) {

                for (IngredientDTO dto : IngredientSearch.getInstance().getAllIngredients()) {
                    if (i == dto.getiID()) {
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

            IngredientDAO dao = new IngredientDAO();
            try {
                dao.addIngredientToMeal(this.dto, toAdd);
            } catch (DuplicateException e) {
                e.printStackTrace();
            }
            MealDAO mealDAO = new MealDAO();


            this.dto = mealDAO.returnMealDTO(dto);


            this.dto.calculateCalories();


            this.ingredientsDataModel = new ListDataModel<>(this.dto.getMealIngredients());

        } else {

            FacesMessage facesMessage = new FacesMessage("Already in Meal!");
            FacesContext.getCurrentInstance().addMessage("messages", facesMessage);
        }


    }


    private boolean contains(IngredientDTO dto) {


        for (IngredientDTO dto1 : this.dto.getMealIngredients()) {
            if (dto.getiID() == dto1.getiID()) {
                return true;
            }
        }
        return false;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<IngredientDTO> getResults() {
        return results;
    }

    public void setResults(List<IngredientDTO> results) {
        this.results = results;
    }

    public DataModel<IngredientDTO> getResultDataModel() {
        return resultDataModel;
    }

    public void setResultDataModel(DataModel<IngredientDTO> resultDataModel) {
        this.resultDataModel = resultDataModel;
    }


    public MealDTO getDto() {
        return dto;
    }

    public void setDto(MealDTO dto) {
        this.dto = dto;
    }

    public DataModel<IngredientDTO> getIngredientsDataModel() {
        return ingredientsDataModel;
    }

    public void setIngredientsDataModel(DataModel<IngredientDTO> ingredientsDataModel) {
        this.ingredientsDataModel = ingredientsDataModel;
    }


}
