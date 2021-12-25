package organizer.diet.ingredient.dtos;

import organizer.diet.nutrients.macronutrient.dtos.MacroDTO;
import organizer.diet.nutrients.micronutrient.dtos.MicroDTO;

public class IngredientDTO {
    private MacroDTO macroDTO;
    private MicroDTO microDTO;
    private float calories;
    private float amount;
    private String name;
    private int iID;
    private String brand;

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

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getiID() {
        return iID;
    }

    public void setiID(int iID) {
        this.iID = iID;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }


}

