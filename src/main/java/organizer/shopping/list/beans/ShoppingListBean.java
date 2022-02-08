package organizer.shopping.list.beans;


import lombok.Getter;
import lombok.Setter;
import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.diet.ingredient.dtos.ShoppingListIngredientDTO;
import organizer.diet.system.IngredientSearch;
import organizer.shopping.list.daos.ShoppingListDAO;
import organizer.shopping.list.dtos.ShoppingListDTO;
import organizer.system.localization.LocalConfig;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Named("s_L_Bean")
@ViewScoped
@Getter
@Setter
public class ShoppingListBean implements Serializable {
    private LocalDate s_D;
    private LocalDate e_D;


    private String s_W;

    private ShoppingListDTO s_L_DTO;
    private ShoppingListDAO s_DAO;
    private DataModel<IngredientDTO> s_L_DM;
    private List<IngredientDTO> r_L;
    private DataModel<IngredientDTO> r_DM;
    private IngredientDTO n_I_DTO;

    private List<ShoppingListIngredientDTO> o_I_L;

    @Inject
    private UserBean u_Bean;


    @PostConstruct
    public void init() {

        this.s_D = LocalDate.now();
        if (this.e_D == null) {
            this.e_D = this.s_D.plusDays(7);
        }


        this.n_I_DTO = new IngredientDTO();
        this.s_DAO = new ShoppingListDAO();


        this.construct();


    }


    public void reload() {

        this.construct();

    }

    private void construct() {
        this.load();
        this.clean();
        this.filter();
        for (IngredientDTO i_DTO : this.s_L_DTO.getI_DTO_L()) {
            i_DTO.createImageUrlList();
        }

    }


    public void load() {
        this.s_L_DTO = new ShoppingListDTO(this.s_D, this.e_D);


    }

    public void add() {


        IngredientDTO t_A = this.r_DM.getRowData();

        if (!this.o_I_L.contains(t_A)) {

            this.s_DAO.addToShoppingList(this.u_Bean.getU_DTO(), t_A);
            this.clean();
            this.filter();
            for (IngredientDTO i_DTO : this.s_L_DTO.getI_DTO_L()) {
                i_DTO.createImageUrlList();
            }

        } else {

                    String msg = LocalConfig.getEntryForMessages("youalreadyhavethatonyourlist",this.u_Bean);
            FacesMessage facesMessage = new FacesMessage(msg);
            FacesContext.getCurrentInstance().addMessage("messages", facesMessage);

        }


    }

    public void update() {
        IngredientDTO t_U = this.s_L_DM.getRowData();

        this.s_DAO.updateShoppingListAmount(this.u_Bean.getU_DTO(), t_U);
        this.clean();
    }


    public void remove() {
        IngredientDTO t_Remove = this.s_L_DM.getRowData();
        this.s_DAO.removeFromShoppingList(this.u_Bean.getU_DTO(), t_Remove);
        this.clean();
    }

    private void filter() {

        List<IngredientDTO> i_DTO_L = this.s_L_DTO.getI_DTO_L();

        HashMap<IngredientDTO, List<Float>> hashMap = new HashMap();


        for (IngredientDTO i_DTO : i_DTO_L) {
            if (!hashMap.containsKey(i_DTO)) {
                hashMap.put(i_DTO, new ArrayList<>());
            }
            hashMap.get(i_DTO).add(i_DTO.getAmount());
        }


        for (IngredientDTO i_DTO : hashMap.keySet()) {
            List<Float> current = hashMap.get(i_DTO);
            float sum = 0;
            for (Float f : current) {
                sum += f;
            }
            hashMap.put(i_DTO, new ArrayList<>());
            hashMap.get(i_DTO).add(sum);

        }


        List<IngredientDTO> total = new ArrayList<>();

        for (IngredientDTO i_DTO : hashMap.keySet()) {
            i_DTO.setAmount(hashMap.get(i_DTO).get(0));
            total.add(i_DTO);
        }


        this.s_L_DTO.setI_DTO_L(total);


    }

    private void clean() {
        this.s_L_DTO.setI_DTO_L(s_DAO.getIngredientssByUserDTO(this.u_Bean.getU_DTO(), this.s_L_DTO));
        this.o_I_L = s_DAO.getShoppingListIngredients(this.u_Bean.getU_DTO());
        this.s_L_DTO.getI_DTO_L().addAll(this.o_I_L);
        this.s_L_DM = new ListDataModel<>(this.s_L_DTO.getI_DTO_L());
    }


    public void search() {
        this.r_L = new ArrayList<>();
        this.r_DM = new ListDataModel<>(this.r_L);

        List<Integer> ids = IngredientSearch.getInstance().search(s_W);


        if (!ids.isEmpty()) {
            for (Integer i : ids) {

                for (IngredientDTO dto : IngredientSearch.getInstance().getIngredients()) {
                    if (i == dto.getIID()) {
                        this.r_L.add(dto);
                    }
                }
            }
        }
        this.r_DM = new ListDataModel<>(this.r_L);
    }


}
