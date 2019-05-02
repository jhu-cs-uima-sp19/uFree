package com.example.ufree;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import org.w3c.dom.Text;

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
    private Context context;
    MainActivity mainActivity;

    public static class FreeFriendHolder extends RecyclerView.ViewHolder{
        public ImageView profilePic;
        public TextView freeFriendname;
        public ImageView timeIcon;
        public TextView tillTextView;
        public TextView freeTimeTextView;
        public TextView freeDateTextView;
        public TextView userIDTextView;
        public CheckBox checkBox;
        MainActivity mainActivity;
        ConstraintLayout constraintLayout;

        public FreeFriendHolder(View v, MainActivity mainActivity) {
            super(v);
            profilePic = v.findViewById(R.id.profilePic_main);
            freeFriendname = v.findViewById(R.id.freeFriendName_main);
            timeIcon = v.findViewById(R.id.timeIcon_main);
            tillTextView = v.findViewById(R.id.tillTextView_main);
            freeTimeTextView = v.findViewById(R.id.freeTimeTextView_main);
            freeDateTextView = v.findViewById(R.id.freeDateTextView_main);
            userIDTextView = v.findViewById(R.id.freeFriendIDTextView);
            checkBox = v.findViewById(R.id.checkBox_main);
            this.mainActivity = mainActivity;
            constraintLayout = v.findViewById(R.id.freeFriendItemLayout_main);
            constraintLayout.setOnLongClickListener(mainActivity);
        }
    }


    public FreeFriendRecyclerViewAdapter(HashMap<String, User> myFreeFriends, Context context) {
        this.freeFriends = myFreeFriends;
        this.userIds = myFreeFriends.keySet().toArray(new String[myFreeFriends.size()]);
        this.context = context;
        this.mainActivity = (MainActivity) context;
    }


    @Override
    public FreeFriendHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_freefriend, parent, false);
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SingleFriendEventsActivity.class);
                Bundle extras = new Bundle();
                TextView ids = view.findViewById(R.id.freeFriendIDTextView);
                extras.putString("friendEmail", String.valueOf(ids.getText()));
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });
        return new FreeFriendHolder(view, mainActivity);
    }

    @Override
    public void onBindViewHolder(FreeFriendHolder holder, final int position) {
        // TODO: reduce memory usage here
        userIds = freeFriends.keySet().toArray(new String[freeFriends.size()]);
        User freeFriend = freeFriends.get(userIds[position]);
        holder.freeFriendname.setText(freeFriend.getFullName());
        String photoUrl = freeFriend.getProfilePic();
        if (photoUrl != null) {
            Glide.with(this.context)
                    .load(photoUrl)
                    .into(holder.profilePic);
        }
        holder.userIDTextView.setText(freeFriend.getEmail());
        Calendar now = Calendar.getInstance();
        Calendar endCalendar = java.util.Calendar.getInstance();
        endCalendar.setTimeInMillis(freeFriend.getEndTime());
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        holder.freeTimeTextView.setText(timeFormat.format(endCalendar.getTime()));
        if (now.get(Calendar.DAY_OF_YEAR) == endCalendar.get(Calendar.DAY_OF_YEAR)) {
            holder.freeDateTextView.setText("today");
        } else {
            holder.freeDateTextView.setText("tomorrow");
        }
        // set visibility of check box
        if (mainActivity.isInActionMode) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(false);
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.prepareSelection(v, userIds[position]);
                }
            });
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return freeFriends.size();
    }

}
