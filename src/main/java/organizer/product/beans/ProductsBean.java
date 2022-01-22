package organizer.product.beans;

import lombok.Getter;
import lombok.Setter;
import organizer.group.daos.GroupDAO;
import organizer.group.daos.GroupMessageDAO;
import organizer.group.daos.GroupUserDAO;
import organizer.group.dtos.GroupDTO;
import organizer.group.dtos.GroupMessageDTO;
import organizer.group.dtos.GroupUserDTO;
import organizer.product.daos.ProductDAO;
import organizer.product.dtos.ProductDTO;
import organizer.system.Utility;
import organizer.system.converter.HashConverter;
import organizer.system.enums.FaceletPath;
import organizer.system.exceptions.DuplicateException;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by cord on 05.07.16.
 */
@Named("product")
@ViewScoped
@Getter
@Setter
public class ProductsBean implements Serializable, Validator {


    private String GroupUserHash;

    private UIComponent saveButton;
    private List<ProductDTO> dto;
    private DataModel<ProductDTO> productModel;
    private DataModel<ProductDTO> newProductModel;
    private UIComponent dTable;
    ProductDTO newProduct;
    GroupDTO groupDTO;
    private boolean isAllowed;

    private List<GroupMessageDTO> dtos;

    private DataModel messagesDataModel;

    private GroupMessageDTO newDTO;


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

        this.newDTO = new GroupMessageDTO();

        GroupMessageDAO groupMessageDAO = new GroupMessageDAO();
        this.dtos = groupMessageDAO.getMessages(this.groupDTO, userBean.getDto());
        this.messagesDataModel = new ListDataModel(this.dtos);


        try {
            this.GroupUserHash = HashConverter.sha384((HashConverter.sha384(String.valueOf(this.userBean.getDto().getUserID())))
                    + HashConverter.sha384(String.valueOf(this.groupDTO.getgID())));
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public boolean isGroupAdmin() {

        return this.groupDTO.isGroupAdmin();
    }

    public void isAllowed() {



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

    public void refresh() {
        this.newDTO = new GroupMessageDTO();
        GroupMessageDAO groupMessageDAO = new GroupMessageDAO();
        this.dtos = groupMessageDAO.getMessages(this.groupDTO, userBean.getDto());
        this.messagesDataModel = new ListDataModel(this.dtos);

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


    public void insert() {

        GroupMessageDAO dao = new GroupMessageDAO();
        dao.insertByDTOs(this.groupDTO, this.userBean.getDto(), this.newDTO);

    }


    public void sendMessage() {

        this.send();
        this.insert();
        this.newDTO.setMessage("");
    }


    @Inject
    @Push
    PushContext groupMessageChannel;


    public void send() {

        GroupDAO dao = new GroupDAO();
        GroupUserDAO groupUserDAO = new GroupUserDAO();
        String userName = groupUserDAO.getUsernameById(this.userBean.getDto().getUserID());


        List<String> users = this.groupUserHashes(dao.getUsers(this.groupDTO));


        this.newDTO.setUser(userName);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        this.newDTO.setTime(formatter.format(date));
        groupMessageChannel.send(this.newDTO, users);

    }


    private ArrayList groupUserHashes(List<GroupUserDTO> users) {

        ArrayList toReturn = new ArrayList();
        for (GroupUserDTO dto : users) {
            try {
                String h = HashConverter.sha384((HashConverter.sha384(String.valueOf(dto.getUserID())))
                        + HashConverter.sha384(String.valueOf(this.groupDTO.getgID())));


                toReturn.add(h);
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return toReturn;


    }
}
