package organizer.shopping.list.dtos;

import lombok.Getter;
import lombok.Setter;
import organizer.diet.ingredient.dtos.IngredientDTO;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ShoppingListDTO {

    private LocalDate startDate;
    private LocalDate endDate;


    private List<IngredientDTO> ingredientDTOS;



    public ShoppingListDTO(LocalDate startDate,LocalDate endDate){
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
