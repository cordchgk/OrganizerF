package organizer.shopping.list.beans;


import lombok.Getter;
import lombok.Setter;
import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.diet.ingredient.dtos.ShoppingListIngredientDTO;
import organizer.diet.system.IngredientSearch;
import organizer.shopping.list.daos.ShoppingListDAO;
import organizer.shopping.list.dtos.ShoppingListDTO;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Named("shoppingListBean")
@ViewScoped
@Getter
@Setter
public class ShoppingListBean implements Serializable {
    private LocalDate startDate;
    private LocalDate endDate;

    private String searchWord;

    private ShoppingListDTO shoppingListDTO;
    private ShoppingListDAO shoppingListDAO;
    private DataModel<IngredientDTO> shoppingListDataModel;
    private List<IngredientDTO> results;
    private DataModel<IngredientDTO> resultDataModel;
    private IngredientDTO newIngredientDTO;

    private List<ShoppingListIngredientDTO> ownAddedIngredients;


    @Inject
    private UserBean userBean;


    @PostConstruct
    public void init() {
        this.startDate = LocalDate.now();
        this.endDate = LocalDate.now().plusDays(7);

        this.newIngredientDTO = new IngredientDTO();

        this.shoppingListDTO = new ShoppingListDTO(this.startDate, this.endDate);
        this.shoppingListDAO = new ShoppingListDAO();
        this.clean();
        this.filter();


    }

    public void add() {


        IngredientDTO toAdd = this.resultDataModel.getRowData();

        if (!this.ownAddedIngredients.contains(toAdd)) {

            this.shoppingListDAO.addToShoppingList(this.userBean.getDto(), toAdd);
            this.clean();
            this.filter();


        } else {

            FacesMessage facesMessage = new FacesMessage("Already in list!");
            FacesContext.getCurrentInstance().addMessage("messages", facesMessage);

        }


    }

    public void update() {
        IngredientDTO toUpdate = this.shoppingListDataModel.getRowData();

        this.shoppingListDAO.updateShoppingListAmount(this.userBean.getDto(), toUpdate);
        this.clean();
    }


    public void remove() {
        IngredientDTO toRemove = this.shoppingListDataModel.getRowData();
        this.shoppingListDAO.removeFromShoppingList(this.userBean.getDto(), toRemove);
        this.clean();
    }

    private void filter() {

        List<IngredientDTO> ingredientDTOList = this.shoppingListDTO.getIngredientDTOS();

        HashMap<IngredientDTO, List<Float>> hashMap = new HashMap();


        for (IngredientDTO ingredientDTO : ingredientDTOList) {
            if (!hashMap.containsKey(ingredientDTO)) {
                hashMap.put(ingredientDTO, new ArrayList<>());
            }
            hashMap.get(ingredientDTO).add(ingredientDTO.getAmount());
        }


        for (IngredientDTO ingredientDTO : hashMap.keySet()) {
            List<Float> current = hashMap.get(ingredientDTO);
            float sum = 0;
            for (Float f : current) {
                sum += f;
            }
            hashMap.put(ingredientDTO, new ArrayList<>());
            hashMap.get(ingredientDTO).add(sum);

        }


        List<IngredientDTO> total = new ArrayList<>();

        for (IngredientDTO ingredientDTO : hashMap.keySet()) {
            ingredientDTO.setAmount(hashMap.get(ingredientDTO).get(0));
            total.add(ingredientDTO);
        }


        this.shoppingListDTO.setIngredientDTOS(total);


    }

    private void clean() {

        this.shoppingListDTO.setIngredientDTOS(shoppingListDAO.getIngredientssByUserDTO(this.userBean.getDto(), this.shoppingListDTO));

        this.ownAddedIngredients = shoppingListDAO.getShoppingListIngredients(this.userBean.getDto());
        this.shoppingListDTO.getIngredientDTOS().addAll(this.ownAddedIngredients);
        this.shoppingListDataModel = new ListDataModel<>(this.shoppingListDTO.getIngredientDTOS());
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

}
