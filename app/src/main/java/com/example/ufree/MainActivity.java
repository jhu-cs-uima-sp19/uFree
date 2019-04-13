package com.example.ufree;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    User currentUser;
    boolean checkedAvailability;
    HashMap<String, User> freeFriends = new HashMap<String, User>();
    static int selectedDay;
    static int selectedHour;
    static int selectedMinute;
    static java.text.DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    static boolean dummyUserIsFree = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Set up App bar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // set up title of app bar
        getSupportActionBar().setTitle("Who's Free");

        /* Set up floating add button */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NewEventActivity.class);
                startActivity(intent);
            }
        });

        // TODO: suppress warning
        fab.setVisibility(View.GONE);

        /* Set up navigation drawer */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // set Who's Free to be selected
        navigationView.getMenu().getItem(0).setChecked(true);

        /* Check if need to ask user availability */
        // initialize firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbRef = database.getReference();
        // TODO: get user id
        String userId = "minqitest";
        // Record if user has been asked for availability
        checkedAvailability = false;

        dbRef.child("users").child(userId).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentUser = dataSnapshot.getValue(User.class);
                        Log.d("user", currentUser.toString());
                        // if user has been asked for availability, do NOT ask again
                        if (!checkedAvailability) {
                            Calendar calendar = Calendar.getInstance();
                            int currentDay = calendar.get(Calendar.DAY_OF_YEAR);
                            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                            int currentMinute = calendar.get(Calendar.MINUTE);
                            int currentTime = currentHour * 60 + currentMinute;
                            // if user is free and end time does not exceed current time, do NOT ask for availability
                            // else show welcome screen
                            if (!(currentUser.getIsFree()
                                    && ((currentUser.getEndDay() > currentDay)
                                    || (currentUser.getEndDay() == currentDay && currentUser.getEndTime() >= currentTime)))) {
                                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                                startActivity(intent);
                            }
                            checkedAvailability = true;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("firebase", "loadUserFreeTime:onCancelled", databaseError.toException());
                    }
                }
        );

        /* Set time machine to be current time */
        Calendar calendar = Calendar.getInstance();
        selectedDay = calendar.get(Calendar.DAY_OF_YEAR);
        selectedHour = calendar.get(Calendar.HOUR_OF_DAY);
        selectedMinute = calendar.get(Calendar.MINUTE);
        Log.d("time", "current day: " + selectedDay);
        Log.d("time", "current hour: " + selectedHour);
        Log.d("time", "current minute: " + selectedMinute);
        Button timeButton = findViewById(R.id.timeButton_main);
        Time selectedTime = new Time(selectedHour, selectedMinute, 0);
        timeButton.setText(timeFormat.format(selectedTime));

        /* Set up Recycler View */
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.freeFriendsRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        final FreeFriendRecyclerViewAdapter adapter = new FreeFriendRecyclerViewAdapter(freeFriends);
        recyclerView.setAdapter(adapter);

        // Set up dummy user
        dbRef.child("users").child("dummy").child("isFree").setValue(dummyUserIsFree);

        dbRef.child("users").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<HashMap<String, User>> t = new GenericTypeIndicator<HashMap<String, User>>() {};
                        HashMap<String, User> allUsers = dataSnapshot.getValue(t);
                        freeFriends.clear();

                        int selectedTime = selectedHour * 60 + selectedMinute;
                        for (Map.Entry<String, User> entry : allUsers.entrySet()) {
                            String userId = entry.getKey();
                            User user = entry.getValue();
                            if (user != null && user.getEmail() != null
                                    // skip the current user and dummy user
                                    && !user.getEmail().equals(currentUser.getEmail())
                                    && !user.getEmail().equals("dummy")
                                    && user.getIsFree()) {
                                if ((user.getEndDay() > selectedDay)
                                        || (user.getEndDay() == selectedDay && user.getEndTime() >= selectedTime)) {
                                    freeFriends.put(userId, new User(user));
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("firebase", "loadFreeFriends:onCancelled", databaseError.toException());
                    }
                }
        );

    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, selectedHour, selectedMinute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            selectedHour = hourOfDay;
            selectedMinute = minute;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference dbRef = database.getReference();
            // change the dummy user to invoke onDataChange
            dummyUserIsFree = !dummyUserIsFree;
            dbRef.child("users").child("dummy").child("isFree").setValue(dummyUserIsFree);
            // change text view for time button
            Button timeButton = getActivity().findViewById(R.id.timeButton_main);
            Time selectedTime = new Time(selectedHour, selectedMinute, 0);
            timeButton.setText(timeFormat.format(selectedTime));
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_main) {
            // TODO: implement search function
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
            // SHOULD NOT DO ANYTHING
        } else if (id == R.id.events_nav) {
            Intent intent = new Intent(this, EventsActivity.class);
            startActivity(intent);
        } else if (id == R.id.friends_nav) {
            // TODO: implement friend activity
        } else if (id == R.id.calendar_nav) {
            // TODO: implement calendar activity
        } else if (id == R.id.profile_nav) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
