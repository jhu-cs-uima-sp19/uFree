package com.example.ufree;

import java.util.ArrayList;

/** User class stores data corresponding to new user and
 * manages database user elements
 */
public class User {
    private String firstName;
    private String lastName;
    private String phone;
    private String birthday;
    private Integer startMinute;
    private Integer startHour;
    private ArrayList<String> friends = new ArrayList<>();

    public User(String n, String l, String p, String b, Integer sm, Integer sh) {
        this.firstName = n;
        this.lastName = l;
        this.phone = p;
        this.birthday = b;
        this.startMinute = sm;
        this.startHour = sh;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {return this.lastName; }

    public String getPhone() {
        return this.phone;
    }

    public String getBirthday() {return this.birthday; }

    public Integer getStartMinute() {
        return this.startMinute;
    }

    public Integer getStartHour() {
        return this.startHour;
    }

    public ArrayList<String> getFriends() {
        return this.friends;
    }
}
