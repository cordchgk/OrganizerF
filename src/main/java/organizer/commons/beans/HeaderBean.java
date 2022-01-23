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
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Map;


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

    private FaceletPath path;





    @Inject
    UserBean userBean;

    @PostConstruct
    public void init() {


        this.currentURL = urlBuilder();

        this.url = Utility.getURL();
    }


    public void lang() {
        PrimeFaces.current().executeInitScript("showSure()");
    }


    public String updateUserLanguage() {
        if (this.userBean.getDto() != null && this.userBean.getDto().getUserID() != null) {
            try {
                UserDAO u_DAO = new UserDAO();
                u_DAO.updateUserLanuage(this.userBean.getDto());

            } catch (DuplicateException e) {
                e.printStackTrace();
            }
        }
        return this.currentURL;


    }


    public static String urlBuilder(){
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(origRequest.getRequestURL());
        if (!FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().isEmpty()) {
            stringBuilder.append("?");

            for (String s : FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().keySet()) {
                stringBuilder.append(s);
                stringBuilder.append("=");
                stringBuilder.append(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(s));
            }
        }
        return stringBuilder.toString();
    }

}
