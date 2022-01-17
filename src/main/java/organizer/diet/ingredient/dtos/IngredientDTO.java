package organizer.diet.ingredient.dtos;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import organizer.diet.nutrients.macronutrient.dtos.MacroDTO;
import organizer.diet.nutrients.micronutrient.dtos.MicroDTO;
import organizer.system.Utility;

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


    public IngredientDTO() {
        this.macroDTO = new MacroDTO();
    }


    public IngredientDTO(String name, String amount) {
        this.name = name;
        this.amount = Float.parseFloat(amount);

    }


    public float getFats() {
        return this.macroDTO.getFatDTO().getAmount();
    }

    public void setFats(float fats) {
        this.macroDTO.getFatDTO().setAmount(fats);
    }

    public float getProtein() {
        return this.macroDTO.getProteinDTO().getAmount();
    }

    public void setProtein(float protein) {
        this.macroDTO.getProteinDTO().setAmount(protein);
    }

    public float getCarbs() {
        return this.macroDTO.getCarbDTO().getAmount();
    }

    public void setCarbs(float carbs) {
        this.macroDTO.getCarbDTO().setAmount(carbs);
    }

    public float actualFats() {
        return Utility.twoDecimals(this.macroDTO.getFatDTO().getAmount() * this.percentage());

    }

    public float actualProtein() {
        return Utility.twoDecimals(this.macroDTO.getProteinDTO().getAmount() * this.percentage());
    }

    public float actualCarbs() {
        return Utility.twoDecimals(this.macroDTO.getCarbDTO().getAmount() * this.percentage());
    }

    public float actualCalories() {
        return Utility.twoDecimals(this.calories * this.percentage());

    }

    private float percentage() {
        return this.amount / 100;
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

    public String toCSV() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.name + ", ");
        stringBuilder.append(this.amount + ", ");
        return stringBuilder.toString();
    }
}

