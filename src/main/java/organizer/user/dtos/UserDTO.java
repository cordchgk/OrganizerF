package organizer.user.dtos;


import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Locale;

/**
 * Created by cord on 07.06.16.
 */
@XmlRootElement(name = "user")
@Getter
@Setter
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 3461340127831352062L;
    private Integer userID = null;
    private String email = null;
    private String firstname = null;
    private String surname = null;
    private String passwordHash = null;
    private String address;
    private String verificationHash = null;
    private Boolean status;
    private String language = "en";

    private UserSettingsDTO userSettingsDTO;

    @XmlElement
    public String getFirstname() {
        return firstname;
    }

    @XmlElement
    public String getSurname() {
        return surname;
    }


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


    @XmlElement
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @XmlAttribute
    public Integer getUserID() {
        return userID;
    }


    public void setUserID(Integer userID) {
        this.userID = userID;
    }


    @XmlElement
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }


    public void setSurname(String surname) {
        this.surname = surname;
    }

    @XmlTransient
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserDTO() {


    }

    public UserDTO(int id) {

    }

    public String getLocale(){
        return this.userSettingsDTO.getLocale();
    }
}