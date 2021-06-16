package organizer.REST;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path( "/message" )
public class MessageResource
{
    @GET
    @Produces( MediaType.TEXT_PLAIN )
    public String message()
    {
        System.out.println("called");
        return "Yea! ";
    }
}