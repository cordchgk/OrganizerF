package organizer.commons.dtos;

import java.util.List;

public class HeaderDTO {


    private int userID;
    private List<Integer> gIDs;


    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public List<Integer> getgIDs() {
        return gIDs;
    }

    public void setgIDs(List<Integer> gIDs) {
        this.gIDs = gIDs;
    }
}
