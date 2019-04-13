package com.example.ufree;

public class User {
    private String firstName;
    private String lastName;
    // TODO: is phone a long?
    private long phone;
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

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public long getPhone() {
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
        return "User name: " + this.firstName + " " + this.lastName
                + "\nphone : " + this.phone + "\nisFree: " + this.isFree
                + "\nstartTime: " + this.startDay + ", " + this.startHour + ", " + this.startMinute
                + "\nendTime: " + this.endDay + ", " + this.endHour + ", " + this.endMinute + "\n";
    }
}
