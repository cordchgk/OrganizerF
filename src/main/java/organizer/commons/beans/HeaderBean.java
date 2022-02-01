package organizer.commons.beans;


import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import organizer.system.Utility;
import organizer.system.enums.FaceletPath;
import organizer.system.exceptions.DuplicateException;
import organizer.user.beans.UserBean;
import organizer.user.daos.UserDAO;

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

import static organizer.system.Utility.urlBuilder;

/**
 * CDI BEAN for the page header
 */
@Named("headerBean")
@ViewScoped
@Getter
@Setter
public class HeaderBean implements Serializable {

    private int notifications;
    private String url;
    private int id;

    private UIComponent uiComponent;

    private String currentURL;


    @Inject
    UserBean userBean;

    @PostConstruct
    public void init() {

        this.currentURL = Utility.urlBuilder();
        this.url = Utility.getURL();
    }


    /**
     *
     * @return Locale tag of the users language
     */
    public String locale() {
        return this.userBean.getLocale();
    }








}
