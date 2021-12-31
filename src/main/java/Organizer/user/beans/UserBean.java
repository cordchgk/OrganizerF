package organizer.user.beans;


import organizer.group.dtos.GroupDTO;
import organizer.system.ConnectionPool;
import organizer.system.exceptions.DuplicateException;
import organizer.user.daos.UserDAO;
import organizer.user.dtos.UserDTO;
import organizer.system.enums.FaceletPath;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.ConfigurableNavigationHandler;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;

import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;


@Named("user")
@SessionScoped
public class UserBean implements Serializable {

    private Integer userID = null;
    private UserDTO dto = new UserDTO();
    private String verificationHash;
    private List<GroupDTO> gDTOAccepted;
    private int notifications = 0;
    private List<GroupDTO> gDTONotAccepted;

    public int getNotifications() {
        return notifications;
    }

    public void setNotifications(int notifications) {
        this.notifications = notifications;
    }

    public Integer getUserID() {

        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public List<GroupDTO> getgDTOAccepted() {
        return gDTOAccepted;
    }

    public void setgDTOAccepted(List<GroupDTO> gDTOAccepted) {
        this.gDTOAccepted = gDTOAccepted;
    }

    public List<GroupDTO> getgDTONotAccepted() {
        return gDTONotAccepted;
    }

    public void setgDTONotAccepted(List<GroupDTO> gDTONotAccepted) {
        this.gDTONotAccepted = gDTONotAccepted;
    }


    public String getVerificationHash() {
        return verificationHash;
    }

    public void setVerificationHash(String verificationHash) {
        this.verificationHash = verificationHash;
    }


    public UserDTO getDto() {
        return dto;
    }

    public void setDto(UserDTO dto) {
        this.dto = dto;
    }

    public String login() {


        UserDAO dao = getDAO();
        List<UserDTO> list;
        list = dao.selectByDto(dto);
        if (!list.isEmpty()) {
            userID = list.get(0).getUserID();
            dto = list.get(0);

            if (dto.isStatus()) {
                return FaceletPath.GROUPS.getRedirectionPath();
            } else {
                return FaceletPath.VERIFICATION.getRedirectionPath();
            }


        }
        return FaceletPath.LOGIN.getRedirectionPath();
    }


    public String logout() {
        userID = null;
        dto = new UserDTO();
        verificationHash = "";

        return FaceletPath.INDEX.getRedirectionPath();
    }


    public String verificate() {

        UserDAO dao = getDAO();
        dto.setVerificationHash(dao.getVerificationHash(dto));

        if (!verificationHash.equals(dto.getVerificationHash())) {
            userID = null;
            return FaceletPath.LOGIN.getRedirectionPath();
        } else {

            try {
                dao.updateStatus(dto.getEmail());
                this.dto.setStatus(true);
            } catch (DuplicateException ex) {

            }
            return FaceletPath.PROFILE.getRedirectionPath();
        }


    }


    private UserDAO getDAO() {
        return UserDAO.getInstance();
    }


    public Boolean isAuthenticated() {
        return this.userID != null && this.dto.isStatus();
    }


    public void isAdmin(ComponentSystemEvent event) {

        FacesContext fc = FacesContext.getCurrentInstance();

        if (!"admin".equals(fc.getExternalContext().getSessionMap().get("role"))) {

            ConfigurableNavigationHandler nav
                    = (ConfigurableNavigationHandler)
                    fc.getApplication().getNavigationHandler();

            nav.performNavigation("access-denied");
        }
    }


    @Push(channel = "testChannel")
    @Inject
    private PushContext testChannel;


    public void send(List<Integer> users) {

        this.notifications++;
        testChannel.send(this.notifications, users);

    }


    public void reset() {

        this.notifications = 0;
    }


}
