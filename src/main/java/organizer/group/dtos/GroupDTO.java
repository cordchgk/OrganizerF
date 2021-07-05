package organizer.group.dtos;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * Created by cord on 07.06.16.
 */

@XmlRootElement(name = "group")
public class GroupDTO implements Serializable {

    private static final long serialVersionUID = 3461340127831352062L;

    private String name;
    private int gID;
    private boolean accepted;
    private boolean groupAdmin;

    public boolean isGroupAdmin() {
        return groupAdmin;
    }

    public void setGroupAdmin(boolean groupAdmin) {
        this.groupAdmin = groupAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public int getgID() {
        return gID;
    }

    public void setgID(int gID) {
        this.gID = gID;
    }


    @XmlTransient
    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }


}
