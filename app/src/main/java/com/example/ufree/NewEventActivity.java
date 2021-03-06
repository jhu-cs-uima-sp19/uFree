package com.example.ufree;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.os.health.SystemHealthManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.content.DialogInterface;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.lang.Long;

public class NewEventActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private DatabaseReference dbref;
    private EditText locationInput;
    private EditText descriptionInput;
    private long eventIdValue = 0;
    private long counter;
    private Button timeButton;
    private static Calendar selectedCalendar;
    static final java.text.DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    static final java.text.DateFormat dateFormat = new SimpleDateFormat("MMM dd, EEE");
    private HashMap<String, String> invitees = new HashMap<>();
    private HashMap<String, String> attendees = new HashMap<>();
    private Event my_event = new Event();
    private static long selectedTime;
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
        timeButton = findViewById(R.id.timeButton_event);

        // initialize event time to be 30 minutes after current time
        selectedCalendar = Calendar.getInstance();
        selectedCalendar.add(Calendar.MINUTE, 30);

        timeButton.setText(timeFormat.format(selectedCalendar.getTime()));
        Button dateButton = findViewById(R.id.dateButton_event);
        dateButton.setText(dateFormat.format(selectedCalendar.getTime()));

        extras = getIntent().getExtras();

        // set up title of app bar
        getSupportActionBar().setTitle("New Event");

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
                // set up title of app bar
                getSupportActionBar().setTitle("Edit Event");

                eventIdValue = extras.getLong("id", counter);
                Button changeEventButton = findViewById(R.id.CreateEventButton);
                Button deleteEventButton = findViewById(R.id.deleteEventButton);
                changeEventButton.setText("Save Changes");
                deleteEventButton.setVisibility(View.VISIBLE);

                dbref.child("events").child(String.valueOf(eventIdValue)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        my_event = dataSnapshot.getValue(Event.class);
                        TextView attendeesTextView = findViewById(R.id.InviteesTextView);

                        if (my_event != null) {
                            locationInput.setText(my_event.location);
                            descriptionInput.setText(my_event.description);
                            String attendeesText = "";
                            attendees = my_event.participants;

                            if (attendees != null) {
                                for (String p : attendees.values()) {
                                    if (!p.equals("null1") && !p.equals("null2")) {
                                        attendeesText += (p + ", ");
                                    }
                                }

                                if (attendeesText.length() > 0) {
                                    attendeesText = attendeesText.substring(0, attendeesText.length() - 1);
                                    attendeesTextView.setText(attendeesText);
                                }
                            }

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(my_event.time);
                            selectedCalendar.setTimeInMillis(my_event.time);
                            timeButton.setText(timeFormat.format(calendar.getTime()));
                            Button dateButton = findViewById(R.id.dateButton_event);
                            dateButton.setText(dateFormat.format(selectedCalendar.getTime()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            } else {
                String[] friends = members.split(" ");

                for (int i = 0; i < friends.length; i++) {
                    invitees.put(friends[i], friends[i]);
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
            descriptionInput.setText("event title");
            locationInput.setText("event location");

            // set up title of app bar
            getSupportActionBar().setTitle("New Event");
        }
    }

    // Time picker for time button at ** BOTTOM **
    public static class TimePickerFragmentBottom extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of TimePickerDialog and return it
            int selectedHour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
            int selectedMinute = selectedCalendar.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, selectedHour, selectedMinute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // update selected calendar object
            selectedCalendar.set(selectedCalendar.get(Calendar.YEAR),
                    selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH),
                    hourOfDay, minute, 0);
            Calendar now = Calendar.getInstance();
            if (selectedCalendar.getTimeInMillis() < now.getTimeInMillis()) {
                Toast.makeText(getContext(), "You cannot select time before current time", Toast.LENGTH_SHORT).show();
                DialogFragment datePickerFragment = new NewEventActivity.TimePickerFragmentBottom();
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "timePickerBottom");
            } else {
                // change text view for time button
                Button timeButton = getActivity().findViewById(R.id.timeButton_event);
                timeButton.setText(timeFormat.format(selectedCalendar.getTime()));
            }
        }
    }

    public void showTimePickerDialogBottom(View v) {
        DialogFragment timePickerFragment = new NewEventActivity.TimePickerFragmentBottom();
        timePickerFragment.show(getSupportFragmentManager(), "timePickerBottom");
    }


    /* Date picker for date button at BOTTOM */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            int year = selectedCalendar.get(Calendar.YEAR);
            int month = selectedCalendar.get(Calendar.MONTH);
            int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            Calendar today = Calendar.getInstance();
            Calendar chosen = Calendar.getInstance();
            chosen.set(year, month, day, selectedCalendar.get(Calendar.HOUR_OF_DAY), selectedCalendar.get(Calendar.MINUTE));
            int chosenDay = chosen.get(Calendar.DAY_OF_YEAR);
            int nowDay = today.get(Calendar.DAY_OF_YEAR);
            // the user can only choose today or tomorrow
            if (chosenDay - nowDay == 1
                    || (chosenDay == nowDay)
                    || ((nowDay == 365 || nowDay == 366) && chosenDay == 1 )) {
                if (today.getTimeInMillis() > chosen.getTimeInMillis()) {
                    Toast.makeText(getContext(), "You cannot select time before current time", Toast.LENGTH_SHORT).show();
                    DialogFragment datePickerFragment = new NewEventActivity.DatePickerFragment();
                    datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                } else {
                    selectedCalendar.set(
                            year, month, day,
                            selectedCalendar.get(Calendar.HOUR_OF_DAY),
                            selectedCalendar.get(Calendar.HOUR_OF_DAY), 0);

                    Button dateButton = getActivity().findViewById(R.id.dateButton_event);
                    dateButton.setText(dateFormat.format(selectedCalendar.getTime()));
                }
            } else {
                Toast.makeText(getContext(), "You can only select today or tomorrow", Toast.LENGTH_SHORT).show();
                DialogFragment datePickerFragment = new NewEventActivity.DatePickerFragment();
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new NewEventActivity.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_events, menu);
        return true;
    }

    public void addEventAction(View v) {
        EditText inviteEditText = (EditText) findViewById(R.id.searchUsersInput);

        String location = String.valueOf(locationInput.getText());
        String description = String.valueOf(descriptionInput.getText());
        String search = String.valueOf(inviteEditText.getText());

        long selectedTime = selectedCalendar.getTimeInMillis();

        if (location.equals("") || description.equals("") || search.equals("")) {
            AlertDialog alertDialog = new AlertDialog.Builder(NewEventActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Invalid input");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            return;
        } else if (invitees.size() == 0 && extras == null) {
            AlertDialog alertDialog = new AlertDialog.Builder(NewEventActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("You must invite members");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            return;
        }

        //add the event to the database then increment the counter
        if (invitees.size() > 0 && extras == null || !members.equals("")) {
            System.out.println("making new event");
            SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
            String user = sp.getString("userID", "none");

            if (attendees.size() == 0) {
                attendees.put("null1", "null1");
                attendees.put("null2", "null2");
                attendees.put(user, user);
            }

            invitees.put("null1", "null1");
            invitees.put("null2", "null2");

            Event e = new Event(attendees, invitees, selectedTime, location, description, eventIdValue);
            dbref.child("events").child(String.valueOf(eventIdValue)).setValue(e);

            counter++;
            dbref.child("counters").child("events").setValue(counter);

            for (String u : invitees.values()) {
                if (!u.equals("null1") && !u.equals("null2")) {
                    u = u.replace("@", "").replace(".", "");
                    dbref.child("users").child(u).child("invites").child(String.valueOf(eventIdValue)).setValue(eventIdValue);
                }
            }

            if (!user.equals("none")) {
                dbref.child("users").child(user).child("events").child(String.valueOf(eventIdValue)).setValue(eventIdValue);
            }

            //return to the events page
            Intent intent = new Intent(this, EventsActivity.class);
            startActivity(intent);
        } else if (eventIdValue != 0 && my_event != null) {
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
        dbref.child("users").child(me).child("frienders").addValueEventListener(new ValueEventListener() {

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
                    String newUserName = userName.replace("@", "").replace(".", "");
                    if (invitees.containsKey(newUserName) || attendees.containsKey(newUserName)) {
                        AlertDialog alertDialog = new AlertDialog.Builder(NewEventActivity.this).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Cannot add friend more than once");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else if (invitees.size() == 0 && found) {
                        invitees.put(newUserName, newUserName);
                        inviteesTextView.setText(userName);
                    } else if (found) {
                        invitees.put(newUserName, newUserName);
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
            dbref.child("events").child(String.valueOf(eventIdValue)).child("participants").child(user).removeValue();
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
