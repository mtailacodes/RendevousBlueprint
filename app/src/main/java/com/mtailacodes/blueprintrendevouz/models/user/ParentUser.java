package com.mtailacodes.blueprintrendevouz.models.user;

/**
 * Created by matthewtaila on 12/3/17.
 */

public class ParentUser {

    private String userName, emailAddress, gender, password;

    public ParentUser() {

    }

    public ParentUser(String userName, String emailAddress, String gender, String password) {
        this.userName = userName;
        this.emailAddress = emailAddress;
        this.gender = gender;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
