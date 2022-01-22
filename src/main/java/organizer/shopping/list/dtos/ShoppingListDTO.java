package organizer.shopping.list.dtos;

import lombok.Getter;
import lombok.Setter;
import organizer.diet.ingredient.dtos.IngredientDTO;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ShoppingListDTO {

    private LocalDate s_D;
    private LocalDate e_D;


    private List<IngredientDTO> i_DTO_L;



    public ShoppingListDTO(LocalDate s_D, LocalDate e_D){
        this.s_D = s_D;
        this.e_D = e_D;

    }

}
