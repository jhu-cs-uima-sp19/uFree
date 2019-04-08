package com.example.ufree;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/** Public class for handling Event database objects. */
public class Event {
    public ArrayList<Integer> participants;
    public HashMap<String, Integer> date;
    public HashMap<String, Integer> time;
    public String location;
    public String description;

    public Event(ArrayList<Integer> invitees, HashMap<String, Integer> d,
                 HashMap<String, Integer> t, String l, String desc) {
        this.participants = invitees;
        this.date = d;
        this.time = t;
        this.location = l;
        this.description = desc;
    }
}
