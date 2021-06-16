package organizer.REST.services;

import organizer.user.daos.UserDAO;
import organizer.user.dtos.UserDTO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/bonjour")
public class HelloResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String direBonjour() {
        System.out.println("called");
        UserDAO dao = new UserDAO();
        UserDTO users = dao.selectById(1);
        return users.getFirstname() + " " + users.getSurname();
    }
}
