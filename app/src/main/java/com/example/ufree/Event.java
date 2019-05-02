package com.example.ufree;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/** Public class for handling Event database objects. */
public class Event {
    public ArrayList<String> participants;
    public ArrayList<String> invitees;
    public HashMap<String, Integer> date;
    public HashMap<String, Integer> time;
    public String location;
    public String description;
    public Long id;

    public Event(ArrayList<String> participants, ArrayList<String> invitees,
                 HashMap<String, Integer> d, HashMap<String, Integer> t,
                 String l, String desc, Long i) {
        this.participants = participants;
        this.invitees = invitees;
        this.date = d;
        this.time = t;
        this.location = l;
        this.description = desc;
        this.id = i;
    }

    public Event(){};
}
