package com.example.ufree;

import java.util.ArrayList;

/** User class stores data corresponding to new user and
 * manages database user elements
 */
public class User {
    private String fullName;
    private String phone;
    private String email;
    private boolean isFree;
    private ArrayList<String> friends = new ArrayList<>();

    public User(String n, String p, String e) {
        this.fullName = n;
        this.phone = p;
        this.email = e;
        this.isFree = false;
    }

    public String getFullName() { return this.fullName; }

    public String getPhone() {
        return this.phone;
    }

    public String getEmail() { return this.email; }

    public boolean getIsFree() { return this.isFree; }

    public ArrayList<String> getFriends() {
        return this.friends;
    }
}
