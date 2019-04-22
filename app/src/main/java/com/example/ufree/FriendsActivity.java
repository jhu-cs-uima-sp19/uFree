package com.example.ufree;

// TODO. Log out in nav drawer. Time in nav drawer

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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

    private String userId;

    public ArrayList<FriendRequestData> friendRequestData;

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

        this.friendRequestData = new ArrayList<FriendRequestData>();

        // Setting up existing friends recycler view
        friendsExistingView = (RecyclerView) findViewById(R.id.friendsExisting);

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
        databaseReference = firebaseDatabase.getReference("users");
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendRequestData.clear();

                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    startActivity(new Intent(FriendsActivity.this, LogIn.class));
                    finish();
                    return;
                }

                // If user is valid
                ArrayList<String> incomingFriends = user.getIncomingFriends();

                if (incomingFriends == null) {
                    return;
                }

                // If user and incoming friends list are valid
                //for (String key : incomingFriends.keySet()) {
                  //  friendRequestData.add(new FriendRequestData(incomingFriends.get(key)));
                //}
                for (int i = 0; i < incomingFriends.size(); i++) {
                    friendRequestData.add(new FriendRequestData(incomingFriends.get(i)));
                }

                FriendRequestAdaptor myAdaptor = new FriendRequestAdaptor(friendRequestData,
                        FriendsActivity.this);
                friendRequestsView.setAdapter(myAdaptor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FriendsActivity.this,
                        "Something went wrong.", Toast.LENGTH_LONG).show();
            }
        });


        // Add new friends
        FloatingActionButton fab = findViewById(R.id.fab_friends);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO. Call search for all friends to add people
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
