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
                                    // If user and incoming friends list are valid

                                    //     for (HashMap.Entry<String,String> entry : incomingFriends.entrySet()){
                                    //         friendRequestData.add(new FriendRequestData(entry.getValue()));
                                    //     }


                                    //        Log.d("test5", Integer.toString(incomingFriends.size()));
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
                                    // for (HashMap.Entry<String,String> entry : existingFriends.entrySet()){
                                    //     friendsExistingData.add(new FriendsExistingData(entry.getValue()));
                                    // }


                                    FriendsExistingAdaptor myAdaptor = new FriendsExistingAdaptor(friendsExistingData,
                                            FriendsActivity.this);
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











        //TODO: set up navigation drawer

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