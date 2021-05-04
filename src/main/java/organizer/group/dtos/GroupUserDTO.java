package organizer.group.dtos;

import organizer.user.dtos.UserDTO;

public class GroupUserDTO extends UserDTO {
    private boolean groupAdmin;
    private int gID;
    private String gName;

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public boolean isGroupAdmin() {
        return groupAdmin;
    }

    public void setGroupAdmin(boolean groupAdmin) {
        this.groupAdmin = groupAdmin;
    }

    public int getgID() {
        return gID;
    }

    public void setgID(int gID) {
        this.gID = gID;
    }
}
