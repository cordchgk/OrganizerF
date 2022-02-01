package organizer.user.beans;


import lombok.Getter;
import lombok.Setter;

import org.primefaces.PrimeFaces;
import organizer.system.Utility;

import organizer.system.enums.FaceletPath;
import organizer.system.exceptions.DuplicateException;
import organizer.user.daos.UserDAO;
import organizer.user.daos.UserSettingsDAO;
import organizer.user.dtos.UserDTO;
import organizer.user.dtos.UserSettingsDTO;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;

import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Cookie;

import java.io.Serializable;
import java.util.*;


@Named("user")
@SessionScoped
@Getter
@Setter
public class UserBean implements Serializable {


    private UserDTO u_DTO;

    private int notifications = 0;

    private String c_E_ID;

    private UserDAO u_DAO = new UserDAO();


    @PostConstruct
    public void init() {

        Cookie cookie = Utility.getCookie();
        if (cookie != null) {
            this.u_DTO = this.u_DAO.selectByCookie(cookie);
        }


        if (this.u_DTO == null) {
            this.u_DTO = new UserDTO();
            this.u_DTO.setUserSettingsDTO(new UserSettingsDTO());
        } else {
            UserSettingsDAO userSettingsDAO = new UserSettingsDAO();
            this.u_DTO.setUserSettingsDTO(userSettingsDAO.getUserSettingsByDTO(this.u_DTO));
        }


    }


    public String login() {

        this.u_DTO = u_DAO.selectByDto(this.u_DTO);


        if (this.u_DTO != null) {

            UserSettingsDAO u_S_DAO = new UserSettingsDAO();
            this.u_DTO.setUserSettingsDTO(u_S_DAO.getUserSettingsByDTO(this.u_DTO));

            if (this.u_DTO.isStatus()) {
                return FaceletPath.MEALS.getRedirectionPath();
            } else {
                return FaceletPath.VERIFICATION.getRedirectionPath();
            }


        }
        return FaceletPath.LOGIN.getRedirectionPath();
    }


    public String logout() {
        this.nullCookie();
        UserSettingsDTO userSettingsDTO = new UserSettingsDTO();
        userSettingsDTO.setLocale(this.u_DTO.getUserSettingsDTO().getLocale());
        this.u_DTO = new UserDTO();
        u_DTO.setUserSettingsDTO(userSettingsDTO);

        return FaceletPath.INDEX.getRedirectionPath();
    }


    public Boolean isAuthenticated() {
        return this.u_DTO.getUserID() != null;
    }

    public void updateJSESSIONID() {
        Cookie cookie = Utility.getCookie();
        if (cookie != null) {
            if (!cookie.getValue().equals(this.u_DTO.getSessioncookie()) && this.u_DTO.getUserID() != null) {

                try {
                    this.u_DAO.updateUserCookie(this.u_DTO, cookie);
                    this.u_DTO.setSessioncookie(cookie.getValue());
                } catch (DuplicateException e) {
                    e.printStackTrace();
                }
            }

        }
    }




    public void reset() {

        this.notifications = 0;
    }


    public String getLocale() {
        return this.u_DTO.getLocale();
    }


    public Integer getUserId() {
        return this.u_DTO.getUserID();
    }


    private void nullCookie() {
        Cookie c = new Cookie("JSESSIONID", "");
        try {
            this.u_DAO.updateUserCookie(this.u_DTO, c);
        } catch (DuplicateException e) {
            e.printStackTrace();
        }
    }


    public void updateUserLanguage() {
        if (this.u_DTO != null && this.getUserId() != null) {
            try {
                UserDAO u_DAO = new UserDAO();
                u_DAO.updateUserLanuage(this.u_DTO);

            } catch (DuplicateException e) {
                e.printStackTrace();
            }
        }
        PrimeFaces.current().executeInitScript("reload()");

    }


}
