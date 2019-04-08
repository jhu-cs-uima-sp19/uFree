package com.example.ufree;

/** User class stores data corresponding to new user and
 * manages database user elements
 */
public class User {
    private String name;
    private Integer[] friends;
    private String email;
    private String phone;
    private String password="";
    private Integer startMinute;
    private Integer startHour;

    public User(String n, String e, Integer sm, Integer sh) {
        this.name = n;
        this.email = e;
        this.password="";
        this.startMinute = sm;
        this.startHour = sh;
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    public String getPhone() {
        return this.phone;
    }

    public Integer getStartMinute() {
        return this.startMinute;
    }

    public Integer getStartHour() {
        return this.startHour;
    }

    public Integer[] getFriends() {
        return this.friends;
    }
}
