package com.example.ufree;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class EventsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseDatabase db;
    private DatabaseReference dbref;
    private RecyclerView eventsRecyclerView;
    private ArrayList<Event> events = new ArrayList<>();
    private Integer selectedEventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseDatabase.getInstance();
        dbref = db.getReference();
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set up title of app bar
        getSupportActionBar().setTitle("Events");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_events);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NewEventActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // set Events to be selected
        navigationView.getMenu().getItem(1).setChecked(true);

        //create recycler and set adapter
        eventsRecyclerView = findViewById(R.id.EventsRecyclerView);
        final CustomAdapter recyclerAdapter = new CustomAdapter(events);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        eventsRecyclerView.setLayoutManager(mLayoutManager);
        eventsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        eventsRecyclerView.setAdapter(recyclerAdapter);

        //initialize the counter
        dbref.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, Object>> dbEvents =
                        (HashMap<String, HashMap<String, Object>>) dataSnapshot.getValue();

                for (HashMap<String, Object> dbEvent : dbEvents.values()) {
                    if (dbEvent != null) {
                        System.out.println(dbEvent);
                        Event e = new Event();
                        e.description = (String) dbEvent.get("description");
                        e.location = (String) dbEvent.get("location");
                        e.time = (HashMap<String, Integer>) dbEvent.get("time");
                        e.date = (HashMap<String, Integer>) dbEvent.get("date");
                        e.id = Long.valueOf(String.valueOf(dbEvent.get("id")));
                        events.add(e);
                    }

                    recyclerAdapter.notifyDataSetChanged();
                }

                HashMap<String, Object> m = (HashMap<String, Object>) dataSnapshot.getValue();
                System.out.println(m.get("3"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_events) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.whosFree_nav) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.friends_nav) {

        } else if (id == R.id.profile_nav) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /** function to handle click events for recyclerview items. */
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
