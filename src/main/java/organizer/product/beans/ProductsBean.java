package organizer.product.beans;

import organizer.group.daos.GroupUserDAO;
import organizer.group.dtos.GroupDTO;
import organizer.product.daos.ProductDAO;
import organizer.product.dtos.ProductDTO;
import organizer.system.Utility;
import organizer.system.enums.FaceletPath;
import organizer.system.exceptions.DuplicateException;
import organizer.user.beans.UserBean;
import organizer.user.dtos.UserDTO;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;


import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cord on 05.07.16.
 */
@Named("product")

@ViewScoped
public class ProductsBean implements Serializable, Validator {


    private List<ProductDTO> dto;
    private DataModel<ProductDTO> productModel;
    private DataModel<ProductDTO> newProductModel;
    private UIComponent dTable;
    ProductDTO newProduct;
    GroupDTO groupDTO;


    public DataModel<ProductDTO> getNewProductModel() {
        return newProductModel;
    }

    public void setNewProductModel(DataModel<ProductDTO> newProductModel) {
        this.newProductModel = newProductModel;
    }

    public UIComponent getdTable() {
        return dTable;
    }

    public void setdTable(UIComponent dTable) {
        this.dTable = dTable;
    }

    public DataModel<ProductDTO> getProductModel() {
        return productModel;
    }

    public void setProductModel(DataModel<ProductDTO> productModel) {
        this.productModel = productModel;
    }

    public List<ProductDTO> getDto() {
        return dto;
    }

    public void setDto(List<ProductDTO> dto) {
        this.dto = dto;
    }

    private UIComponent saveButton;


    public UIComponent getSaveButton() {
        return saveButton;
    }

    public void setSaveButton(UIComponent saveButton) {
        this.saveButton = saveButton;
    }


    public GroupDTO getGroupDTO() {
        return groupDTO;
    }

    public void setGroupDTO(GroupDTO groupDTO) {
        this.groupDTO = groupDTO;
    }

    @Inject
    UserBean userBean;


    @PostConstruct
    public void init() {

        Map<String, String> parameter = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        ProductDAO dao = new ProductDAO();
        this.groupDTO = new GroupDTO();
        this.newProduct = new ProductDTO();
        List<ProductDTO> n = new ArrayList<>();


        groupDTO.setgID(Integer.parseInt(parameter.get("gID")));
        this.groupDTO.setGroupAdmin(new GroupUserDAO().isGroupAdminByDTO(groupDTO, userBean.getDto()));

        this.dto = dao.selectByDTO(groupDTO);


        n.add(this.newProduct);


        newProductModel = new ListDataModel<>(n);
        productModel = new ListDataModel<>(this.dto);


    }


    public boolean isGroupAdmin() {

        return this.groupDTO.isGroupAdmin();
    }

    public void isAllowed() {
        FacesContext fc = FacesContext.getCurrentInstance();


        boolean toReturn = false;
        for (GroupDTO dto : userBean.getgDTOAccepted()
        ) {
            if (dto.getgID() == this.groupDTO.getgID()) {
                toReturn = true;
            }
        }


        if (!toReturn) {
            ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler();
            nav.performNavigation("/access/access-denied.xhtml");
        }


    }


    public void inc() {
        ProductDTO selectedDTO = productModel.getRowData();

        selectedDTO.setDiff(selectedDTO.getDiff() + 1);

    }

    public void dec() {
        ProductDTO selectedDTO = productModel.getRowData();

        selectedDTO.setDiff(selectedDTO.getDiff() - 1);

    }


    public String save() {
        ProductDAO dao = new ProductDAO();
        for (ProductDTO dto : this.dto
        ) {

            dto.setCount(dto.getCount() + dto.getDiff());
            try {
                dao.update(dto);
            } catch (DuplicateException e) {
                e.printStackTrace();
            }
        }


        reset();
        return FaceletPath.PRODUCTS.getRedirectionPath() + "&includeViewParams=true";

    }


    public boolean rendered() {
        ProductDTO s = productModel.getRowData();

        return (s.getCount() + s.getDiff()) > 0;
    }

    private void reset() {
        for (ProductDTO d : this.dto
        ) {
            d.setDiff(0);
        }
    }


    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {

        String[] fields = uiComponent.getClientId().split(":");

        int index = Integer.parseInt(fields[fields.length - 2]);

        ProductDTO selectedDTO = this.dto.get(index);
        int v = Integer.parseInt(String.valueOf(o));
        selectedDTO.setDiff(v);

        if ((selectedDTO.getCount() + v) < 0) {

            FacesMessage msg =
                    new FacesMessage("Da stimmt etwas nicht");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);


            throw new ValidatorException(msg);
        }


    }


    public void namedChanged(AjaxBehaviorEvent event) {


        UIComponent uiComponent = event.getComponent();
        FacesContext context = FacesContext.getCurrentInstance();
        UIInput in = (UIInput) event.getSource();
        String[] fields = uiComponent.getClientId().split(":");
        int index = Integer.parseInt(fields[fields.length - 2]);

        ProductDTO selectedDTO = this.dto.get(index);

        selectedDTO.setDiff((Integer) in.getValue());

    }


    public void createProduct() {
        this.newProduct.setgID(this.groupDTO.getgID());
        this.dto.add(this.newProduct);
        ProductDAO dao = new ProductDAO();
        try {
            dao.insert(this.newProduct, this.userBean);
        } catch (DuplicateException e) {
            e.printStackTrace();
        }
        //return FaceletPath.PRODUCTS.getRedirectionPath() + "&includeViewParams=true";
    }

    public void delete() {
        ProductDTO pDTO = productModel.getRowData();
        ProductDAO dao = new ProductDAO();
        dao.deleteByDTO(pDTO);
        this.dto.remove(pDTO);
    }

    public boolean isEmpty() {

        return this.dto.isEmpty();
    }


    public String toGroupManager() {

        return Utility.getURL() + FaceletPath.GROUPMANAGER.getPath() + "?gID=" + this.groupDTO.getgID();
    }
}
