package com.example.ufree;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FriendsSearchAdaptor extends RecyclerView.Adapter {

    ArrayList<FriendsSearchData> list;
    Context context;
    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("users");
    HashMap<String, Runnable> pendingRunnables = new HashMap<>();

    public FriendsSearchAdaptor(ArrayList<FriendsSearchData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public FriendsSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout_friends_search,
                parent, false);
        FriendsSearchViewHolder holder = new FriendsSearchViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        final FriendsSearchViewHolder myHolder = (FriendsSearchViewHolder) holder;
        String id = list.get(position).email.replaceAll("[^a-zA-Z0-9]", "");
        dbref.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    return;
                }
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    myHolder.name.setText(user.getFullName());
                    myHolder.email.setText(user.getEmail());
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
    public void insert(int position, FriendsSearchData data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(FriendsSearchData data) {
        int position = list.indexOf(data);
        list.remove(position);
    }

    public void remove() {
    }
}