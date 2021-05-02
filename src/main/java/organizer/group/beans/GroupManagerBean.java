package organizer.group.beans;


import organizer.group.daos.GroupManagerDAO;
import organizer.group.dtos.GroupDTO;
import organizer.system.Utility;
import organizer.system.enums.FaceletPath;
import organizer.user.dtos.UserDTO;
import organizer.user.dtos.UserGroupDTO;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;


@Named("groupManager")
@ViewScoped
public class GroupManagerBean implements Serializable {


    GroupDTO dto;
    List<UserGroupDTO> userDTOS;
    DataModel<UserGroupDTO> userDataModel;

    public GroupDTO getDto() {
        return dto;
    }

    public void setDto(GroupDTO dto) {
        this.dto = dto;
    }

    public List<UserGroupDTO> getUserDTOS() {
        return userDTOS;
    }

    public void setUserDTOS(List<UserGroupDTO> userDTOS) {
        this.userDTOS = userDTOS;
    }

    public DataModel<UserGroupDTO> getUserDataModel() {
        return userDataModel;
    }

    public void setUserDataModel(DataModel<UserGroupDTO> userDataModel) {
        this.userDataModel = userDataModel;
    }

    @PostConstruct
    public void init() {
        Map<String, String> parameter = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        this.dto = new GroupDTO();
        this.dto.setgID(Integer.parseInt(parameter.get("gID")));
        GroupManagerDAO dao = new GroupManagerDAO();
        this.dto = dao.getDtoByID(this.dto);
        this.userDTOS = dao.getUsers(this.dto);
        this.userDataModel = new ListDataModel<>(this.userDTOS);
        System.out.println(this.userDTOS.size());


    }


    //TODO: implement methods

    public void remove() {

    }

    public void makeAdmin() {

    }

}
