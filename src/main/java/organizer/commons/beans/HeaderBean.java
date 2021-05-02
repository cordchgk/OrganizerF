package organizer.commons.beans;


import organizer.commons.daos.HeaderDAO;
import organizer.commons.dtos.HeaderDTO;
import organizer.system.Events.UserEventBean;
import organizer.system.Utility;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;


@Named("headerB")
@ViewScoped
public class HeaderBean implements Serializable {

    private UIComponent uiComponent;
    private int notifications;

    public int getNotifications() {
        return notifications;
    }

    public void setNotifications(int notifications) {
        this.notifications = notifications;
    }

    HeaderDTO dto;

    public UIComponent getUiComponent() {
        return uiComponent;
    }

    public void setUiComponent(UIComponent uiComponent) {
        this.uiComponent = uiComponent;
    }


    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Inject
    UserBean userBean;


    @PostConstruct
    public void init() {
        this.url = Utility.getURL();

        HeaderDAO dao = new HeaderDAO();
        this.dto = new HeaderDTO();
        if (userBean.isAuthenticated()) {
            this.dto.setUserID(userBean.getUserID());
            this.dto.setgIDs(dao.selectByUser(this.dto));


        }

    }


    @PreDestroy
    public void destroy() {


    }


    public void notificate() {

        this.notifications++;
        System.out.println(this.uiComponent.getClientId());


    }


}
