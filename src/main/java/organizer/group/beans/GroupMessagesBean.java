package organizer.group.beans;

import organizer.group.daos.GroupMessageDAO;
import organizer.group.dtos.GroupDTO;
import organizer.group.dtos.GroupMessageDTO;
import organizer.product.beans.ProductsBean;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;


@ViewScoped
@Named("groupMessages")
public class GroupMessagesBean implements Serializable {

    private List<GroupMessageDTO> dtos;
    private GroupDTO groupDTO;
    private DataModel messagesDataModel;
    private GroupMessageDTO newDTO;


    @Inject
    ProductsBean productsBean;

    @Inject
    UserBean userBean;

    public List<GroupMessageDTO> getDtos() {
        return dtos;
    }

    public void setDtos(List<GroupMessageDTO> dtos) {
        this.dtos = dtos;
    }

    public GroupDTO getGroupDTO() {
        return groupDTO;
    }

    public void setGroupDTO(GroupDTO groupDTO) {
        this.groupDTO = groupDTO;
    }

    public DataModel getMessagesDataModel() {
        return messagesDataModel;
    }

    public void setMessagesDataModel(DataModel messagesDataModel) {
        this.messagesDataModel = messagesDataModel;
    }

    public GroupMessageDTO getNewDTO() {
        return newDTO;
    }

    public void setNewDTO(GroupMessageDTO newDTO) {
        this.newDTO = newDTO;
    }

    @PostConstruct
    public void init() {
        this.groupDTO = new GroupDTO();
        GroupMessageDAO dao = new GroupMessageDAO();
        this.dtos = dao.getMessages(this.groupDTO, userBean.getDto());
        this.messagesDataModel = new ListDataModel(this.dtos);

    }


    public void insert() {
        Map<String, String> parameter = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        this.groupDTO = new GroupDTO();
        this.groupDTO.setgID(Integer.parseInt(parameter.get("gID")));
        GroupMessageDAO dao = new GroupMessageDAO();
        dao.insertByDTOs(this.groupDTO, this.userBean.getDto(), this.newDTO);

    }


}
