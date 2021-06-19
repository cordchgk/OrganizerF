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
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.io.StringWriter;


@Path("/user")

public class HelloResource implements Serializable {

    @Inject
    UserBean userBean;




    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_XML)
    public UserDTO getSelf() {

        if (userBean.getDto().getUserID() == null) {
            return null;
        }


        return userBean.getDto();
    }


/**
 @GET
 @Path("/{id}/get")
 @Produces(MediaType.APPLICATION_XML) public String getUser(@PathParam("id") int id) {
 System.out.println(userBean.getDto().getUserID());
 UserDAO dao = new UserDAO();
 UserDTO users = dao.selectById(id);
 return "<?xml version=\"1.0\"?>" + "<user>" + users.getFirstname() + " " + users.getSurname() + "</user>";
 }
 **/
}
