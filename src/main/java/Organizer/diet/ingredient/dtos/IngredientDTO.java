package organizer.diet.ingredient.dtos;

import org.jetbrains.annotations.NotNull;
import organizer.diet.nutrients.macronutrient.dtos.MacroDTO;
import organizer.diet.nutrients.micronutrient.dtos.MicroDTO;
import organizer.system.Utility;

public class IngredientDTO implements Comparable {
    private int iID;


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

    public int getiID() {
        return iID;
    }

    public void setiID(int iID) {
        this.iID = iID;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public MacroDTO getMacroDTO() {
        return macroDTO;
    }

    public void setMacroDTO(MacroDTO macroDTO) {
        this.macroDTO = macroDTO;
    }

    public MicroDTO getMicroDTO() {
        return microDTO;
    }

    public void setMicroDTO(MicroDTO microDTO) {
        this.microDTO = microDTO;
    }

    public float getUserAmount() {
        return userAmount;
    }

    public void setUserAmount(float userAmount) {
        this.userAmount = userAmount;
    }
}

