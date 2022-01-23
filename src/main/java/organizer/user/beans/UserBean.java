package organizer.user.beans;


import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import organizer.group.dtos.GroupDTO;
import organizer.system.enums.FaceletPath;
import organizer.system.exceptions.DuplicateException;
import organizer.user.daos.UserDAO;
import organizer.user.daos.UserSettingsDAO;
import organizer.user.dtos.UserDTO;
import organizer.user.dtos.UserSettingsDTO;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.*;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;


@Named("user")
@SessionScoped
@Getter
@Setter
public class UserBean implements Serializable {

    private Integer userID = null;
    private UserDTO dto;
    private String verificationHash;
    private List<GroupDTO> gDTOAccepted;
    private int notifications = 0;
    private List<GroupDTO> gDTONotAccepted;

    private String currentEventId;

    private UserDAO u_DAO = new UserDAO();


    @PostConstruct
    public void init() {
        this.dto = new UserDTO();
        this.dto.setUserSettingsDTO(new UserSettingsDTO());


    }


    public String login() {
        List<UserDTO> list;
        list = u_DAO.selectByDto(dto);
        if (!list.isEmpty()) {
            userID = list.get(0).getUserID();
            dto = list.get(0);

            UserSettingsDAO userSettingsDAO = new UserSettingsDAO();
            this.dto.setUserSettingsDTO(userSettingsDAO.getUserSettingsByDTO(this.dto));

            if (dto.isStatus()) {
                return FaceletPath.MEALS.getRedirectionPath();
            } else {
                return FaceletPath.VERIFICATION.getRedirectionPath();
            }


        }
        return FaceletPath.LOGIN.getRedirectionPath();
    }


    public String logout() {
        userID = null;
        UserSettingsDTO userSettingsDTO = new UserSettingsDTO();
        userSettingsDTO.setLocale(this.dto.getUserSettingsDTO().getLocale());
        dto = new UserDTO();
        dto.setUserSettingsDTO(userSettingsDTO);


        verificationHash = "";

        return FaceletPath.INDEX.getRedirectionPath();
    }


    public String verificate() {

        dto.setVerificationHash(u_DAO.getVerificationHash(dto));

        if (!verificationHash.equals(dto.getVerificationHash())) {
            userID = null;
            return FaceletPath.LOGIN.getRedirectionPath();
        } else {

            try {
                u_DAO.updateStatus(dto.getEmail());
                this.dto.setStatus(true);
            } catch (DuplicateException ex) {

            }
            return FaceletPath.PROFILE.getRedirectionPath();
        }


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
