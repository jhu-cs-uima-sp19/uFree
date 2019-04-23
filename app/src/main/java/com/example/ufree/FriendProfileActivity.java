package com.example.ufree;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendProfileActivity extends AppCompatActivity {

    String email;
    String name;
    String phone;

    // Set up the view elements
    TextView nameView;// = (TextView) findViewById(R.id.name);
    TextView emailView;// = (TextView) findViewById(R.id.email);
    TextView phoneView;// = (TextView) findViewById(R.id.phone);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        nameView = (TextView) findViewById(R.id.name);
        emailView = (TextView) findViewById(R.id.email);
        phoneView = (TextView) findViewById(R.id.phone);

        // Get the extras from intent
        Intent intent = getIntent();
        String email = intent.getExtras().getString("email");
        String userId = email.replaceAll("[^a-zA-Z0-9]", "");

        // Firebase stuff
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Getting info from firebase to populate recycler view with
        DatabaseReference databaseReference = firebaseDatabase.getReference("users");
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name = user.getFullName();
                phone = user.getPhone();
                nameView.setText(name);
                phoneView.setText(phone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        System.out.println("Name: " + name + " Phone: " + phone);
        emailView.setText(email);
    }
}
