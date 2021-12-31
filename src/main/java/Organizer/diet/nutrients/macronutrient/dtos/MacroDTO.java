package organizer.diet.nutrients.macronutrient.dtos;

public class MacroDTO {
    private FatDTO fatDTO;
    private ProteinDTO proteinDTO;
    private CarbDTO carbDTO;


    public MacroDTO() {
        this.fatDTO = new FatDTO();
        this.proteinDTO = new ProteinDTO();
        this.carbDTO = new CarbDTO();
    }


    public FatDTO getFatDTO() {
        return fatDTO;
    }

    public void setFatDTO(FatDTO fatDTO) {
        this.fatDTO = fatDTO;
    }

    public ProteinDTO getProteinDTO() {
        return proteinDTO;
    }

    public void setProteinDTO(ProteinDTO proteinDTO) {
        this.proteinDTO = proteinDTO;
    }

    public CarbDTO getCarbDTO() {
        return carbDTO;
    }

    public void setCarbDTO(CarbDTO carbDTO) {
        this.carbDTO = carbDTO;
    }
}
