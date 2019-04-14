package com.example.ufree;

public class User {
    private String email;
    private String fullName;
    private String phone;
    private int startDay;
    private int startHour;
    private int startMinute;
    private int endDay;
    private int endHour;
    private int endMinute;
    private boolean isFree;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(com.example.ufree.User.class)
    }

    // Constructor for sign up
    public User(String n, String p, String e) {
        this.fullName = n;
        this.phone = p;
        this.email = e;
        this.isFree = false;
        this.startDay = 0;
        this.startHour = 0;
        this.startMinute = 0;
        this.endDay = 0;
        this.endHour = 0;
        this.endMinute = 0;
    }

    public User(User that) {
        this.email = that.email;
        this.fullName = that.fullName;
        this.phone = that.phone;
        this.startDay = that.startDay;
        this.startHour = that.startHour;
        this.startMinute = that.startMinute;
        this.endDay = that.endDay;
        this.endHour = that.endHour;
        this.endMinute = that.endMinute;
        this.isFree = that.isFree;
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

    public int getStartDay() {
        return this.startDay;
    }

    public int getStartHour() {
        return this.startHour;
    }

    public int getStartMinute() {
        return this.startMinute;
    }

    public int getEndDay() {
        return this.endDay;
    }

    public int getEndHour() {
        return this.endHour;
    }

    public int getEndMinute() {
        return this.endMinute;
    }

    public boolean getIsFree() {
        return this.isFree;
    }

    public int getEndTime() {
        return this.endHour * 60 + this.endMinute;
    }


    public String toString() {
        return "User email: " + this.email + "\nname: " + this.fullName
                + "\nphone : " + this.phone + "\nisFree: " + this.isFree
                + "\nstartTime: " + this.startDay + ", " + this.startHour + ", " + this.startMinute
                + "\nendTime: " + this.endDay + ", " + this.endHour + ", " + this.endMinute + "\n";
    }

}