package com.example.ufree;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FriendRequestViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {

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

        accept.setOnClickListener(this);
        reject.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == accept.getId()) {
            AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
            alert.setTitle("Add Friend");
            alert.setMessage("Are you sure you want to add friend?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO. Do add friend
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
        if (v.getId() == reject.getId()) {
            AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
            alert.setTitle("Delete Friend Request");
            alert.setMessage("Are you sure you want to delete the request?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO. Do remove friend request
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
