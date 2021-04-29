package organizer.user.dtos;


import java.io.Serializable;

/**
 * Created by cord on 07.06.16.
 */
public class UserDTO  implements Serializable {

    private static final long serialVersionUID = 3461340127831352062L;

    private Integer userID = null;
    private String email = null;
    private String firstname = null;
    private String surname = null;
    private String passwordHash = null;
    private String address;
    private String verificationHash = null;
    private Boolean status;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getVerificationHash() {
        return verificationHash;
    }

    public void setVerificationHash(String verificationHash) {
        this.verificationHash = verificationHash;
    }

    public Boolean isStatus() {
        return status;

    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getUserID() {
        return userID;
    }


    public void setUserID(Integer userID) {
        this.userID = userID;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }


}