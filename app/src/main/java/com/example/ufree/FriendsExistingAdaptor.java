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
        return list.size();
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
                // TODO. Do delete friend
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