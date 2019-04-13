package com.example.ufree;

public class User {
    private String email;
    private String firstName;
    private String lastName;
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

    public User(User that) {
        this.email = that.email;
        this.firstName = that.firstName;
        this.lastName = that.lastName;
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

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
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
        return "User email: " + this.email + "\nname: " + this.firstName + " " + this.lastName
                + "\nphone : " + this.phone + "\nisFree: " + this.isFree
                + "\nstartTime: " + this.startDay + ", " + this.startHour + ", " + this.startMinute
                + "\nendTime: " + this.endDay + ", " + this.endHour + ", " + this.endMinute + "\n";
    }
}