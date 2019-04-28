package com.example.ufree;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FriendProfileActivity extends AppCompatActivity {

    String email;
    String name;
    String phone;

    // Set up the view elements
    TextView nameView;
    TextView emailView;
    TextView phoneView;
    TextView deleteeV;
    TextView deleterV;
    TextView requestSentV;
    Button deleteFriend;
    Button addFriend;
    ArrayList<String> key1;
    String deleteeId;
    String deleterId;


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
        deleteeV = (TextView) findViewById(R.id.textViewDeletee);
        deleterV = (TextView) findViewById(R.id.textViewDeleter);
        requestSentV = (TextView) findViewById(R.id.textViewIncoming);



        // Get the extras from intent
        Intent intent = getIntent();
        final String email = intent.getExtras().getString("email");
        final String userId = email.replaceAll("[^a-zA-Z0-9]", "");
        type = intent.getExtras().getInt("type");

        // Firebase stuff
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String idOfCurrentUser = user.getEmail().replaceAll("[^a-zA-Z0-9]", "");
        final DatabaseReference mDatabase1 = firebaseDatabase.getReference("users");
        final DatabaseReference mDatabase2 = firebaseDatabase.getReference("users");


        // Getting info from firebase to populate recycler view with
        final DatabaseReference databaseReference = firebaseDatabase.getReference("users");
        final DatabaseReference dbRef = firebaseDatabase.getReference();


        //see if the current user's id is in the target user's incomingFriends list. If yes, store 1 to requestSentV, otherwise 0.
        requestSentV.setText("0");
        databaseReference.child(userId).child("incomingFriends").orderByValue().startAt(user.getEmail()).endAt(user.getEmail())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        requestSentV.setText("1");
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
        //see if the current user's id is in the target user's friend list. If yes, store 2 to requestSentV, otherwise 0.
        requestSentV.setText("0");
        databaseReference.child(userId).child("frienders").orderByValue().startAt(user.getEmail()).endAt(user.getEmail())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        requestSentV.setText("2");
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



        //get the key for deletee in deleter's frienders list and store it in TextView "deleteeV"
        databaseReference.child(idOfCurrentUser).child("frienders").orderByValue().startAt(email).endAt(email)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        deleteeV.setText(dataSnapshot.getKey());
                   //     Log.d("test2", "Inner" + (String) deleteeV.getText());

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

        //get the key for deleter in deletee's frienders list and store it in TextView deleterV
        databaseReference.child(userId).child("frienders").orderByValue().startAt(user.getEmail()).endAt(user.getEmail())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        deleterV.setText(dataSnapshot.getKey());
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

                                    deleteeId = (String)deleteeV.getText();
                                    deleterId = (String)deleterV.getText();

                                    final DatabaseReference mDatabase1 = firebaseDatabase.getReference("users");
                                    final DatabaseReference mDatabase2 = firebaseDatabase.getReference("users");
                              //      Log.d("test2",deleteeId);
                              //      Log.d("test2",deleterId);

                                    mDatabase1.child(idOfCurrentUser).child("frienders").child(deleteeId).removeValue();
                                    mDatabase2.child(userId).child("frienders").child(deleterId).removeValue();
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
                            if(requestSentV.getText() == "1"){
                                Toast.makeText(getBaseContext(), "Friend Request Sent", Toast.LENGTH_LONG).show();
                            }
                            else if(requestSentV.getText() == "2"){
                                Toast.makeText(getBaseContext(), "You Are Already Friends!", Toast.LENGTH_LONG).show();
                            }
                            else{
                                mDatabase2.child(userId).child("incomingFriends").push().setValue(user.getEmail());
                                Toast.makeText(getBaseContext(), "Friend Request Sent", Toast.LENGTH_LONG).show();
                            }
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

    public void getKey(String cur, String target){
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("users");
        final ArrayList ids = new ArrayList();
        databaseReference.child(cur).child("frienders").orderByValue().startAt(target).endAt(target)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        key1.add(dataSnapshot.getKey());
                      //  Log.d("test3",dataSnapshot.getKey().toString());

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
    }
}
