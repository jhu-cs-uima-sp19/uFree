package com.example.ufree;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendProfileActivity extends AppCompatActivity {

    String email;
    String name;
    String phone;

    // Set up the view elements
    TextView nameView;
    TextView emailView;
    TextView phoneView;
    Button deleteFriend;
    Button addFriend;

    // Type of user accessing this page
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nameView = (TextView) findViewById(R.id.name);
        emailView = (TextView) findViewById(R.id.email);
        phoneView = (TextView) findViewById(R.id.phone);
        deleteFriend = (Button) findViewById(R.id.deleteFriend);
        addFriend = (Button) findViewById(R.id.addFriend);

        // Get the extras from intent
        Intent intent = getIntent();
        String email = intent.getExtras().getString("email");
        String userId = email.replaceAll("[^a-zA-Z0-9]", "");
        type = intent.getExtras().getInt("type");

        // Firebase stuff
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Getting info from firebase to populate recycler view with
        DatabaseReference databaseReference = firebaseDatabase.getReference("users");
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    finish();
                    return;
                }
                name = user.getFullName();
                phone = user.getPhone();
                nameView.setText(name);
                phoneView.setText(phone);
                // set up title of action bar to be name of friend
                getSupportActionBar().setTitle(user.getFullName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        System.out.println("Name: " + name + " Phone: " + phone);
        emailView.setText(email);

        if (type == 1) {
            deleteFriend.setVisibility(View.VISIBLE);
            deleteFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(FriendProfileActivity.this);
                    alert.setTitle("Delete Friend");
                    alert.setMessage("Are you sure you want to remove friend?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO remove friend from deleter and deletee
                                }
                            });
                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
            });
        } else if (type == 2) {
            addFriend.setVisibility(View.VISIBLE);
            addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(FriendProfileActivity.this);
                    alert.setTitle("Add Friend");
                    alert.setMessage("Are you sure you want to send a friend request?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO add friend request
                        }
                    });
                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
            });
        }
    }
}
