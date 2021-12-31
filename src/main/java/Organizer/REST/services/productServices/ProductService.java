package organizer.REST.services.productServices;

import organizer.REST.authentication.AuthenticationService;
import organizer.group.dtos.GroupDTO;
import organizer.product.daos.ProductDAO;
import organizer.product.dtos.ProductDTO;
import organizer.user.dtos.UserDTO;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;


@Path("/product")
public class ProductService {


    UserDTO userDTO;
    ProductDTO productDTO;


    @Context
    private HttpServletRequest request;


    public void init() {

        try {
            this.userDTO = AuthenticationService.assignUser(request);


            if (!new ProductDAO().getUserIdsByProductDTO(this.productDTO).contains(this.userDTO.getUserID())) {
                throw new WebApplicationException(401);
            }


        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    @GET
    @Path("/get/{id}")
    @Produces(MediaType.APPLICATION_XML)
    public ProductDTO getProduct(@PathParam("id") int id) {
        this.productDTO = new ProductDTO();
        this.productDTO.setpID(id);
        this.init();


        return this.productDTO;
    }


    @GET
    @Path("/get/groupproducts/{id}")
    @Produces(MediaType.APPLICATION_XML)
    public List<ProductDTO> getGroupProducts(@PathParam("id") int id) {
        this.productDTO = new ProductDTO();
        this.productDTO.setpID(id);
        this.init();

        GroupDTO dto = new GroupDTO();
        dto.setgID(id);

        return new ProductDAO().selectByDTO(dto);
    }


}
