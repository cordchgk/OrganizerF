package organizer.diet.nutrients.macronutrient.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MacroDTO {
    private FatDTO fatDTO;
    private ProteinDTO proteinDTO;
    private CarbDTO carbDTO;


    public MacroDTO() {
        this.fatDTO = new FatDTO();
        this.proteinDTO = new ProteinDTO();
        this.carbDTO = new CarbDTO();
    }



    public float getFats(){
        return this.fatDTO.getAmount();
    }

    public float getProtein() {
        return this.proteinDTO.getAmount();
    }

    public float getCarbs(){
        return this.carbDTO.getAmount();
    }


    public void setFats(float amount){
        this.fatDTO.setAmount(amount);
    }

    public void setProtein(float amount){
        this.proteinDTO.setAmount(amount);

    }


    public void setCarbs(float amount){
        this.carbDTO.setAmount(amount);
    }
}
