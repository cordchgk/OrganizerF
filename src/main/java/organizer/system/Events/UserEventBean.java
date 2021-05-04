package organizer.system.Events;


import organizer.commons.beans.HeaderBean;
import organizer.product.daos.ProductDAO;
import organizer.user.beans.UserBean;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

@Named("userEvent")
@ApplicationScoped

public class UserEventBean implements Serializable {


    private  UserEventBean instance;
    private HashMap<Integer, List<HeaderBean>> map;


@PostConstruct
    public void init() {

    }








    public void add(int gID, HeaderBean headerBean) {
        if (!this.map.containsKey(gID)) {
            this.map.put(gID, new ArrayList<>());
            this.map.get(gID).add(headerBean);
        } else {
            this.map.get(gID).add(headerBean);
        }

    }


    public void remove(int gID, HeaderBean headerBean) {
        this.map.get(gID).remove(headerBean);

    }


}
