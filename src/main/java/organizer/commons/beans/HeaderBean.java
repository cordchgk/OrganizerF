package organizer.commons.beans;



import organizer.commons.daos.HeaderDAO;
import organizer.commons.dtos.HeaderDTO;
import organizer.system.Events.UserEventBean;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import javax.faces.component.UIComponent;
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

    @Inject
    UserBean userBean;


    @PostConstruct
    public void init() {

        HeaderDAO dao = new HeaderDAO();
        this.dto = new HeaderDTO();
        if (userBean.isAuthenticated()) {
            this.dto.setUserID(userBean.getUserID());
            this.dto.setgIDs(dao.selectByUser(this.dto));
            UserEventBean event = UserEventBean.getInstance();
            for (Integer i : this.dto.getgIDs()
            ) {
                event.add(i, this);
            }


        }

    }


    @PreDestroy
    public void destroy() {


        UserEventBean event = UserEventBean.getInstance();
        if (this.dto.getgIDs() != null) {
            for (Integer i : this.dto.getgIDs()
            ) {
                event.remove(i, this);
            }
        }

    }



    public void notificate(){

        this.notifications++;
        System.out.println(this.uiComponent.getClientId());


    }


}
