package com.example.ufree;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FriendsExistingAdaptor extends RecyclerView.Adapter {

    ArrayList<FriendsExistingData> list;
    Context context;
    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("users");
    HashMap<String, Runnable> pendingRunnables = new HashMap<>();
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String idOfCurrentUser = user.getEmail().replaceAll("[^a-zA-Z0-9]", "");
    final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    final DatabaseReference databaseReference = firebaseDatabase.getReference("users");
    final DatabaseReference mDatabase1 = firebaseDatabase.getReference("users");
    final DatabaseReference mDatabase2 = firebaseDatabase.getReference("users");

    public FriendsExistingAdaptor(ArrayList<FriendsExistingData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public FriendsExistingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout_friends_existing,
                parent, false);
        FriendsExistingViewHolder holder = new FriendsExistingViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        final FriendsExistingViewHolder myHolder = (FriendsExistingViewHolder) holder;
        myHolder.email.setText(list.get(position).email);
        String id = list.get(position).email.replaceAll("[^a-zA-Z0-9]", "");
        dbref.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    myHolder.name.setText(user.getFullName());
                    String url = user.getProfilePic();
                    if (url != null) {
                        Glide.with(context)
                                .load(url)
                                .into(myHolder.profilePic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("0", "Database error");
            }
        });
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, FriendsExistingData data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(FriendsExistingData data) {
        int position = list.indexOf(data);
        this.remove(position);
    }

    public void remove(final int pos) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Delete Friend");
        alert.setMessage("Are you sure you want to delete friend?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.child(idOfCurrentUser).child("frienders").orderByValue().startAt(list.get(pos).email).endAt(list.get(pos).email)
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                                String temp = dataSnapshot.getKey();
                                mDatabase1.child(idOfCurrentUser).child("frienders").child(temp).removeValue();
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

                databaseReference.child(list.get(pos).email).child("frienders").orderByValue().startAt(idOfCurrentUser).endAt(idOfCurrentUser)
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                                String temp = dataSnapshot.getKey();
                                mDatabase1.child(list.get(pos).email).child("frienders").child(temp).removeValue();
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





                list.remove(pos);
                notifyItemRemoved(pos);
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
    }
}