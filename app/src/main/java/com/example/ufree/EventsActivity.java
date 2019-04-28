package com.example.ufree;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.EventLog;
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
import android.view.ViewParent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import static com.example.ufree.MainActivity.timeFormat;

public class EventsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseDatabase db;
    private DatabaseReference dbref;
    private RecyclerView eventsRecyclerView;
    private RecyclerView invitesRecyclerView;
    private ArrayList<Event> events = new ArrayList<>();
    private ArrayList<Event> invites = new ArrayList<>();
    private HashMap<String, Long> eventRefs = new HashMap<>();
    private HashMap<String, Long> inviteRefs = new HashMap<>();
    private static String user;
    CustomAdapter recyclerAdapter = new CustomAdapter(events);
    CustomAdapter invitesRecyclerAdapter = new CustomAdapter(invites);
    private static User currentUser;


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



        /* Set up navigation drawer */
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

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        eventsRecyclerView.setLayoutManager(mLayoutManager);
        eventsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        eventsRecyclerView.setAdapter(recyclerAdapter);

        invitesRecyclerView = findViewById(R.id.InvitesRecyclerView);
        RecyclerView.LayoutManager iLayoutManager = new LinearLayoutManager(getApplicationContext());
        invitesRecyclerView.setLayoutManager(iLayoutManager);
        invitesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        invitesRecyclerView.setAdapter(invitesRecyclerAdapter);

        SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
        user = sp.getString("userID", "empty");

        if (user != "empty") {
            if (dbref.child("users").child(user).child("events").getRoot() != null) {
                dbref.child("users").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);
                        eventRefs = u.events;
                        inviteRefs = u.invites;
                        callBack();
                        invitesCallback();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        /* ------------------------------------------------------------------- */
        /* CODES FOR CONFIGURING NAV DRAWER */
        /* DO NOT DELETE!!!! */
        dbref.child("users").child(user).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            currentUser = dataSnapshot.getValue(User.class);

                            /* Display user info in navigation header */
                            NavigationView navigationView = findViewById(R.id.nav_view);
                            View navHeader = navigationView.getHeaderView(0);

                            TextView nameTextView = navHeader.findViewById(R.id.name_nav);
                            TextView emailTextView = navHeader.findViewById(R.id.email_nav);
                            nameTextView.setText(currentUser.getFullName());
                            emailTextView.setText(currentUser.getEmail());

                            Switch toggle = findViewById(R.id.toggle_nav);
                            toggle.setChecked(currentUser.getIsFree());

                            Button currentStatusButton = findViewById(R.id.timeButton_nav);
                            Calendar endCalendar = Calendar.getInstance();
                            endCalendar.setTimeInMillis(currentUser.getEndTime());
                            currentStatusButton.setText(timeFormat.format(endCalendar.getTime()));

                            Button dateButtonNav = findViewById(R.id.dateButton_nav);
                            Calendar today = Calendar.getInstance();
                            if (today.get(Calendar.DAY_OF_YEAR) == endCalendar.get(Calendar.DAY_OF_YEAR)) {
                                dateButtonNav.setText(getString(R.string.today_nav));
                            } else {
                                dateButtonNav.setText(getString(R.string.tomorrow_nav));
                            }
                        } else {
                            Log.d("debug", "data snapshot is null");
                            startActivity(new Intent(EventsActivity.this, LogIn.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("firebase", "loadUserFreeTime:onCancelled", databaseError.toException());
                    }
                }
        );

        // Set up listener for toggle and time button in nav drawer
        Switch toggleNav = findViewById(R.id.toggle_nav);
        Button currentStatusButton = findViewById(R.id.timeButton_nav);
        Button dateButtonNav = findViewById(R.id.dateButton_nav);
        toggleNav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dbref.child("users").child(user).child("isFree").setValue(isChecked);
            }
        });
        currentStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = new TimePickerFragmentNav();
                timePickerFragment.show(getSupportFragmentManager(), "timePickerNav");
            }
        });
        dateButtonNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button dateButtonNav = v.findViewById(R.id.dateButton_nav);

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTimeInMillis(currentUser.getEndTime());

                Calendar newEnd = Calendar.getInstance();
                newEnd.set(
                        newEnd.get(Calendar.YEAR),
                        newEnd.get(Calendar.MONTH),
                        newEnd.get(Calendar.DAY_OF_MONTH),
                        endCalendar.get(Calendar.HOUR_OF_DAY),
                        endCalendar.get(Calendar.MINUTE)
                );

                // today --> tomorrow
                if (dateButtonNav.getText().toString().equals(getString(R.string.today_nav))) {
                    // add one day
                    newEnd.add(Calendar.DATE, 1);
                    // update end time in database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference dbRef = database.getReference();
                    dbRef.child("users").child(user).child("endTime").setValue(newEnd.getTimeInMillis());
                    // change button text
                    dateButtonNav.setText(getString(R.string.tomorrow_nav));
                } else {
                    // tomorrow --> today
                    // do not change day
                    Calendar now = Calendar.getInstance();
                    if (newEnd.getTimeInMillis() < now.getTimeInMillis()) {
                        Toast.makeText(v.getContext(), "You cannot set free time before current time", Toast.LENGTH_SHORT).show();
                    } else {
                        // update end time in database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference dbRef = database.getReference();
                        dbRef.child("users").child(user).child("endTime").setValue(newEnd.getTimeInMillis());
                        // change button text
                        dateButtonNav.setText(getString(R.string.today_nav));
                    }
                }
            }
        });

        // Set up listener for log out
        ImageView exitImageView = findViewById(R.id.exitImageView_nav);
        TextView logoutTextView = findViewById(R.id.logout_nav);
        exitImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(EventsActivity.this, LogIn.class));
                finish();
            }
        });
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(EventsActivity.this, LogIn.class));
                finish();
            }
        });

        /* END OF CODES FOR CONFIGURING NAV DRAWER*/
        /* ------------------------------------------------------------------- */

    }

    //This is horrendous style but it's what we're doing.
    //let's stack asynchronous functions on top of asynchronous functions by placing
    //database queries in the callback for our database query
    private void callBack() {
        for (long id : eventRefs.values()) {
            //System.out.println(id);
            //initialize the counter
            if (id != -1 && id != -2) {
                dbref.child("events").child(String.valueOf(id)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Event e = dataSnapshot.getValue(Event.class);
                        events.add(e);
                        recyclerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }
    }

    private void invitesCallback() {
        for (long id : inviteRefs.values()) {
            if (id != -1 && id != -2) {
                dbref.child("events").child(String.valueOf(id)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Event e = dataSnapshot.getValue(Event.class);
                        invites.add(e);
                        invitesRecyclerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }
        }
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

    /** function to handle click events for recyclerview items. */
    public void viewEventAction(View view) {
        TextView selectedItemDescription = (TextView) view.findViewById(R.id.description);
        TextView selectedItemLocation = (TextView) view.findViewById(R.id.location);
        TextView selectedItemId = (TextView) view.findViewById(R.id.id);

        if (selectedItemDescription != null && selectedItemLocation != null) {
            Long id = Long.valueOf(String.valueOf(selectedItemId.getText()));
            Intent intent;


            ViewParent parent = view.getParent();
            if (((View) parent).getId() == R.id.EventsRecyclerView) {
                intent = new Intent(this, NewEventActivity.class);
            } else {
                intent = new Intent(this, ViewEventActivity.class);
            }

            Bundle extras = new Bundle();
            extras.putLong("id", id);
            intent.putExtras(extras);
            startActivity(intent);
        }
    }

    /* CODES FOR SETTING UP TIME BUTTON IN NAV DRAWER */
    /* DO NOT DELETE!!! */
    // Time picker for time button in the ** NAV DRAWER **
    public static class TimePickerFragmentNav extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of TimePickerDialog and return it
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentUser.getEndTime());
            int endHour = calendar.get(Calendar.HOUR_OF_DAY);
            int endMinute = calendar.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, endHour, endMinute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar now = Calendar.getInstance();
            int currentDay = now.get(Calendar.DAY_OF_YEAR);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentUser.getEndTime());
            calendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    hourOfDay, minute);
            int endDay = calendar.get(Calendar.DAY_OF_YEAR);

            // if user set free time less than current time
            if (currentDay == endDay && now.getTimeInMillis() >= calendar.getTimeInMillis()) {
                Toast.makeText(getContext(), "You cannot set free time before current time", Toast.LENGTH_LONG).show();
                DialogFragment timePickerFragment = new TimePickerFragmentNav();
                timePickerFragment.show(getActivity().getSupportFragmentManager(), "timePickerNav");
            } else {
                // update selected calendar object
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = database.getReference();

                dbRef.child("users").child(user).child("endTime").setValue(calendar.getTimeInMillis());

                // change text view for time button
                Button timeButton = getActivity().findViewById(R.id.timeButton_nav);
                timeButton.setText(timeFormat.format(calendar.getTime()));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        // set Who's Free to be selected
        navigationView.getMenu().getItem(1).setChecked(true);
    }

}
