package com.example.ufree;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FreeFriendRecyclerViewAdapter extends RecyclerView.Adapter<FreeFriendRecyclerViewAdapter.FreeFriendHolder> {

    private final List<User> freeFriends;

    public static class FreeFriendHolder extends RecyclerView.ViewHolder {
        public ImageView profilePic;
        public TextView freeFriendname;
        public ImageView timeIcon;
        public TextView tillTextView;
        public TextView freeTimeTextView;

        public FreeFriendHolder(View v) {
            super(v);
            profilePic = v.findViewById(R.id.profilePic_main);
            freeFriendname = v.findViewById(R.id.freeFriendName_main);
            timeIcon = v.findViewById(R.id.timeIcon_main);
            tillTextView = v.findViewById(R.id.tillTextView_main);
            freeTimeTextView = v.findViewById(R.id.freeTimeTextView_main);
        }
    }


    public FreeFriendRecyclerViewAdapter(List<User> myFreeFriends) {
        freeFriends = myFreeFriends;
    }


    @Override
    public FreeFriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_freefriend, parent, false);
        return new FreeFriendHolder(view);
    }

    @Override
    public void onBindViewHolder(FreeFriendHolder holder, int position) {
        User freeFriend = freeFriends.get(position);
        holder.freeFriendname.setText("Test Name");
        holder.freeTimeTextView.setText("1am");
    }

    @Override
    public int getItemCount() {
        return freeFriends.size();
    }

}
