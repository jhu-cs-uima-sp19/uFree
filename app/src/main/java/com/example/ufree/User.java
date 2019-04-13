package com.example.ufree;

import java.util.ArrayList;

/** User class stores data corresponding to new user and
 * manages database user elements
 */
public class User {
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private boolean isFree;
    private ArrayList<String> friends = new ArrayList<>();

    public User(String n, String l, String p, String e) {
        this.firstName = n;
        this.lastName = l;
        this.phone = p;
        this.email = e;
        this.isFree = false;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {return this.lastName; }

    public String getPhone() {
        return this.phone;
    }

    public String getEmail() { return this.email; }

    public boolean getIsFree() { return this.isFree; }

    public ArrayList<String> getFriends() {
        return this.friends;
    }
}
