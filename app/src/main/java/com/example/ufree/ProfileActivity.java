package com.example.ufree;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseUser user;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // set up title of app bar
        getSupportActionBar().setTitle("Profile");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // set Profile to be selected
        navigationView.getMenu().getItem(4).setChecked(true);



        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference mDatabase = database.getInstance().getReference();
        final DatabaseReference mDatabase2 = database.getReference("users");
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //final DatabaseReference nameDB = mDatabase.child().xxx;
        //final DatabaseReference phoneDB = mDatabase.child().xxx;





        // disable edit text
        final EditText phoneEditView = (EditText) findViewById(R.id.phoneEditText_profile);
        phoneEditView.setEnabled(false);

        final EditText nameEditView = (EditText) findViewById(R.id.nameEditText_profile);
        nameEditView.setEnabled(false);

        final TextView emailTV = findViewById(R.id.email_profile);

        Button deleteAccount = findViewById(R.id.deleteAccountButton_profile);

        final ImageView userNameButton = findViewById(R.id.editNameButton_profile);
        final ImageView phoneButton = findViewById(R.id.editPhoneButton_profile);
        ImageView passButton = findViewById(R.id.changePasswordButton_profile);


        // TODO: DIRECTLY GET USER ID FROM DATABASE
        user = FirebaseAuth.getInstance().getCurrentUser();
        String temp = user.getEmail().replaceAll("@", "");
        final String userId = temp.replaceAll("\\.", "");

        FirebaseDatabase.getInstance().getReference().child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUser = dataSnapshot.getValue(User.class);
                    String displayName = currentUser.getFullName();
                    nameEditView.setText(displayName);
                    phoneEditView.setText(dataSnapshot.getValue(User.class).getPhone());
                    emailTV.setText(dataSnapshot.getValue(User.class).getEmail());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        userNameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!nameEditView.isEnabled()){
                    nameEditView.setEnabled(true);
                    userNameButton.setImageResource(R.drawable.ic_check_orange_24dp);
                }
                else if(nameEditView.isEnabled()){
                    String value = nameEditView.getText().toString().trim();
                    String[] splited = value.split("\\s+");
                    if(splited.length < 2){
                        Toast.makeText(getBaseContext(), "Please Include Both First And Last Name", Toast.LENGTH_LONG).show();
                    }
                    else if(splited.length > 2){
                        Toast.makeText(getBaseContext(), "Please Include Only First And Last Name", Toast.LENGTH_LONG).show();
                    }
                    else {
                        mDatabase.child("users").child(userId).child("fullName").setValue(value);
                        nameEditView.setEnabled(false);
                        userNameButton.setImageResource(R.drawable.ic_edit_orange_24dp);
                    }
                }
            }
        });

        passButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = emailTV.getText().toString();
                mAuth.sendPasswordResetEmail(email);
                Toast.makeText(getBaseContext(), "Reset Email Sent", Toast.LENGTH_LONG).show();
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!phoneEditView.isEnabled()){
                    phoneEditView.setEnabled(true);
                    phoneButton.setImageResource(R.drawable.ic_check_orange_24dp);
                }
                else if(phoneEditView.isEnabled()){
                    String value = phoneEditView.getText().toString().trim();
                    if(value.length() == 0){
                        Toast.makeText(getBaseContext(), "Please Enter a Phone Number", Toast.LENGTH_LONG).show();
                    }
                    else {
                        mDatabase.child("users").child(userId).child("phone").setValue(value);
                        phoneEditView.setEnabled(false);
                        phoneButton.setImageResource(R.drawable.ic_edit_orange_24dp);
                    }
                }
            }
        });

        // TODO: Delete account from authentication, not just from database
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);
                alert.setTitle("Delete");
                alert.setMessage("Are you sure to delete you account?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase2.child(userId).removeValue();

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProfileActivity.this, "Delete success", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                        mDatabase.child("users").child(userId).removeValue();
                    //    Intent intent = new Intent(this, LogIn.class);
                     //   startActivity(intent);
                        dialog.dismiss();
                        finish();
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
        });



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.whosFree_nav) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.events_nav) {
            Intent intent = new Intent(this, EventsActivity.class);
            startActivity(intent);
        } else if (id == R.id.friends_nav) {

        } else if (id == R.id.calendar_nav) {

        } else if (id == R.id.profile_nav) {
            // SHOULD NOT DO ANYTHING
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
