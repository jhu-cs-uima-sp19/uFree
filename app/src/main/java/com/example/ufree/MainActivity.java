package com.example.ufree;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnLongClickListener {

    static User currentUser;
    static String userId;
    static boolean checkedAvailability;
    HashMap<String, User> freeFriends = new LinkedHashMap<String, User>();
    static Calendar selectedCalendar;
    static boolean dummyUserIsFree = true;
    boolean isInActionMode = false;
    TextView counterTextView;
    FreeFriendRecyclerViewAdapter adapter;
    FloatingActionButton fab;
    ArrayList<String> selectedFreinds = new ArrayList<String>();
    int counter = 0;
    ActionBarDrawerToggle toggle;

    static final java.text.DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    static final java.text.DateFormat dateFormat = new SimpleDateFormat("MMM dd, EEE");

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get user id from Shared Preferences
        SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
        userId = sp.getString("userID", "dummy");
        // if there is no user id in Shared Preferences, go back to log in
        if (userId.equals("dummy")) {
            Log.d("debug", "userId in Shared Preferences is null");
            startActivity(new Intent(MainActivity.this, LogIn.class));
            finish();
            return;
        }

        /* Set up App bar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // set up title of app bar
        getSupportActionBar().setTitle("Who's Free");
        counterTextView = findViewById(R.id.counterTextView_main);
        counterTextView.setVisibility(View.GONE);

        // TODO: Set up correct listener for fab
        /* Set up floating add button */
        fab = (FloatingActionButton) findViewById(R.id.fab_main);
        fab.setVisibility(View.GONE);

        /* Set up navigation drawer */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // set Who's Free to be selected
        navigationView.getMenu().getItem(0).setChecked(true);


        /* Check if need to ask user availability */
        // initialize firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbRef = database.getReference();

        // Record if user has been asked for availability
        checkedAvailability = false;

        dbRef.child("users").child(userId).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            currentUser = dataSnapshot.getValue(User.class);

                            // if user has been asked for availability, do NOT ask again
                            if (!checkedAvailability) {
                                long now = Calendar.getInstance().getTimeInMillis();

                                // if user is free and end time does not exceed current time, do NOT ask for availability
                                // else show welcome screen
                                if (!(currentUser.getIsFree()
                                        && currentUser.getEndTime() >= now)) {
                                    Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                                    startActivity(intent);
                                }
                                checkedAvailability = true;
                            }

                            /* Display user info in navigation header */
                            NavigationView navigationView = findViewById(R.id.nav_view);
                            View navHeader = navigationView.getHeaderView(0);

                            TextView nameTextView = navHeader.findViewById(R.id.name_nav);
                            TextView emailTextView = navHeader.findViewById(R.id.email_nav);
                            String photoUrl = currentUser.getProfilePic();
                            ImageView imageView = navHeader.findViewById(R.id.imageView);
                            if (photoUrl != null) {
                                if (imageView != null) {
                                    Glide.with(getApplicationContext())
                                            .load(photoUrl)
                                            .into(imageView);
                                }
                            }
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
                            startActivity(new Intent(MainActivity.this, LogIn.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("firebase", "loadUserFreeTime:onCancelled", databaseError.toException());
                    }
                }
        );


        /* Set time machine to be current time */
        selectedCalendar = Calendar.getInstance();

        // Set up time button
        Button timeButton = findViewById(R.id.timeButton_main);
        timeButton.setText(timeFormat.format(selectedCalendar.getTime()));
        // Set up date button
        Button dateButton = findViewById(R.id.dateButton_main);
        dateButton.setText(dateFormat.format(selectedCalendar.getTime()));


        /* Set up Recycler View */
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.freeFriendsRecyclerView);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FreeFriendRecyclerViewAdapter(freeFriends, this);
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
                        adapter.notifyDataSetChanged();

                        if (currentUser != null) {
                            // Get current user's friends
                            HashMap<String, String> friends = currentUser.getFrienders();
                            if (friends != null) {
                                for (String friendEmail : friends.values()) {
                                    User user = allUsers.get(friendEmail.replaceAll("[^a-zA-Z0-9]", ""));
                                    if (user != null && user.getEmail() != null
                                            // skip the current user and dummy user
                                            && !user.getEmail().equals(currentUser.getEmail())
                                            && !user.getEmail().equals("dummy")
                                            && user.getIsFree()) {
                                        if (selectedCalendar.getTimeInMillis() < user.getEndTime()) {
                                            freeFriends.put(friendEmail.replaceAll("[^a-zA-Z0-9]", ""), new User(user));
                                        }
                                    }
                                }
                            }
                            HashMap<String, User> sortedFreeFriends = sortByTime(freeFriends);
                            for (Map.Entry<String, User> entry: sortedFreeFriends.entrySet()) {
                                Log.d("debug", "here " + entry.getKey());
                            }
                            freeFriends.clear();
                            for (Map.Entry<String, User> entry: sortedFreeFriends.entrySet()) {
                                freeFriends.put(entry.getKey(), entry.getValue());
                            }
                            for (Map.Entry<String, User> entry: freeFriends.entrySet()) {
                                Log.d("debug", "where " + entry.getKey());
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            startActivity(new Intent(MainActivity.this, LogIn.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("firebase", "loadFreeFriends:onCancelled", databaseError.toException());
                    }
                }
        );

        // Set up listener for toggle and time button in nav drawer
        Switch toggleNav = findViewById(R.id.toggle_nav);
        Button currentStatusButton = findViewById(R.id.timeButton_nav);
        toggleNav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dbRef.child("users").child(userId).child("isFree").setValue(isChecked);
            }
        });
        currentStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = new TimePickerFragmentNav();
                timePickerFragment.show(getSupportFragmentManager(), "timePickerNav");
            }
        });

        // Set up listener for log out
        ImageView exitImageView = findViewById(R.id.exitImageView_nav);
        TextView logoutTextView = findViewById(R.id.logout_nav);
        exitImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LogIn.class));
                finish();
            }
        });
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                System.out.println("thiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiis");
                startActivity(new Intent(MainActivity.this, LogIn.class));
                finish();
            }
        });

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
                DialogFragment datePickerFragment = new TimePickerFragmentBottom();
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "timePickerBottom");
            } else {
                // change the dummy user to invoke onDataChange
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = database.getReference();
                dummyUserIsFree = !dummyUserIsFree;
                dbRef.child("users").child("dummy").child("isFree").setValue(dummyUserIsFree);
                // change text view for time button
                Button timeButton = getActivity().findViewById(R.id.timeButton_main);
                timeButton.setText(timeFormat.format(selectedCalendar.getTime()));
            }
        }
    }

    public void showTimePickerDialogBottom(View v) {
        DialogFragment timePickerFragment = new TimePickerFragmentBottom();
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
                    DialogFragment datePickerFragment = new DatePickerFragment();
                    datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                } else {
                    selectedCalendar.set(
                            year, month, day,
                            selectedCalendar.get(Calendar.HOUR_OF_DAY),
                            selectedCalendar.get(Calendar.HOUR_OF_DAY), 0);
                    Button dateButton = getActivity().findViewById(R.id.dateButton_main);
                    dateButton.setText(dateFormat.format(selectedCalendar.getTime()));
                    // change the dummy user to invoke onDataChange
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference dbRef = database.getReference();
                    dummyUserIsFree = !dummyUserIsFree;
                    dbRef.child("users").child("dummy").child("isFree").setValue(dummyUserIsFree);
                }
            } else {
                Toast.makeText(getContext(), "You can only select today or tomorrow", Toast.LENGTH_SHORT).show();
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

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

                dbRef.child("users").child(userId).child("endTime").setValue(calendar.getTimeInMillis());

                // change text view for time button
                Button timeButton = getActivity().findViewById(R.id.timeButton_nav);
                timeButton.setText(timeFormat.format(calendar.getTime()));
            }
        }
    }

    // Date button in ** NAV DRAWER **
    public void changeDate(View v) {
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
            dbRef.child("users").child(userId).child("endTime").setValue(newEnd.getTimeInMillis());
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
                dbRef.child("users").child(userId).child("endTime").setValue(newEnd.getTimeInMillis());
                // change button text
                dateButtonNav.setText(getString(R.string.today_nav));
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (isInActionMode) {
            clearActionMode();
            adapter.notifyDataSetChanged();
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
        Log.d("debug", "item id: " + id);

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home && isInActionMode) {
            clearActionMode();
            Log.d("debug", "back button is pressed");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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
            Intent intent = new Intent(this, FriendsActivity.class);
            startActivity(intent);
        } else if (id == R.id.calendar_nav) {
            // TODO: implement calendar activity
            Toast.makeText(getApplicationContext(), "Coming up...", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.profile_nav) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onLongClick(View v) {
        // clear the option menu
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        isInActionMode = true;
        getSupportActionBar().setTitle("");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        counterTextView.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();
                for (String id: selectedFreinds) {
                    sb.append(id);
                    sb.append(" ");
                }

                String result = sb.toString();
                Intent intent = new Intent(v.getContext(), NewEventActivity.class);
                intent.putExtra("ids", result);
                startActivity(intent);
                clearActionMode();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();
        return true;
    }

    public void prepareSelection(View view, String selectedId) {
        if (((CheckBox)view).isChecked()) {
            selectedFreinds.add(selectedId);
            counter++;
            updateCounter(counter);
        } else {
            selectedFreinds.remove(selectedId);
            counter--;
            updateCounter(counter);
        }
    }

    public void updateCounter(int counter) {
        if (counter == 0) {
            counterTextView.setText("0 friend selected");
        } else {
            counterTextView.setText(getString(R.string.multiSelectedTextView_toolbar, counter));
        }
    }


    public void clearActionMode() {
        isInActionMode = false;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.main);
        counterTextView.setText("0 friend selected");
        counterTextView.setVisibility(View.GONE);
        getSupportActionBar().setTitle("Who's Free");
        counter = 0;
        selectedFreinds.clear();
        fab.setVisibility(View.GONE);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        /* Set up navigation drawer */
        toggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        // set Who's Free to be selected
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    // function to sort hashmap by values
    public static HashMap<String, User> sortByTime(HashMap<String, User> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, User> > list =
                new LinkedList<>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, User> >() {
            public int compare(Map.Entry<String, User> o1,
                               Map.Entry<String, User> o2)
            {
                return (Long.valueOf(o1.getValue().getEndTime())).compareTo(Long.valueOf(o2.getValue().getEndTime()));
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, User> temp = new LinkedHashMap<String, User>();
        for (Map.Entry<String, User> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }


}
