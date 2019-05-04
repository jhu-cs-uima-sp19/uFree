package com.example.ufree;

// TODO. Log out in nav drawer. Time in nav drawer

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static com.example.ufree.MainActivity.timeFormat;

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
                //       ArrayList<String> incomingFriends = user.getIncomingFriends();
                //       ArrayList<String> existingFriends = user.getFrienders();


                //     final HashMap<String, String> incomingFriends = user.getIncomingFriends();
                //     final HashMap<String, String> existingFriends = user.getFrienders();
                final HashMap<String,String> incomingFriendsPre = new HashMap<>();
                final HashMap<String,String> existingFriendsPre = new HashMap<>();


                //      final ArrayList<FriendRequestData> incomingFriends = new ArrayList<FriendRequestData>();
                //      final ArrayList<FriendsExistingData> existingFriends = new ArrayList<FriendsExistingData>();
                databaseReference.child("users").child(userId).child("incomingFriends").orderByValue()
                        .addChildEventListener(new ChildEventListener() {

                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                                //incomingFriends.put(dataSnapshot.getKey(),(String)dataSnapshot.getValue());
                                incomingFriendsPre.clear();

                                String id = ((String) dataSnapshot.getValue()).replaceAll("[^a-zA-Z0-9]", "");
                                Query query = databaseReference.child("users").child(id).orderByChild("fullName");
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        //    Log.d("test5", dataSnapshot.getKey() + "  " + dataSnapshot.child("fullName").getValue());
                                        incomingFriendsPre.put((String)dataSnapshot.child("email").getValue(), (String) dataSnapshot.child("fullName").getValue());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }


                                });
                            }
                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


            /*    databaseReference.child("users").child(userId).child("frienders").orderByValue()
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                                existingFriends.put(dataSnapshot.getKey(),(String)dataSnapshot.getValue());
                                //   Log.d("test",dataSnapshot.getKey().toString());
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });*/

                databaseReference.child("users").child(userId).child("frienders").orderByValue()
                        .addChildEventListener(new ChildEventListener() {


                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                                //incomingFriends.put(dataSnapshot.getKey(),(String)dataSnapshot.getValue());
                                incomingFriendsPre.clear();


                                String id = ((String) dataSnapshot.getValue()).replaceAll("[^a-zA-Z0-9]", "");
                                Query query = databaseReference.child("users").child(id).orderByChild("fullName");
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //Log.d("test5", "   existing friends   " + dataSnapshot.getKey() + "  " + dataSnapshot.child("fullName").getValue());
                                        existingFriendsPre.put((String)dataSnapshot.child("email").getValue(), (String) dataSnapshot.child("fullName").getValue());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }


                                });
                            }
                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                ArrayList<HashMap.Entry<String, String>> entries = new ArrayList<>(incomingFriendsPre.entrySet());
                                Collections.sort(entries, new Comparator<HashMap.Entry<String, String>>(){
                                    public int compare(HashMap.Entry<String, String> a, HashMap.Entry<String, String> b){
                                        return a.getValue().compareTo(b.getValue());
                                    }
                                });
                                ArrayList<String> incomingFriends = new ArrayList<>();
                                for (HashMap.Entry<String, String> entry : entries) {
                                    incomingFriends.add(entry.getKey());
                                }




                                ArrayList<HashMap.Entry<String, String>> entries1 = new ArrayList<>(existingFriendsPre.entrySet());
                                Collections.sort(entries1, new Comparator<HashMap.Entry<String, String>>(){
                                    public int compare(HashMap.Entry<String, String> a, HashMap.Entry<String, String> b){
                                        return a.getValue().compareTo(b.getValue());
                                    }
                                });
                                ArrayList<String> existingFriends = new ArrayList<>();
                                for (HashMap.Entry<String, String> entry : entries1) {
                                    existingFriends.add(entry.getKey());
                                }






                                if (incomingFriends != null) {
                                    friendRequestData.clear();
                                    // If user and incoming friends list are valid

                                    //     for (HashMap.Entry<String,String> entry : incomingFriends.entrySet()){
                                    //         friendRequestData.add(new FriendRequestData(entry.getValue()));
                                    //     }


                                    //        Log.d("test5", Integer.toString(incomingFriends.size()));
                                    for (int i = 0; i < incomingFriends.size(); i++) {
                                        friendRequestData.add(new FriendRequestData(incomingFriends.get(i)));
                                    }

                                    FriendRequestAdaptor myAdaptor = new FriendRequestAdaptor(friendRequestData,
                                            getApplicationContext());
                                    friendRequestsView.setAdapter(myAdaptor);
                                }
                                if (existingFriends != null) {
                                    friendsExistingData.clear();
                                    for (int i = 0; i < existingFriends.size(); i++) {
                                        friendsExistingData.add(new FriendsExistingData(existingFriends.get(i)));
                                    }
                                    // for (HashMap.Entry<String,String> entry : existingFriends.entrySet()){
                                    //     friendsExistingData.add(new FriendsExistingData(entry.getValue()));
                                    // }


                                    FriendsExistingAdaptor myAdaptor = new FriendsExistingAdaptor(friendsExistingData,
                                            getApplicationContext());
                                    friendsExistingView.setAdapter(myAdaptor);
                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });

                    }
                }, 300);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FriendsActivity.this,
                        "Something went wrong.", Toast.LENGTH_LONG).show();
            }
        });

        /* ------------------------------------------------------------------- */
        /* CODES FOR CONFIGURING NAV DRAWER */
        /* DO NOT DELETE!!!! */
        databaseReference.child("users").child(userId).addValueEventListener(
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
                            startActivity(new Intent(FriendsActivity.this, LogIn.class));
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
                databaseReference.child("users").child(userId).child("isFree").setValue(isChecked);
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
                    dbRef.child("users").child(userId).child("endTime").setValue(newEnd.getTimeInMillis());
                    // change button text
                    dateButtonNav.setText(getString(R.string.tomorrow_nav));
                } else {
                    // tomorrow --> today
                    // do not change day
                    Calendar now = Calendar.getInstance();
                    if (newEnd.getTimeInMillis() < now.getTimeInMillis()) {
                        Toast.makeText(v.getContext(), "You cannot set free time before current time", Toast.LENGTH_SHORT).show();
                    }
                    else if (newEnd.getTimeInMillis() < now.getTimeInMillis() + 1800000) {
                        Toast.makeText(v.getContext(), "minimum period is 30 minutes", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // update end time in database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference dbRef = database.getReference();
                        dbRef.child("users").child(userId).child("endTime").setValue(newEnd.getTimeInMillis());
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
                startActivity(new Intent(FriendsActivity.this, LogIn.class));
                finish();
            }
        });
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(FriendsActivity.this, LogIn.class));
                finish();
            }
        });

        /* END OF CODES FOR CONFIGURING NAV DRAWER*/
        /* ------------------------------------------------------------------- */

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
            Toast.makeText(getApplicationContext(), "Coming up...", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.profile_nav) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            }
            else if (currentDay == endDay && now.getTimeInMillis() >= calendar.getTimeInMillis() - 1800000) {
                Toast.makeText(getContext(), "minimum period is 30 minutes", Toast.LENGTH_LONG).show();
                DialogFragment timePickerFragment = new TimePickerFragmentNav();
                timePickerFragment.show(getActivity().getSupportFragmentManager(), "timePickerNav");
            }
            else {
                // update selected calendar object
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = database.getReference();

                SharedPreferences sp = getActivity().getSharedPreferences("User", MODE_PRIVATE);
                String userId = sp.getString("userID", "dummy");
                dbRef.child("users").child(userId).child("endTime").setValue(calendar.getTimeInMillis());

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
        navigationView.getMenu().getItem(2).setChecked(true);
    }
}