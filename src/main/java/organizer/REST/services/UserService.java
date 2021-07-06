package organizer.REST.services;


//import org.springframework.beans.factory.annotation.Autowired;

import organizer.REST.authentication.AuthenticationService;
import organizer.group.daos.GroupDAO;
import organizer.group.dtos.GroupDTO;
import organizer.user.dtos.UserDTO;

import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Path("/user")
@RequestScoped
public class UserService implements Serializable {

    UserDTO userDTO;
    @Context
    private HttpServletRequest request;


    public void init() {

        try {
            this.userDTO = AuthenticationService.assignUser(request);


        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_XML)
    public UserDTO getSelf(@Context final HttpServletResponse response) {

        this.init();
if (this.userDTO == null){
    response.setStatus(501);
    return null;
}
        return this.userDTO;
    }

    @GET
    @Path("/get/groups")
    @Produces(MediaType.APPLICATION_XML)
    public List<GroupDTO> getUserGroups() {

        this.init();
        System.out.println(this.userDTO.getUserID());

        return new GroupDAO().selectByUser(this.userDTO);
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
