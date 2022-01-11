package organizer.shopping.list.beans;


import lombok.Getter;
import lombok.Setter;
import organizer.shopping.list.daos.ShoppingListDAO;
import organizer.shopping.list.dtos.ShoppingListDTO;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;

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
    public void init(){
        this.startDate = LocalDate.now();

        this.endDate = LocalDate.now().plusDays(7);

        ShoppingListDAO shoppingListDAO = new ShoppingListDAO();
        this.shoppingListDTO = new ShoppingListDTO(this.startDate,this.endDate);

        this.shoppingListDTO.setIngredientDTOS(shoppingListDAO.getIngredientssByUserDTO(this.userBean.getDto(), this.shoppingListDTO));

    }

}
