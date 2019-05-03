package com.example.ufree;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class SingleFriendEventsActivity extends AppCompatActivity {
    private FirebaseDatabase db;
    private DatabaseReference dbref;
    private RecyclerView eventsRecyclerView;
    private ArrayList<Event> events = new ArrayList<>();
    private HashMap<String, Long> eventRefs = new HashMap<>();
    private String user;
    private String friendID = "null";
    CustomAdapter recyclerAdapter = new CustomAdapter(events);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_friend_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // set up back navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // set up title of app bar
        getSupportActionBar().setTitle("Events with a friend");

        db = FirebaseDatabase.getInstance();
        dbref = db.getReference();

        Bundle extras = getIntent().getExtras();
        String email = extras.getString("friendEmail", "missing");

        if (!email.equals("missing")) {
            friendID = email.replace("@", "");
            friendID = friendID.replace(".", "");
        }

        //create recycler and set adapter
        eventsRecyclerView = findViewById(R.id.SingleFriendRecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        eventsRecyclerView.setLayoutManager(mLayoutManager);
        eventsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        eventsRecyclerView.setAdapter(recyclerAdapter);

        SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
        user = sp.getString("userID", "empty");

        if (!user.equals("empty")) {
            dbref.child("users").child(user).child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    System.out.println("found events!");
                    eventRefs = (HashMap<String, Long>) dataSnapshot.getValue();
                    callBack();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_singleFriendEvents);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NewEventActivity.class);
                startActivity(intent);
            }
        });

    }

    private void callBack() {
        System.out.println("events array of size: " + eventRefs.size());
        for (long id : eventRefs.values()) {
            //find commonality
            if (id != -1 && id != -2) {
                dbref.child("events").child(String.valueOf(id)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Event e = dataSnapshot.getValue(Event.class);
                        if (e.participants != null && e.participants.containsKey(friendID)) {
                            events.add(e);
                            recyclerAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.single_friend_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void viewEventAction(View view) {
        TextView selectedItemDescription = (TextView) view.findViewById(R.id.description);
        TextView selectedItemLocation = (TextView) view.findViewById(R.id.location);
        TextView selectedItemId = (TextView) view.findViewById(R.id.id);

        if (selectedItemDescription != null && selectedItemLocation != null) {
            Long id = Long.valueOf(String.valueOf(selectedItemId.getText()));
            Intent intent = new Intent(this, NewEventActivity.class);
            Bundle extras = new Bundle();
            extras.putLong("id", id);
            intent.putExtras(extras);
            startActivity(intent);
        }
    }

}
