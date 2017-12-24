package com.mtailacodes.blueprintrendevouz.models.user.user;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by matthewtaila on 12/3/17.
 */

public class ParentUser {

    private String userName, emailAddress, gender, password;
    private FirebaseUser mFirebaseUser;

    public ParentUser() {

    }

    public ParentUser(String userName, String gender) {
        this.userName = userName;
        this.gender = gender;
    }

    public FirebaseUser getmFirebaseUser() {
        return mFirebaseUser;
    }

    public void setmFirebaseUser(FirebaseUser mFirebaseUser) {
        this.mFirebaseUser = mFirebaseUser;
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
