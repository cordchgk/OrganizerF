package organizer.REST.services;

import organizer.user.beans.UserBean;
import organizer.user.daos.UserDAO;
import organizer.user.dtos.UserDTO;

import javax.annotation.PostConstruct;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;


@Path("/user")
@Named("rest")
@SessionScoped
public class HelloResource implements Serializable {





    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String direBonjour() {

        UserDAO dao = new UserDAO();
        UserDTO users = dao.selectById(1);
        return "<?xml version=\"1.0\"?>" + "<user>" + users.getFirstname() + " " + users.getSurname() + "</user>";
    }



    @GET
    @Path("/{id}/get")
    @Produces(MediaType.APPLICATION_XML)
    public String getUser(@PathParam("id") int id) {
        UserDAO dao = new UserDAO();
        UserDTO users = dao.selectById(id);
        return "<?xml version=\"1.0\"?>" + "<user>" + users.getFirstname() + " " + users.getSurname() + "</user>";
    }
}
