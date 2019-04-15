package com.example.ufree;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;

public class ViewEventActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private DatabaseReference dbref;
    private long eventIdValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        final TextView locationView = findViewById(R.id.locationTextView);
        final TextView desciptionView = findViewById(R.id.descriptionTextView);
        TextView dateView = findViewById(R.id.dateTextView);
        TextView timeView = findViewById(R.id.timeTextView);
        TextView friendsView = findViewById(R.id.partTextView);
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
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }
    }
}
