package com.example.ufree;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsExistingViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    ConstraintLayout constraintLayout;
    ImageView profilePic;
    TextView name;
    TextView email;

    FriendsExistingViewHolder(View itemView) {
        super(itemView);

        this.constraintLayout =(ConstraintLayout) itemView.findViewById(R.id.friendsExistingRowLayout);
        this.profilePic = (ImageView) itemView.findViewById(R.id.profilePic_main);
        this.name = (TextView) itemView.findViewById(R.id.name);
        this.email = (TextView) itemView.findViewById(R.id.email);

        this.constraintLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        // If the entire row is clicked
        if (v.getId() == this.constraintLayout.getId()) {
            String e = this.email.getText().toString();
            Intent i = new Intent(v.getContext(), FriendProfileActivity.class);
            i.putExtra("email", e);
            i.putExtra("friend", true);
            v.getContext().startActivity(i);
        }
    }
}
