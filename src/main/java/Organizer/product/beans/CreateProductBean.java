package organizer.product.beans;

import organizer.product.dtos.ProductDTO;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;


@Named("create")
@ViewScoped
public class CreateProductBean implements Serializable {


    private int gID;

    private ProductDTO dto;


    @Inject
    UserBean userBean;

    @PostConstruct
    public void init(){
        this.dto = new ProductDTO();

    }

}
