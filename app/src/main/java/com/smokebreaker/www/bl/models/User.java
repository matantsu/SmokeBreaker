package com.smokebreaker.www.bl.models;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    String uid;
    String displayName;
    String photoUrl;
    boolean isOnline;

    public User() {
    }

    public User(FirebaseUser currentUser) {
        this.uid = currentUser.getUid();
        displayName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : currentUser.getEmail();
        photoUrl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
        isOnline = false;
    }

    //region g&s

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    //endregion
}
