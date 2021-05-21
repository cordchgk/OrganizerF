package organizer.group.beans;

import organizer.group.daos.GroupMessageDAO;
import organizer.group.dtos.GroupDTO;
import organizer.group.dtos.GroupMessageDTO;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;


@ViewScoped
@Named("groupMessages")
public class GroupMessagesBean implements Serializable {

    private List<GroupMessageDTO> dtos;
    private GroupDTO groupDTO;
    private DataModel messagesDataModel;
    private GroupMessageDTO newDTO;





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
    public void init(){
        this.groupDTO = new GroupDTO();
        GroupMessageDAO dao = new GroupMessageDAO();
        this.dtos = dao.getMessages(this.groupDTO);
        this.messagesDataModel = new ListDataModel(this.dtos);

    }



    public void insert(){
        GroupMessageDAO dao = new GroupMessageDAO();
        dao.insertByDTOs(this.groupDTO,this.userBean.getDto(),this.newDTO);

    }















}
