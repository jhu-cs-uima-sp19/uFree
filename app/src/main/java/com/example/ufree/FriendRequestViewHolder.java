package com.example.ufree;

import android.media.Image;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendRequestViewHolder extends RecyclerView.ViewHolder {

    ConstraintLayout constraintLayout;
    ImageView profilePic;
    TextView name;
    TextView email;
    ImageView accept;
    ImageView reject;

    FriendRequestViewHolder(View itemView) {
        super(itemView);
        constraintLayout =(ConstraintLayout) itemView.findViewById(R.id.friendRequestRowLayout);
        profilePic = (ImageView) itemView.findViewById(R.id.profilePic_main);
        name = (TextView) itemView.findViewById(R.id.name);
        email = (TextView) itemView.findViewById(R.id.email);
        accept = (ImageView) itemView.findViewById(R.id.accept);
        reject = (ImageView) itemView.findViewById(R.id.reject);
    }
}
