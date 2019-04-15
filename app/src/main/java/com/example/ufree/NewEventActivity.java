package com.example.ufree;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Long;

public class NewEventActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private DatabaseReference dbref;
    private EditText locationInput;
    private EditText descriptionInput;
    private Spinner monthInputSpinner;
    private Spinner hourInputSpinner;
    private Spinner minuteInputSpinner;
    private EditText dayInput;
    private long eventIdValue;
    private long counter;
    ArrayList<String> invitees = new ArrayList<>();

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
        minuteInputSpinner = (Spinner) findViewById(R.id.minuteSpinner);
        dayInput = (EditText) findViewById(R.id.dayEditText);

        Spinner monthInput = findViewById(R.id.monthSpinner);

        Bundle extras = getIntent().getExtras();

        //initialize the counter
        dbref.child("counters").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Long> counters = (HashMap) dataSnapshot.getValue();
                counter = counters.get("events");
                if (eventIdValue == 0) {
                    setValues();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        if (extras != null) {
            eventIdValue = extras.getLong("id", counter);
            System.out.println("ID IS: " + eventIdValue);
            dbref.child("events").child(String.valueOf(eventIdValue)).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    HashMap<String, Object> event = (HashMap<String, Object>) dataSnapshot.getValue();
                    if (event != null) {
                        locationInput.setText((String) event.get("location"));
                        descriptionInput.setText((String) event.get("description"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        } else {
            descriptionInput.setText("event title");
            locationInput.setText("event location");
        }

        // set up title of app bar
        getSupportActionBar().setTitle("New Event");
    }

    private void setValues() {
        eventIdValue = counter;
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

    public void addEventAction(View v) {
        HashMap<String, Integer> date = new HashMap<>();
        HashMap<String, Integer> time = new HashMap<>();

        String location = String.valueOf(locationInput.getText());
        String description = String.valueOf(descriptionInput.getText());
        String hour = String.valueOf(hourInputSpinner.getSelectedItem());
        String minute = String.valueOf(minuteInputSpinner.getSelectedItem());
        String month = String.valueOf(monthInputSpinner.getSelectedItem());
        Integer day = Integer.parseInt(String.valueOf(dayInput.getText()));

        date.put("month",convertTimeToInt(month));
        date.put("day", day);
        time.put("hour", convertTimeToInt(hour));
        time.put("minute", convertTimeToInt(minute));

        //add the event to the database then increment the counter
        if (invitees.size() > 0) {
            Event e = new Event(invitees, date, time, location, description, eventIdValue);

            dbref.child("events").child(String.valueOf(eventIdValue)).setValue(e);

            if (eventIdValue == counter) {
                dbref.child("events").child(String.valueOf(counter)).setValue(e);
                counter++;
                dbref.child("counters").child("events").setValue(counter);
            }

            SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
            String user = sp.getString("userID", "none");

            for (String u : invitees) {
                dbref.child("users").child(u).child("events").child(String.valueOf(eventIdValue)).setValue(eventIdValue);
            }

            if (!user.equals("none")) {
                dbref.child("users").child(user).child("events").child(String.valueOf(eventIdValue)).setValue(eventIdValue);
            }

            //return to the events page
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

    public void addInviteAction(View v) {
        EditText searchUsersEditText = findViewById(R.id.searchUsersInput);
        TextView inviteesTextView = findViewById(R.id.InviteesTextView);
        String userName = String.valueOf(searchUsersEditText.getText());
        String email = userName;
        userName = userName.replace("@", "");
        userName = userName.replace(".", "");

        if (dbref.child("users").child(userName) != null) {
            invitees.add(userName);
            String invitees = String.valueOf(inviteesTextView.getText());
            if (invitees.equals("attendees appear here")) {
                inviteesTextView.setText(userName);
            } else {
                invitees = invitees + ", " + email;
                inviteesTextView.setText(invitees);
            }
        }
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

    public void removeEventAction(View v) {
        dbref.child("events").child(String.valueOf(eventIdValue)).removeValue();
        //return to the events page
        Intent intent = new Intent(this, EventsActivity.class);
        startActivity(intent);
    }
}
