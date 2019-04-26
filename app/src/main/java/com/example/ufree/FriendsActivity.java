package com.example.ufree;

// TODO. Log out in nav drawer. Time in nav drawer

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class FriendsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView friendRequestsView;
    private RecyclerView friendsExistingView;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authStateListener;

    static User currentUser;

    private String userId;
    private boolean checkedAvailability;

    static final java.text.DateFormat timeFormat = new SimpleDateFormat("hh:mm a");

    public ArrayList<FriendRequestData> friendRequestData;
    public ArrayList<FriendsExistingData> friendsExistingData;

    public ImageView accept;
    public ImageView reject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // set FriendsActivity to be selected
        navigationView.getMenu().getItem(2).setChecked(true);

        // Set up friend request recycler view
        friendRequestsView = (RecyclerView) findViewById(R.id.friendRequests);
        friendRequestsView.setLayoutManager(new LinearLayoutManager(FriendsActivity.this));
        friendRequestData = new ArrayList<FriendRequestData>();

        // Setting up existing friends recycler view
        friendsExistingView = (RecyclerView) findViewById(R.id.friendsExisting);
        friendsExistingView.setLayoutManager(new LinearLayoutManager(FriendsActivity.this));
        friendsExistingData = new ArrayList<FriendsExistingData>();

        // Setting up firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(FriendsActivity.this, LogIn.class));
            finish();
            return;
        }
        // If there is a user, get the user ID from the email
        userId = user.getEmail().replaceAll("[^a-zA-Z0-9]", "");

        // Getting info from firebase to populate recycler view with
        databaseReference = firebaseDatabase.getReference();
        databaseReference.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendRequestData.clear();
                friendsExistingData.clear();

                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    startActivity(new Intent(FriendsActivity.this, LogIn.class));
                    finish();
                    return;
                }

                // If user is valid
                ArrayList<String> incomingFriends = user.getIncomingFriends();
                ArrayList<String> existingFriends = user.getFrienders();

                if (incomingFriends != null) {
                    // If user and incoming friends list are valid
                    for (int i = 0; i < incomingFriends.size(); i++) {
                        friendRequestData.add(new FriendRequestData(incomingFriends.get(i)));
                    }

                    FriendRequestAdaptor myAdaptor = new FriendRequestAdaptor(friendRequestData,
                            FriendsActivity.this);
                    friendRequestsView.setAdapter(myAdaptor);
                }
                if (existingFriends != null) {
                    for (int i = 0; i < existingFriends.size(); i++) {
                        friendsExistingData.add(new FriendsExistingData(existingFriends.get(i)));
                    }

                    FriendsExistingAdaptor myAdaptor = new FriendsExistingAdaptor(friendsExistingData,
                            FriendsActivity.this);
                    friendsExistingView.setAdapter(myAdaptor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FriendsActivity.this,
                        "Something went wrong.", Toast.LENGTH_LONG).show();
            }
        });

        // Record if user has been asked for availability
        checkedAvailability = false;

        databaseReference.child("users").child(userId).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            System.out.println(dataSnapshot.getValue());
                            currentUser = dataSnapshot.getValue(User.class);
                            SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
                            SharedPreferences.Editor spEdit = sp.edit();
                            spEdit.putString("userID", dataSnapshot.getKey());
                            System.out.println(dataSnapshot.getKey());
                            spEdit.apply();

                            Log.d("test", "here" + currentUser.toString());

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

                            /* Display user info in navigation header */
                            NavigationView navigationView = findViewById(R.id.nav_view);
                            View navHeader = navigationView.getHeaderView(0);
                            if (navHeader != null) {
                                TextView nameTextView = navHeader.findViewById(R.id.name_nav);
                                TextView emailTextView = navHeader.findViewById(R.id.email_nav);
                                nameTextView.setText(currentUser.getFullName());
                                emailTextView.setText(currentUser.getEmail());
                                Switch toggle = findViewById(R.id.toggle_nav);
                                Button currentStatusButton = findViewById(R.id.timeButton_nav);
                                toggle.setChecked(currentUser.getIsFree());
                                Time t = new Time(currentUser.getEndHour(), currentUser.getEndMinute(), 0);
                                currentStatusButton.setText(timeFormat.format(t));
                            } else {
                                Log.d("debug", "Nav view is null");
                                Log.d("debug", "Nav view: " + navigationView);
                                Log.d("debug", "Nav header: " + navHeader);
                            }
                        } else {
                            Log.d("debug", "data snapshot is null");
                            startActivity(new Intent(FriendsActivity.this, LogIn.class));
                            finish();
                            return;
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
        toggleNav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                databaseReference.child(userId).child("isFree").setValue(isChecked);
            }
        });
        currentStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = new MainActivity.TimePickerFragmentNav();
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
                startActivity(new Intent(FriendsActivity.this, LogIn.class));
                finish();
                return;
            }
        });
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(FriendsActivity.this, LogIn.class));
                finish();
                return;
            }
        });

        // Add new friends
        FloatingActionButton fab = findViewById(R.id.fab_friends);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FriendsActivity.this, FriendsSearch.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.whosFree_nav) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.events_nav) {
            Intent intent = new Intent(this, EventsActivity.class);
            startActivity(intent);
        } else if (id == R.id.friends_nav) {
            // Should not do anything
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