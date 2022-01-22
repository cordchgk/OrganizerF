package organizer.commons.beans;


import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
public class HeaderBean implements Serializable {

    private int notifications;
    private String url;

    private UIComponent uiComponent;


    @Inject
    UserBean userBean;

    @PostConstruct
    public void init() {
        this.url = Utility.getURL();

    }


}
