package organizer.group.beans;

import organizer.group.daos.GroupDAO;
import organizer.group.daos.GroupUserDAO;
import organizer.group.dtos.GroupDTO;
import organizer.group.dtos.GroupUserDTO;
import organizer.system.Utility;
import organizer.system.enums.FaceletPath;
import organizer.system.exceptions.DuplicateException;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cord on 05.07.16.
 */
@Named("group")

@ViewScoped
public class GroupBean implements Serializable {

    private DataModel groupModelAccepted;
    private DataModel groupModelNotAccepted;
    private List<GroupDTO> gDTOAccepted;
    private List<GroupDTO> gDTONotAccepted;
    private List<GroupDTO> allGroups;
    private int ID;
    private String newGroupName;

    public String getNewGroupName() {
        return newGroupName;
    }

    public void setNewGroupName(String newGroupName) {
        this.newGroupName = newGroupName;
    }

    public DataModel getGroupModelAccepted() {
        return groupModelAccepted;
    }

    public void setGroupModelAccepted(DataModel groupModelAccepted) {
        this.groupModelAccepted = groupModelAccepted;
    }

    public DataModel getGroupModelNotAccepted() {
        return groupModelNotAccepted;
    }

    public void setGroupModelNotAccepted(DataModel groupModelNotAccepted) {
        this.groupModelNotAccepted = groupModelNotAccepted;
    }

    public List<GroupDTO> getgDTOAccepted() {
        return gDTOAccepted;
    }

    public void setgDTOAccepted(List<GroupDTO> gDTOAccepted) {
        this.gDTOAccepted = gDTOAccepted;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public List<GroupDTO> getgDTONotAccepted() {
        return gDTONotAccepted;
    }

    public void setgDTONotAccepted(List<GroupDTO> gDTONotAccepted) {
        this.gDTONotAccepted = gDTONotAccepted;
    }


    @Inject
    UserBean userBean;

    @PostConstruct
    public void init() {


        gDTOAccepted = new ArrayList<>();
        gDTONotAccepted = new ArrayList<>();
        GroupDAO dao = new GroupDAO();
        this.allGroups = dao.selectByUser(userBean.getDto());
        for (GroupDTO g : this.allGroups
        ) {
            if (g.isAccepted()) {
                this.gDTOAccepted.add(g);
            } else {
                this.gDTONotAccepted.add(g);
            }
        }
        userBean.setgDTOAccepted(gDTOAccepted);
        userBean.setgDTONotAccepted(gDTONotAccepted);
        groupModelAccepted = new ListDataModel(gDTOAccepted);
        groupModelNotAccepted = new ListDataModel(gDTONotAccepted);

    }

    public String select() {
        GroupDTO dto = (GroupDTO) this.groupModelAccepted.getRowData();
        this.ID = dto.getgID();

        return Utility.getURL() + FaceletPath.PRODUCTS.getPath() + "?gID=" + this.ID;
    }


    public String accept() {
        GroupDTO dto = (GroupDTO) this.groupModelNotAccepted.getRowData();
        dto.setAccepted(true);
        GroupDAO dao = new GroupDAO();
        try {
            dao.update(dto, userBean.getDto());
        } catch (DuplicateException e) {
            e.printStackTrace();
        }
        return FaceletPath.GROUPS.getRedirectionPath();
    }


    public String createGroup() {

        GroupDAO dao = new GroupDAO();
        GroupUserDTO newGroupUserDTO = new GroupUserDTO();
        newGroupUserDTO.setgName(this.newGroupName);
        newGroupUserDTO.setUserID(this.userBean.getUserID());
        try {
            dao.createGroup(newGroupUserDTO);


        } catch (DuplicateException e) {
            FacesContext.getCurrentInstance().addMessage();
            e.printStackTrace();
        }

        return FaceletPath.GROUPS.getRedirectionPath();

    }


}
