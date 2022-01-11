package organizer.diet.meal.beans;

import organizer.diet.ingredient.daos.IngredientDAO;
import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.diet.meal.daos.MealDAO;
import organizer.diet.meal.dtos.MealDTO;
import organizer.diet.system.IngredientSearch;
import organizer.system.exceptions.DuplicateException;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.faces.application.ConfigurableNavigationHandler;
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
import java.util.Map;


@Named("mealBean")
@ViewScoped
public class MealBean implements Serializable {
    private boolean isAllowed = false;

    private String searchWord;

    private MealDTO mealDTO;

    private List<IngredientDTO> results;

    private DataModel<IngredientDTO> ingredientsDataModel;
    private DataModel<IngredientDTO> resultDataModel;

    @Inject
    UserBean userBean;

    @PostConstruct
    public void init() {
        Map<String, String> parameter = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        this.isAllowed = true;

        this.mealDTO = new MealDTO();

        this.mealDTO.setmID(Integer.parseInt(parameter.get("mID")));

        MealDAO dao = new MealDAO();
        this.mealDTO = dao.getMealDTO(mealDTO);

        this.mealDTO.calculateCalories();

        Collections.sort(this.mealDTO.getMealIngredients());

        this.ingredientsDataModel = new ListDataModel<>(this.mealDTO.getMealIngredients());

    }

    public void add() {

        IngredientDTO toAdd = this.resultDataModel.getRowData();

        toAdd.setAmount(100);

        if (!this.contains(toAdd)) {

            IngredientDAO dao = new IngredientDAO();
            try {
                dao.addIngredientToMeal(this.mealDTO, toAdd);
            } catch (DuplicateException e) {
                e.printStackTrace();
            }
            MealDAO mealDAO = new MealDAO();


            this.mealDTO = mealDAO.getMealDTO(mealDTO);

            this.mealDTO.calculateCalories();

            this.ingredientsDataModel = new ListDataModel<>(this.mealDTO.getMealIngredients());

        } else {

            FacesMessage facesMessage = new FacesMessage("Already in Meal!");
            FacesContext.getCurrentInstance().addMessage("messages", facesMessage);

        }


    }

    public void updateIngredientAmount() {
        MealDAO mealDAO = new MealDAO();
        mealDAO.updateIngredientAmount(this.mealDTO, ingredientsDataModel.getRowData());

        this.mealDTO = mealDAO.getMealDTO(this.mealDTO);

        this.mealDTO.calculateCalories();

        Collections.sort(this.mealDTO.getMealIngredients());

        this.ingredientsDataModel = new ListDataModel<>(this.mealDTO.getMealIngredients());


    }


    public void removeIngredient() {
        MealDAO mealDAO = new MealDAO();

        mealDAO.deleteIngredientFromMeal(this.mealDTO, ingredientsDataModel.getRowData());

        this.mealDTO = mealDAO.getMealDTO(this.mealDTO);

        this.mealDTO.calculateCalories();

        this.ingredientsDataModel = new ListDataModel<>(this.mealDTO.getMealIngredients());

    }

    public void search() {

        this.results = new ArrayList<>();
        this.resultDataModel = new ListDataModel<>(this.results);

        List<Integer> ids = IngredientSearch.getInstance().search(searchWord);


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


    public void isAllowed() {

        if (!this.isAllowed) {
            Map<String, String> parameter = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();



            MealDAO mealDAO = new MealDAO();
            List<Integer> mealDTOS = mealDAO.getIDsForAccess(this.userBean.getDto());


            if (!mealDTOS.contains(Integer.parseInt(parameter.get("mID")))) {
                ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) FacesContext.getCurrentInstance().getApplication()
                        .getNavigationHandler();
                nav.performNavigation("/access/access-denied.xhtml");
            }

        }


    }

    private boolean contains(IngredientDTO ingredientDTO) {


        for (IngredientDTO dto : this.mealDTO.getMealIngredients()) {
            if (ingredientDTO.getiID() == dto.getiID()) {
                return true;
            }
        }
        return false;
    }


    public void setAllowed(boolean allowed) {
        isAllowed = allowed;
    }

    public String getSearchWord() {
        return searchWord;
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }

    public MealDTO getMealDTO() {
        return mealDTO;
    }

    public void setMealDTO(MealDTO mealDTO) {
        this.mealDTO = mealDTO;
    }

    public List<IngredientDTO> getResults() {
        return results;
    }

    public void setResults(List<IngredientDTO> results) {
        this.results = results;
    }

    public DataModel<IngredientDTO> getIngredientsDataModel() {
        return ingredientsDataModel;
    }

    public void setIngredientsDataModel(DataModel<IngredientDTO> ingredientsDataModel) {
        this.ingredientsDataModel = ingredientsDataModel;
    }

    public DataModel<IngredientDTO> getResultDataModel() {
        return resultDataModel;
    }

    public void setResultDataModel(DataModel<IngredientDTO> resultDataModel) {
        this.resultDataModel = resultDataModel;
    }
}
