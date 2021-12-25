package organizer.diet.nutrients.macronutrient.dtos;

public class MacroDTO {
    private ProteinDTO proteinDTO;
    private FatDTO fatDTO;
    private CarbDTO carbDTO;


    public ProteinDTO getProteinDTO() {
        return proteinDTO;
    }

    public void setProteinDTO(ProteinDTO proteinDTO) {
        this.proteinDTO = proteinDTO;
    }

    public FatDTO getFatDTO() {
        return fatDTO;
    }

    public void setFatDTO(FatDTO fatDTO) {
        this.fatDTO = fatDTO;
    }

    public CarbDTO getCarbDTO() {
        return carbDTO;
    }

    public void setCarbDTO(CarbDTO carbDTO) {
        this.carbDTO = carbDTO;
    }
}
