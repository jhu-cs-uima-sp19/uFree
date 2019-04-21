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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewEventActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private DatabaseReference dbref;
    private long eventIdValue;
    private String participants;
    private TextView timeView;
    private TextView partsView;
    private TextView dateView;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
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

        if (extras != null) {
            eventIdValue = extras.getLong("id", -1);
            if (eventIdValue != -1 && eventIdValue != -2) {
                dbref.child("events").child(String.valueOf(eventIdValue)).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Event e = dataSnapshot.getValue(Event.class);
                        if (e != null) {
                            locationView.setText(e.location);
                            desciptionView.setText(e.description);
                            String time = e.time.get("hour") + ":" + e.time.get("minute");
                            timeView.setText(time);
                            String date = e.date.get("month") + "/" + e.date.get("day");
                            getParticipants(e);
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
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        }
    }

    public void acceptInviteAction(View v) {
        dbref.child("users").child(user).child("events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Long> events = (HashMap<String, Long>) dataSnapshot.getValue();
                events.put(String.valueOf(eventIdValue), eventIdValue);
                dbref.child("users").child(user).child("events").setValue(events);
                dbref.child("users").child(user).child("invites").child(String.valueOf(eventIdValue)).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Intent intent = new Intent(this, EventsActivity.class);
        startActivity(intent);
    }
}
