package com.example.ufree;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/** Public class for handling Event database objects. */
public class Event {
    public ArrayList<String> participants;
    public ArrayList<String> invitees;
    public String location;
    public String description;
    public long time;
    public Long id;

    public Event(ArrayList<String> participants, ArrayList<String> invitees,
                 long t, String l, String desc, Long i) {
        this.participants = participants;
        this.invitees = invitees;
        this.location = l;
        this.time = t;
        this.description = desc;
        this.id = i;
    }

    public Event(){};
}
