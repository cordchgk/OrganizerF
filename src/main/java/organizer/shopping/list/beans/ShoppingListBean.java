package organizer.shopping.list.beans;


import lombok.Getter;
import lombok.Setter;
import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.shopping.list.daos.ShoppingListDAO;
import organizer.shopping.list.dtos.ShoppingListDTO;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
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


    private ShoppingListDTO shoppingListDTO;

    @Inject
    private UserBean userBean;


    @PostConstruct
    public void init() {
        this.startDate = LocalDate.now();

        this.endDate = LocalDate.now().plusDays(7);

        ShoppingListDAO shoppingListDAO = new ShoppingListDAO();
        this.shoppingListDTO = new ShoppingListDTO(this.startDate, this.endDate);

        this.shoppingListDTO.setIngredientDTOS(shoppingListDAO.getIngredientssByUserDTO(this.userBean.getDto(), this.shoppingListDTO));
        this.filter();

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


}
