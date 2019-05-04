package com.example.ufree;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.Calendar;

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
    private User myUser = new User();
    private String user;
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

        reset();

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

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.d("test","now running view\n\n\n");

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

        if (!myUser.equals("empty")) {
            if (dbref.child("users").child(user).child("events").getRoot() != null) {
                eventRefs.clear();
                inviteRefs.clear();
                dbref.child("users").child(user).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            myUser = dataSnapshot.getValue(User.class);
                            eventRefs = myUser.events;
                            inviteRefs = myUser.invites;
                            callBack();
                            invitesCallback();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }



//        if (!user.equals("empty")) {
//            if (dbref.child("users").child(user).child("events").getRoot() != null) {
//                eventRefs.clear();
//                inviteRefs.clear();
//                dbref.child("users").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        User u = dataSnapshot.getValue(User.class);
//                        eventRefs = u.events;
//                        inviteRefs = u.invites;
//                        callBack();
//                        invitesCallback();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        }

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
                            String photoUrl = currentUser.getProfilePic();
                            ImageView imageView = navHeader.findViewById(R.id.imageView);
                            if (photoUrl != null) {
                                if (imageView != null) {
                                    Glide.with(getApplicationContext())
                                            .load(photoUrl)
                                            .into(imageView);
                                }
                            }

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
        final Switch toggleNav = findViewById(R.id.toggle_nav);
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
        }, 500);


    }

    private void callBack() {
        for (long id : eventRefs.values()) {
            //System.out.println(id);
            //initialize the counter
            if (id != -1 && id != -2) {
                dbref.child("events").child(String.valueOf(id)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Event e = dataSnapshot.getValue(Event.class);

                        boolean add = true;
                        System.out.println("RUNNING!");
                        for (Event event : events) {
                            System.out.println("here are the events " + event.toString());
                            if (event == null || e == null || event.id.equals(e.id)) {
                                add = false;
                            }
                        }

                        if (add) {
                            events.add(e);
                        }

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
        if (inviteRefs != null) {
            for (long id : inviteRefs.values()) {
                if (id != -1 && id != -2) {
                    dbref.child("events").child(String.valueOf(id)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Event e;
                            e = dataSnapshot.getValue(Event.class);
                            boolean add = true;
                            for (Event event : invites) {
                                if (event == null || e == null || event.id.equals(e.id)) {
                                    add = false;
                                }
                            }

                            if (add) {
                                invites.add(e);
                            }

                            invitesRecyclerAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
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
            Intent intent = new Intent(this, FriendsActivity.class);
            startActivity(intent);
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

    private void reset() {
        Date d = new Date();
//        final long time = d.getTime() - d.getTime() / (2019 - 1970);
        final long time = d.getTime();
//        final double msMonth = 26298E5;
//        final double msDay = 86400000;
//        final double msHour = 3600000;
//        final double msMinute = 60000;
//
        final ArrayList<Event> events = new ArrayList<>();
//
        dbref.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Event e = ds.getValue(Event.class);
                    if (e != null) {
                        long cid = e.id;
                        long event_time = e.time;
                        Log.d("test", "event time: " + event_time + "  current time: " + time);
                        if (event_time < time) {
//                            String temp = e.description;
//                            temp = "(Expired)" + temp;
//                            Log.d("test", Long.toString(cid));
//                            dbref.child("events").child(Long.toString(cid)).child("description").setValue(temp);
                            seekAndDestroy(e);
                        }
                    }
                }
            }
//
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//
            }
        });
    }


    private void seekAndDestroy(Event e) {
        if (e.invitees != null) {
            for (String next_user : e.invitees.values()) {
                next_user = next_user.replace("@", "").replace(".", "");
                dbref.child("users").child(next_user).child("invites").child(String.valueOf(e.id)).removeValue();
            }
        }

        if (e.participants != null) {
            for (String next_user : e.participants.values()) {
                next_user = next_user.replace("@", "").replace(".", "");
                dbref.child("users").child(next_user).child("events").child(String.valueOf(e.id)).removeValue();
            }
        }

        dbref.child("events").child(String.valueOf(e.id)).removeValue();
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

                SharedPreferences sp = getActivity().getSharedPreferences("User", MODE_PRIVATE);
                String user = sp.getString("userID", "dummy");
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
