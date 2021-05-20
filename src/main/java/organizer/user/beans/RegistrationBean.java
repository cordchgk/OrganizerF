package organizer.user.beans;


import organizer.system.exceptions.DuplicateException;
import organizer.user.daos.UserDAO;
import organizer.user.dtos.UserDTO;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named("registrationBean")
@ViewScoped
public class RegistrationBean implements Serializable {
    UserDTO newUserDTO;


    @PostConstruct
    public void init() {
        this.newUserDTO = new UserDTO();
    }


    public void createUser() {
        UserDAO dao = new UserDAO();
        try {
            dao.insert(this.newUserDTO);
        } catch (DuplicateException e) {
            e.printStackTrace();
        }

    }

}
