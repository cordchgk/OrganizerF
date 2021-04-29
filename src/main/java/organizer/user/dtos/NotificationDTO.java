package organizer.user.dtos;

public class NotificationDTO {

    private int nID;
    private int gID;
    private String time;
    private String message;
    private String name;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getnID() {
        return nID;
    }

    public void setnID(int nID) {
        this.nID = nID;
    }

    public int getgID() {
        return gID;
    }

    public void setgID(int gID) {
        this.gID = gID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
