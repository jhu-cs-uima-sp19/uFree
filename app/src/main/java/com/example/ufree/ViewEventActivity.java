package com.example.ufree;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ViewEventActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private DatabaseReference dbref;
    private long eventIdValue;
    private String participants;
    private TextView timeView;
    private TextView partsView;
    private TextView dateView;
    private User my_user = new User();
    private Event my_event = new Event();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
        String user = "";
        user = sp.getString("userID", "empty");

        final TextView locationView = findViewById(R.id.locationTextView);
        final TextView desciptionView = findViewById(R.id.descriptionTextView);
        partsView = findViewById(R.id.partTextView);
        timeView = findViewById(R.id.timeTextView);
        dateView = findViewById(R.id.dateTextView);

        //initialize our database objects
        db = FirebaseDatabase.getInstance();
        dbref = db.getReference();


        Bundle extras = getIntent().getExtras();

        if (user != "empty" && user != null) {
            dbref.child("users").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    my_user = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if (extras != null) {
            eventIdValue = extras.getLong("id", -1);
            if (eventIdValue != -1 && eventIdValue != -2) {
                dbref.child("events").child(String.valueOf(eventIdValue)).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        my_event = dataSnapshot.getValue(Event.class);
                        if (my_event != null) {
                            locationView.setText(my_event.location);
                            desciptionView.setText(my_event.description);
                            String time = my_event.time.get("hour") + ":" + my_event.time.get("minute");
                            timeView.setText(time);
                            String date = my_event.date.get("month") + "/" + my_event.date.get("day");
                            getParticipants(my_event);
                            dateView.setText(date);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }
    }

    private void getParticipants(Event e) {
        if (e != null && e.participants != null) {
            for (String id : e.participants) {
                dbref.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);
                        String participantsText = String.valueOf(partsView.getText());
                        participantsText += u.getFullName() + "\n";
                        partsView.setText(participantsText);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }
    }

    /** Function corresponds to clicking the accept invite button. */
    public void acceptInviteAction(View v) {
        final String userName = my_user.getEmail().replace("@", "").replace(".", "");
        my_event.invitees.remove(userName);
        my_event.participants.add(userName);
        dbref.child("events").child(String.valueOf(eventIdValue)).setValue(my_event);
        my_user.invites.remove(String.valueOf(eventIdValue));
        my_user.events.put(String.valueOf(eventIdValue), eventIdValue);
        dbref.child("users").child(userName).setValue(my_user);

        Intent intent = new Intent(this, EventsActivity.class);
        startActivity(intent);
    }

    /** Function corresponds to clicking the decline invite button. */
    public void rejectInvitationAction(View v) {
        final String userName = my_user.getEmail().replace("@", "").replace(".", "");
        my_event.invitees.remove(userName);
        dbref.child("events").child(String.valueOf(eventIdValue)).setValue(my_event);
        my_user.invites.remove(String.valueOf(eventIdValue));
        dbref.child("users").child(userName).setValue(my_user);
        Intent intent = new Intent(this, EventsActivity.class);
        startActivity(intent);
    }
}
