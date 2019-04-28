package com.example.ufree;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private String email;
    private String fullName;
    private String phone;
    public HashMap<String, Long> events;
    public HashMap<String, Long> invites;
    private int startDay;
    private int startHour;
    private int startMinute;
    private int endDay;
    private int endHour;
    private int endMinute;
    private long startTime;
    private long endTime;
    private boolean isFree;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(com.example.ufree.User.class)
    }

    // Constructor for sign up
    public User(String n, String p, String e, HashMap<String, Long> eventsList) {
        this.fullName = n;
        this.phone = p;
        this.email = e;
        this.isFree = false;
        this.startTime = 0;
        this.endTime = 0;
        this.events = eventsList;
    }

    public User(User that) {
        this.email = that.email;
        this.fullName = that.fullName;
        this.phone = that.phone;
        this.startTime = that.startTime;
        this.endTime = that.endTime;
        this.isFree = that.isFree;
        this.events = that.events;
    }

    public String getEmail() {
        return this.email;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getPhone() {
        return this.phone;
    }

    public long getStartTime() { return this.startTime; }

    public long getEndTime() {
        return this.endTime;
    }

    public boolean getIsFree() {
        return this.isFree;
    }


    public String toString() {
        return "User email: " + this.email + "\nname: " + this.fullName
                + "\nphone : " + this.phone + "\nisFree: " + this.isFree
                + "\nstartTime: " + this.startTime + ", "
                + "\nendTime: " + this.endTime + "\n";
    }

}