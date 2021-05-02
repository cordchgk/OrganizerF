package organizer.user.dtos;

public class UserGroupDTO extends UserDTO{
    private boolean groupAdmin;

    public boolean isGroupAdmin() {
        return groupAdmin;
    }

    public void setGroupAdmin(boolean groupAdmin) {
        this.groupAdmin = groupAdmin;
    }
}
