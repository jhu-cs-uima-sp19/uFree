package com.example.ufree;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class FreeFriendRecyclerViewAdapter extends RecyclerView.Adapter<FreeFriendRecyclerViewAdapter.FreeFriendHolder> {

    private final HashMap<String, User> freeFriends;
    private String[] userIds;

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


    public FreeFriendRecyclerViewAdapter(HashMap<String, User> myFreeFriends) {
        freeFriends = myFreeFriends;
        userIds = myFreeFriends.keySet().toArray(new String[myFreeFriends.size()]);
    }


    @Override
    public FreeFriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_freefriend, parent, false);
        return new FreeFriendHolder(view);
    }

    @Override
    public void onBindViewHolder(FreeFriendHolder holder, int position) {
        // TODO: reduce memory usage here
        userIds = freeFriends.keySet().toArray(new String[freeFriends.size()]);
        User freeFriend = freeFriends.get(userIds[position]);
        holder.freeFriendname.setText(freeFriend.getFirstName() + " " + freeFriend.getLastName());
        Calendar calendar = java.util.Calendar.getInstance();
        Time time = new Time(freeFriend.getEndHour(), freeFriend.getEndMinute(), 0);
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        holder.freeTimeTextView.setText(timeFormat.format(time));
    }

    @Override
    public int getItemCount() {
        return freeFriends.size();
    }

}
