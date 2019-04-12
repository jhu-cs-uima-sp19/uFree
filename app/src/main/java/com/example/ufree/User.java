package com.example.ufree;

/** User class stores data corresponding to new user and
 * manages database user elements
 */
public class User {
    private String firstName;
    private String lastName;
    private Integer[] friends;
    private String email;
    private String phone;
    private Integer startMinute;
    private Integer startHour;

    public User(String n, String l, String e, Integer sm, Integer sh) {
        this.firstName = n;
        this.lastName = l;
        this.email = e;
        this.startMinute = sm;
        this.startHour = sh;
    }

    public String getEmail() {
        return this.email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {return this.lastName; }

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
