package organizer.user.beans;



import organizer.system.Utility;
import organizer.system.enums.FaceletPath;
import organizer.user.daos.NotifcationDAO;
import organizer.user.dtos.NotificationDTO;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

import java.util.Collections;
import java.util.List;

@ViewScoped
@Named("notification")
public class NotificationBean implements Serializable {
    List<NotificationDTO> dtos;
    DataModel<NotificationDTO> notificationDataModel;

    public DataModel<NotificationDTO> getNotificationDataModel() {
        return notificationDataModel;
    }

    public void setNotificationDataModel(DataModel<NotificationDTO> notificationDataModel) {
        this.notificationDataModel = notificationDataModel;
    }

    @Inject
    UserBean userBean;

    @PostConstruct
    public void init() {

        NotifcationDAO dao = new NotifcationDAO();
        this.dtos = dao.selectByUserDTO(this.userBean.getDto());

        this.notificationDataModel = new
                ListDataModel<>(this.dtos);

    }


    public String select() {
        NotificationDTO dto = this.notificationDataModel.getRowData();

        String path = Utility.getURL() + FaceletPath.PRODUCTS.getPath() + "?gID=" + dto.getgID();

        return path;
    }












}
