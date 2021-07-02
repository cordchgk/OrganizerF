package organizer.REST.services;


//import org.springframework.beans.factory.annotation.Autowired;
import organizer.group.daos.GroupDAO;
import organizer.group.dtos.GroupDTO;
import organizer.user.beans.UserBean;
import organizer.user.dtos.UserDTO;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/user")

public class HelloResource implements Serializable {

    @Inject
    UserBean userBean;




    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_XML)
    public UserDTO getSelf() {
      //  System.out.println(this.getHeadersInfo());
        System.out.println("called");
        if (userBean.getDto().getUserID() == null) {
            return null;
        }


        return  userBean.getDto();
    }

    @GET
    @Path("/get/groups")
    @Produces(MediaType.APPLICATION_XML)
    public List<GroupDTO> getUserGroups(){

        return new GroupDAO().selectByUser(userBean.getDto());
    }
/*
    @Autowired
    private HttpServletRequest request;

    private Map<String, String> getHeadersInfo() {

        Map<String, String> map = new HashMap<String, String>();

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }
*/

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
