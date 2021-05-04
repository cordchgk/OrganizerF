package organizer.group.beans;

import organizer.group.daos.GroupDAO;
import organizer.group.daos.GroupUserDAO;
import organizer.product.beans.ProductsBean;
import organizer.user.beans.UserBean;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;



@Named("groupUser")
@SessionScoped
public class GroupUserBean implements Serializable {



    @Inject
    UserBean userBean;

    @Inject
    ProductsBean productsBean;


public boolean isGroupAdmin(){
    return new GroupUserDAO().isGroupAdminByDTO(this.productsBean.getGroupDTO(), this.userBean.getDto());
}



}
