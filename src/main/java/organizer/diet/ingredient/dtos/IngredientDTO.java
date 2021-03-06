package organizer.diet.ingredient.dtos;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import organizer.diet.nutrients.macronutrient.dtos.MacroDTO;
import organizer.diet.nutrients.micronutrient.dtos.MicroDTO;
import organizer.system.Utility;
import organizer.system.image.daos.ImageDAO;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class IngredientDTO implements Comparable {
    private int iID;
    private boolean shoppingListIngredient = false;

    private float calories;
    private float amount;
    private float userAmount;

    private String name;
    private String brand;

    private MacroDTO macroDTO;
    private MicroDTO microDTO;

    private List<String> i_U_L;

    private String firstImage;

    private int u_ID;


    public IngredientDTO() {
        this.macroDTO = new MacroDTO();
    }

    public IngredientDTO(int id) {
        this.iID = id;
    }


    public IngredientDTO(String name, String amount) {
        this.name = name;
        this.amount = Float.parseFloat(amount);

    }


    public void createImageUrlList() {
        this.i_U_L = new ImageDAO().getIngredientImagesUrls(this);
        if (!this.i_U_L.isEmpty()) {
            this.firstImage = this.i_U_L.get(0);
        }
    }


    public float getFats() {
        return this.macroDTO.getFats();
    }

    public void setFats(float fats) {
        this.macroDTO.setFats(fats);
    }

    public float getProtein() {
        return this.macroDTO.getProtein();
    }

    public void setProtein(float protein) {
        this.macroDTO.setProtein(protein);
    }

    public float getCarbs() {
        return this.macroDTO.getCarbs();
    }

    public void setCarbs(float carbs) {
        this.macroDTO.setCarbs(carbs);
    }



    @Override
    public int compareTo(@NotNull Object o) {

        IngredientDTO toCompare = (IngredientDTO) o;
        String first = toCompare.getName();
        String second = this.getName();
        if (second.compareTo(first) < 0) {
            return -1;
        } else if (second.equals(first)) {
            return 0;
        }
        return 1;

    }

    @Override
    public int hashCode() {
        return this.iID;
    }

    @Override
    public boolean equals(Object o) {
        IngredientDTO toCompare = (IngredientDTO) o;
        return this.iID == toCompare.iID;
    }


}

