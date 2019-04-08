package com.example.ufree;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class NewEventActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private DatabaseReference dbref;
    private EditText locationInput;
    private EditText descriptionInput;
    private long counter;

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

        //initialize the counter
        dbref.child("counters").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Long> counters = (HashMap) dataSnapshot.getValue();
                counter = counters.get("events");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // set up title of app bar
        getSupportActionBar().setTitle("New Event");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_events, menu);
        return true;
    }


    public void addEventAction(View v) {
        System.out.println("executing");
        HashMap<String, Integer> date = new HashMap<>();
        HashMap<String, Integer> time = new HashMap<>();
        ArrayList<Integer> invitees = new ArrayList<>();
        invitees.add(2);

        String location = String.valueOf(locationInput.getText());
        String description = String.valueOf(descriptionInput.getText());

        //add the event to the database then increment the counter
        Event e = new Event(invitees, date, time, location, description);
        dbref.child("events").child(String.valueOf(counter)).setValue(e);
        counter++;
        dbref.child("counters").child("events").setValue(counter);
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
