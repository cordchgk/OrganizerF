package organizer.REST.services;

import organizer.user.daos.UserDAO;
import organizer.user.dtos.UserDTO;

import javax.annotation.PostConstruct;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;


@Path("/bonjour")
@Named("rest")
@SessionScoped
public class HelloResource implements Serializable {
boolean loggedIn;



    @PostConstruct
    public void init(){
        this.loggedIn = true;
    }


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String direBonjour() {
if(loggedIn == false){
    return "Access Denied";
}
        UserDAO dao = new UserDAO();
        UserDTO users = dao.selectById(1);
        return users.getFirstname() + " " + users.getSurname();
    }
}
