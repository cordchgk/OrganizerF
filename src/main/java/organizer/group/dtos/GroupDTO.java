package organizer.group.dtos;


import java.io.Serializable;

/**
 * Created by cord on 07.06.16.
 */
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

    public int getgID() {
        return gID;
    }

    public void setgID(int gID) {
        this.gID = gID;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }


}
