package com.mydroid.notesapp.Model;

public class Users {
    String  uid, email, name;

    public Users() {
    }

    public Users(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
