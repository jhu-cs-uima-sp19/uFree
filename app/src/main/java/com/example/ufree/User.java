package com.example.ufree;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class User {
    private String email;
    private String fullName;
    private String phone;
    private String profilePic;
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
    private HashMap<String,String> incomingFriends;
    private HashMap<String,String> frienders;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(com.example.ufree.User.class)
    }

    // Constructor for sign up
    public User(String n, String p, String e, HashMap<String, Long> eventsList, HashMap<String, Long> invitesList) {
        this.fullName = n;
        this.phone = p;
        this.email = e;
        this.profilePic = null;
        this.isFree = false;
        this.startTime = 0;
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 30);
        this.endTime = now.getTimeInMillis();
        this.events = eventsList;
        this.incomingFriends = null;
        this.invites = invitesList;
        this.frienders = null;
    }

    public User(User that) {
        this.email = that.email;
        this.fullName = that.fullName;
        this.phone = that.phone;
        this.profilePic = that.profilePic;
        this.startTime = that.startTime;
        this.endTime = that.endTime;
        this.isFree = that.isFree;
        this.events = that.events;
        this.invites = that.invites;
        this.incomingFriends = that.incomingFriends;
        this.frienders = that.frienders;
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

    public String getProfilePic() {
        return this.profilePic;
    }

    public long getStartTime() { return this.startTime; }

    public long getEndTime() {
        return this.endTime;
    }

    public boolean getIsFree() {
        return this.isFree;
    }

    public HashMap<String,String> getIncomingFriends() { return this.incomingFriends; }

    public HashMap<String,String> getFrienders() { return this.frienders; }

    public HashMap<String, Long> getEvents() { return this.events; }

    public HashMap<String, Long> getInvites() {
        return invites;
    }

    public String toString() {
        return "User email: " + this.email + "\nname: " + this.fullName
                + "\nphone : " + this.phone + "\nisFree: " + this.isFree
                + "\nstartTime: " + this.startTime + ", "
                + "\nendTime: " + this.endTime + "\n";
    }

}