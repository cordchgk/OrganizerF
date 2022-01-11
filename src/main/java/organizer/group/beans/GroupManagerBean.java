package organizer.group.beans;


import organizer.group.daos.GroupManagerDAO;
import organizer.group.daos.GroupUserDAO;
import organizer.group.dtos.GroupDTO;
import organizer.user.beans.UserBean;
import organizer.group.dtos.GroupUserDTO;

import javax.annotation.PostConstruct;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;


@Named("groupManager")
@ViewScoped
public class GroupManagerBean implements Serializable {

    @Inject
    UserBean userBean;

    private GroupUserDTO newGroupUser;
    GroupDTO dto;
    List<GroupUserDTO> userDTOS;
    DataModel<GroupUserDTO> userDataModel;

    public GroupDTO getDto() {
        return dto;
    }

    public void setDto(GroupDTO dto) {
        this.dto = dto;
    }

    public List<GroupUserDTO> getUserDTOS() {
        return userDTOS;
    }

    public void setUserDTOS(List<GroupUserDTO> userDTOS) {
        this.userDTOS = userDTOS;
    }

    public DataModel<GroupUserDTO> getUserDataModel() {
        return userDataModel;
    }

    public void setUserDataModel(DataModel<GroupUserDTO> userDataModel) {
        this.userDataModel = userDataModel;
    }

    public GroupUserDTO getNewGroupUser() {
        return newGroupUser;
    }

    public void setNewGroupUser(GroupUserDTO newGroupUser) {
        this.newGroupUser = newGroupUser;
    }

    @PostConstruct
    public void init() {
        this.newGroupUser = new GroupUserDTO();
        Map<String, String> parameter = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        this.dto = new GroupDTO();
        this.dto.setgID(Integer.parseInt(parameter.get("gID")));
        GroupManagerDAO dao = new GroupManagerDAO();
        this.dto = dao.getDtoByID(this.dto);
        this.userDTOS = dao.getUsers(this.dto);

        this.userDataModel = new ListDataModel<>(this.userDTOS);


    }


    public void remove() {
        GroupUserDTO selectedDTO = userDataModel.getRowData();
        userDTOS.remove(selectedDTO);
        selectedDTO.setgID(this.dto.getgID());
        GroupUserDAO dao = new GroupUserDAO();
        dao.removeUserFromGroup(selectedDTO);
    }

    public void makeAdmin() {

        GroupUserDTO selectedDTO = userDataModel.getRowData();


        GroupManagerDAO dao = new GroupManagerDAO();
        boolean success = dao.makeAdmin(selectedDTO);
        if (success) {
            selectedDTO.setGroupAdmin(true);
        }
        //TODO: FacesContext...addMessage
    }


    public void isAllowed() {


        FacesContext fc = FacesContext.getCurrentInstance();

        boolean b = new GroupUserDAO().isGroupAdminByDTO(this.getDto(), this.userBean.getDto());


        if (!b) {
            ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler();
            nav.performNavigation("/access/access-denied.xhtml");
        }


    }


    public boolean isLast() {
        return !(this.userDTOS.size() > 1);
    }


    public void addUserByEmail() {
        this.newGroupUser.setgID(this.dto.getgID());
        GroupManagerDAO dao = new GroupManagerDAO();
        if (!dao.addToGroup(this.newGroupUser)) {

        }


    }

}
