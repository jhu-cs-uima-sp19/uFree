package com.example.ufree;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.os.health.SystemHealthManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.content.DialogInterface;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.lang.Long;

public class NewEventActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private DatabaseReference dbref;
    private EditText locationInput;
    private EditText descriptionInput;
    private Spinner monthInputSpinner;
    private Spinner hourInputSpinner;
    private EditText dayInput;
    private long eventIdValue = 0;
    private long counter;
    private ArrayList<String> invitees = new ArrayList<>();
    private ArrayList<String> attendees = new ArrayList<>();
    private Event my_event = new Event();
    Bundle extras = null;
    String members = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        //initialize our database objects
        db = FirebaseDatabase.getInstance();
        dbref = db.getReference();

        //initialize the input fields
        locationInput = (EditText) findViewById(R.id.LocationInput);
        descriptionInput = (EditText) findViewById(R.id.DescriptionInput);
        monthInputSpinner = (Spinner) findViewById(R.id.monthSpinner);
        hourInputSpinner = (Spinner) findViewById(R.id.hourSpinner);
        dayInput = (EditText) findViewById(R.id.dayEditText);

        Spinner monthInput = findViewById(R.id.monthSpinner);

        extras = getIntent().getExtras();

        //initialize the counter
        dbref.child("counters").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Long> counters = (HashMap) dataSnapshot.getValue();
                counter = counters.get("events");
                if (eventIdValue == 0) {
                    eventIdValue = counter;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //check if we're editing an existing event
        if (extras != null) {

            members = extras.getString("ids", "none");

            if (members.equals("none")) {
                System.out.println(extras.getLong("id", -2));

                // set up title of app bar
                getSupportActionBar().setTitle("Edit Event");

                eventIdValue = extras.getLong("id", counter);
                Button changeEventButton = findViewById(R.id.CreateEventButton);
                Button deleteEventButton = findViewById(R.id.deleteEventButton);
                changeEventButton.setText("Save Changes");
                deleteEventButton.setVisibility(View.VISIBLE);

                dbref.child("events").child(String.valueOf(eventIdValue)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        my_event = dataSnapshot.getValue(Event.class);
                        TextView attendeesTextView = findViewById(R.id.InviteesTextView);
                        Spinner monthSpinner = findViewById(R.id.monthSpinner);
                        EditText dayEditText = findViewById(R.id.dayEditText);
                        Spinner hourSpinner = findViewById(R.id.hourSpinner);
                        EditText minuteInput = findViewById(R.id.minuteInput);

                        if (my_event != null) {
                            locationInput.setText(my_event.location);
                            descriptionInput.setText(my_event.description);
                            String attendeesText = "";
                            attendees = my_event.participants;

                            if (attendees != null) {
                                for (String p : attendees) {
                                    attendeesText += (p + ", ");
                                }

                                attendeesText = attendeesText.substring(0, attendeesText.length() - 1);
                                attendeesTextView.setText(attendeesText);
                            }

                            dayEditText.setText(String.valueOf(my_event.date.get("day")));
                            minuteInput.setText(String.valueOf(my_event.time.get("minute")));

                            ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                    R.array.monthSpinnerValues, R.layout.support_simple_spinner_dropdown_item);

                            ArrayAdapter<CharSequence> hourAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                    R.array.hourSpinnerValues, R.layout.support_simple_spinner_dropdown_item);

                            monthSpinner.setAdapter(monthAdapter);
                            hourSpinner.setAdapter(hourAdapter);

                            hourSpinner.setSelection(my_event.time.get("hour"));
                            monthSpinner.setSelection(my_event.date.get("month") - 1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            } else {
                String[] friends = members.split(" ");

                for (int i = 0; i < friends.length; i++) {
                    invitees.add(friends[i]);
                }

                TextView inviteesTextView = findViewById(R.id.InviteesTextView);

                String content = "";
                for (int i = 0; i < friends.length; i++) {
                    content = content + friends[i] + ", ";
                }

                content = content.substring(0, content.length() - 2);
                inviteesTextView.setText(content);
            }
        } else {
            System.out.println("no extras!");
            descriptionInput.setText("event title");
            locationInput.setText("event location");

            // set up title of app bar
            getSupportActionBar().setTitle("New Event");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_events, menu);
        return true;
    }

    private Integer convertTimeToInt(String time) {
        if (time.equals("January") || time.equals("1 AM")) {
            return 1;
        } else if (time.equals("February") || time.equals("2 AM")) {
            return 2;
        } else if (time.equals("March") || time.equals("3 AM")) {
            return 3;
        } else if (time.equals("April") || time.equals("4 AM")) {
            return 4;
        } else if (time.equals("May") || time.equals("5 AM")) {
            return 5;
        } else if (time.equals("June") || time.equals("6 AM")) {
            return 6;
        } else if (time.equals("July") || time.equals("7 AM")) {
            return 7;
        } else if (time.equals("August") || time.equals("8 AM")) {
            return 8;
        } else if (time.equals("September") || time.equals("9 AM")) {
            return 9;
        } else if (time.equals("October") || time.equals("10 AM")) {
            return 10;
        } else if (time.equals("November") || time.equals("11 AM")) {
            return 11;
        } else if (time.equals("December") || time.equals("12 PM")) {
            return 12;
        } else if (time.equals("12 AM") || time.equals("00")) {
            return 0;
        } else if (time == null) {
          return -2;
        } else if (time.equals("1 PM")) {
            return 13;
        } else if (time.equals("2 PM")) {
            return 14;
        } else if (time.equals("3 PM")) {
            return 15;
        } else if (time.equals("4 PM")) {
            return 16;
        } else if (time.equals("5 PM")) {
            return 17;
        } else if (time.equals("6 PM")) {
            return 18;
        } else if (time.equals("7 PM")) {
            return 19;
        } else if (time.equals("8 PM")) {
            return 20;
        } else if (time.equals("9 PM")) {
            return 21;
        } else if (time.equals("10 PM")) {
            return 22;
        } else if (time.equals("11 PM")) {
            return 23;
        } else {
            return -2;
        }
    }

    private boolean checkDate(Event e) {
        Date d = new Date();
        final long time = d.getTime() - d.getTime() / (2019 - 1970);
        final double msMonth = 26298E5;
        final double msDay = 86400000;
        final double msHour = 3600000;
        final double msMinute = 60000;
        double event_time = e.date.get("month") * msMonth + e.date.get("day") * msDay
                + e.time.get("hour") * msHour * e.time.get("minute") * msMinute;

        return event_time > time;
    }

    public void addEventAction(View v) {
        System.out.println("Trying to execute action");
        HashMap<String, Integer> date = new HashMap<>();
        HashMap<String, Integer> time = new HashMap<>();
        EditText minuteInput = findViewById(R.id.minuteInput);

        String location = String.valueOf(locationInput.getText());
        String description = String.valueOf(descriptionInput.getText());
        String hour = String.valueOf(hourInputSpinner.getSelectedItem());
        String month = String.valueOf(monthInputSpinner.getSelectedItem());
        Integer day = 0;
        Integer minute = 0;

        try {
            day = Integer.parseInt(String.valueOf(dayInput.getText()));
        } catch (NumberFormatException e) {
            AlertDialog alertDialog = new AlertDialog.Builder(NewEventActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Invalid Time");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return;
        }

        try {
            minute = Integer.parseInt(String.valueOf(minuteInput.getText()));
        } catch (NumberFormatException e) {
            AlertDialog alertDialog = new AlertDialog.Builder(NewEventActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Invalid Time");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return;
        }

        date.put("month",convertTimeToInt(month));
        date.put("day", day);
        time.put("hour", convertTimeToInt(hour));
        time.put("minute", minute);

        //add the event to the database then increment the counter
        if (invitees.size() > 0 && minute < 59 && minute > 0 && extras == null || !members.equals("")) {
            System.out.println("making new event");
            SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
            String user = sp.getString("userID", "none");

            if (attendees.size() == 0) {
                attendees.add(user);
            }

            Event e = new Event(attendees, invitees, date, time, location,
                    description, eventIdValue);


            dbref.child("events").child(String.valueOf(eventIdValue)).setValue(e);

            if (eventIdValue == counter) {
                System.out.println("increment counter");
                dbref.child("events").child(String.valueOf(counter)).setValue(e);
                counter++;
                dbref.child("counters").child("events").setValue(counter);
            } else {
                System.out.println(counter);
            }

            for (String u : invitees) {
                u = u.replace("@", "").replace(".", "");
                dbref.child("users").child(u).child("invites").child(String.valueOf(eventIdValue)).setValue(eventIdValue);
            }

            if (!user.equals("none")) {
                dbref.child("users").child(user).child("events").child(String.valueOf(eventIdValue)).setValue(eventIdValue);
            }

            //return to the events page
            Intent intent = new Intent(this, EventsActivity.class);
            startActivity(intent);
        } else if (minute < 0 || minute > 59) {
            AlertDialog alertDialog = new AlertDialog.Builder(NewEventActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Invalid Time");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else if (eventIdValue != 0) {
            System.out.println(eventIdValue);
            my_event.date = new HashMap<>();
            my_event.date.put("month", convertTimeToInt(month));
            my_event.date.put("day", day);
            my_event.time = new HashMap<>();
            my_event.time.put("hour", convertTimeToInt(hour));
            my_event.time.put("minute", minute);
            my_event.location = location;
            my_event.participants = attendees;
            my_event.description = description;
            my_event.invitees = invitees;
            dbref.child("events").child(String.valueOf(eventIdValue)).setValue(my_event);

            Intent intent = new Intent(this, EventsActivity.class);
            startActivity(intent);
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(NewEventActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("You must invite others to your event");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    /** adds invitation. */
    public void addInviteAction(View v) {
        EditText searchUsersEditText = findViewById(R.id.searchUsersInput);
        final TextView inviteesTextView = findViewById(R.id.InviteesTextView);
        final String userName = String.valueOf(searchUsersEditText.getText());
        SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
        final String me = sp.getString("userID", "none");

        //search through logged-in users friends to add invites
        dbref.child("users").child(me).child("frienders").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;
                if (dataSnapshot.getValue() != null) {
                    System.out.println(dataSnapshot.getValue());
                    HashMap<String, String> myFriends = (HashMap<String, String>) dataSnapshot.getValue();
                    for (String f : myFriends.values()) {
                        if (f.equals(userName)) {
                            found = true;
                            break;
                        }
                    }

                    String inviteesStringVal = String.valueOf(inviteesTextView.getText());
                    if (invitees.size() == 0 && found) {
                        invitees.add(userName);
                        inviteesTextView.setText(userName);
                    } else if (found) {
                        invitees.add(userName);
                        inviteesStringVal = inviteesStringVal + ", " + userName;
                        inviteesStringVal = inviteesStringVal.substring(0, inviteesStringVal.length() - 2);
                        inviteesTextView.setText(inviteesStringVal);
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(NewEventActivity.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Sorry! We can't find that friend");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void deleteEventAction(View v) {
        SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
        final String user = sp.getString("userID", "none");

        if (!user.equals("none")) {
            dbref.child("events").child(String.valueOf(eventIdValue)).child("participants").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> participants = (ArrayList<String>) dataSnapshot.getValue();
                    long index = 0;
                    for (int i = 0; i < participants.size(); i++) {
                        if (participants.get(i).equals(user)) {
                            index = i;
                        }
                    }

                    dbref.child("events").child(String.valueOf(eventIdValue)).child("participants").child(String.valueOf(index)).removeValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            dbref.child("users").child(user).child("events").child(String.valueOf(eventIdValue)).removeValue();
        }

        Intent intent = new Intent(this, EventsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.close_newEvent) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
