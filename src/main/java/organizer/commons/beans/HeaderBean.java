package organizer.commons.beans;


import organizer.system.Utility;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;


@Named("headerBean")
@ViewScoped
public class HeaderBean implements Serializable {
    private UIComponent uiComponent;
    @Inject
    UserBean userBean;
    private int notifications;
    private String url;

    @PostConstruct
    public void init() {
        this.url = Utility.getURL();

    }


    public UIComponent getUiComponent() {
        return uiComponent;
    }

    public void setUiComponent(UIComponent uiComponent) {
        this.uiComponent = uiComponent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNotifications() {
        return notifications;
    }

    public void setNotifications(int notifications) {
        this.notifications = notifications;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
}
