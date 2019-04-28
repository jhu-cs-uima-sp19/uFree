package com.example.ufree;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class FriendRequestViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {

    ConstraintLayout constraintLayout;
    ImageView profilePic;
    TextView name;
    TextView email;
    TextView Rid;
    ImageView accept;
    ImageView reject;

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String idOfCurrentUser = user.getEmail().replaceAll("[^a-zA-Z0-9]", "");
    final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    final DatabaseReference databaseReference = firebaseDatabase.getReference("users");
    final DatabaseReference mDatabase1 = firebaseDatabase.getReference("users");
    final DatabaseReference mDatabase2 = firebaseDatabase.getReference("users");



    FriendRequestViewHolder(View itemView) {
        super(itemView);
        constraintLayout =(ConstraintLayout) itemView.findViewById(R.id.friendRequestRowLayout);
        profilePic = (ImageView) itemView.findViewById(R.id.profilePic_main);
        name = (TextView) itemView.findViewById(R.id.name);
        email = (TextView) itemView.findViewById(R.id.email);
        Rid = (TextView) itemView.findViewById(R.id.textViewRequestID);
        accept = (ImageView) itemView.findViewById(R.id.accept);
        reject = (ImageView) itemView.findViewById(R.id.reject);



        constraintLayout.setOnClickListener(this);
        accept.setOnClickListener(this);
        reject.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        // If the entire row is clicked
        if (v.getId() == constraintLayout.getId()) {
            String e = email.getText().toString();
            Intent i = new Intent(v.getContext(), FriendProfileActivity.class);
            i.putExtra("email", e);
            i.putExtra("type", 0);
            v.getContext().startActivity(i);
        }

        // Accept button
        if (v.getId() == accept.getId()) {
            AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
            alert.setTitle("Add Friend");
            alert.setMessage("Are you sure you want to add friend?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    databaseReference.child(idOfCurrentUser).child("incomingFriends").orderByValue().startAt((String)email.getText()).endAt((String)email.getText())
                            .addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                                    Rid.setText(dataSnapshot.getKey());
                                    mDatabase1.child(idOfCurrentUser).child("incomingFriends").child((String)Rid.getText()).removeValue();
                                    mDatabase1.child(idOfCurrentUser).child("frienders").push().setValue(email.getText());
                                    mDatabase2.child(((String)email.getText()).replaceAll("[^a-zA-Z0-9]", "")).child("frienders").push().setValue(user.getEmail());
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





                    return;
                }
            });
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.show();
            return;
        }

        // Reject button
        if (v.getId() == reject.getId()) {
            AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
            alert.setTitle("Delete Friend Request");
            alert.setMessage("Are you sure you want to delete the request?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    databaseReference.child(idOfCurrentUser).child("incomingFriends").orderByValue().startAt((String)email.getText()).endAt((String)email.getText())
                            .addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                                    Rid.setText(dataSnapshot.getKey());
                                    mDatabase1.child(idOfCurrentUser).child("incomingFriends").child((String)Rid.getText()).removeValue();
                                //    mDatabase1.child(idOfCurrentUser).child("frienders").push().setValue(email.getText());
                                //    mDatabase2.child(((String)email.getText()).replaceAll("[^a-zA-Z0-9]", "")).child("frienders").push().setValue(user.getEmail());
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

                    return;
                }
            });
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.show();
            return;
        }
    }
}
