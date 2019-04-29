package com.example.ufree;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseUser user;
    static private User currentUser;
    static String userId;

    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();

    private final int GALLERY = 0;
    private final int CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // set up title of app bar
        getSupportActionBar().setTitle("Profile");

        /* Set up navigation drawer */
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
        final DatabaseReference dbRef = database.getInstance().getReference();
        final DatabaseReference mDatabase2 = database.getReference("users");


        final FirebaseAuth mAuth = FirebaseAuth.getInstance();


        // disable edit text
        final EditText phoneEditView = (EditText) findViewById(R.id.phoneEditText_profile);
        phoneEditView.setEnabled(false);

        final EditText nameEditView = (EditText) findViewById(R.id.nameEditText_profile);
        nameEditView.setEnabled(false);

        final TextView emailTV = findViewById(R.id.email_profile);

        Button deleteAccount = findViewById(R.id.deleteAccountButton_profile);

        final View passView = findViewById(R.id.ChangePassView);
        final ImageView userNameButton = findViewById(R.id.editNameButton_profile);
        final ImageView phoneButton = findViewById(R.id.editPhoneButton_profile);
        ImageView passButton = findViewById(R.id.changePasswordButton_profile);


        // TODO: DIRECTLY GET USER ID FROM DATABASE
        user = FirebaseAuth.getInstance().getCurrentUser();
        String temp = user.getEmail().replaceAll("@", "");
        userId = temp.replaceAll("\\.", "");

        dbRef.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ImageView picView = (ImageView) findViewById(R.id.profilePic_profile);
                    currentUser = dataSnapshot.getValue(User.class);
                    String displayName = currentUser.getFullName();
                    nameEditView.setText(displayName);
                    phoneEditView.setText(dataSnapshot.getValue(User.class).getPhone());
                    emailTV.setText(dataSnapshot.getValue(User.class).getEmail());
                    String photoUrl = currentUser.getProfilePic();
                    System.out.println("hELLO" + photoUrl);
                    if (photoUrl != null) {
                        Glide.with(getApplicationContext())
                                .load(photoUrl)
                                .into(picView);
                    }

                    /* Display user info in navigation header */
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    View navHeader = navigationView.getHeaderView(0);
                    if (navHeader != null) {
                        TextView nameTextView = navHeader.findViewById(R.id.name_nav);
                        TextView emailTextView = navHeader.findViewById(R.id.email_nav);
                        nameTextView.setText(currentUser.getFullName());
                        emailTextView.setText(currentUser.getEmail());
                        Switch toggle = findViewById(R.id.toggle_nav);
                        Button currentStatusButton = findViewById(R.id.timeButton_nav);
                        toggle.setChecked(currentUser.getIsFree());
                        Time t = new Time(currentUser.getEndHour(), currentUser.getEndMinute(), 0);
                        currentStatusButton.setText(MainActivity.timeFormat.format(t));
                    } else {
                        Log.d("debug", "Nav view is null");
                        Log.d("debug", "Nav view: " + navigationView);
                        Log.d("debug", "Nav header: " + navHeader);
                    }
                } else {
                    startActivity(new Intent(ProfileActivity.this, LogIn.class));
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        // Set up listener for toggle and time button in nav drawer
        Switch toggleNav = findViewById(R.id.toggle_nav);
        Button currentStatusButton = findViewById(R.id.timeButton_nav);
        toggleNav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dbRef.child("users").child(userId).child("isFree").setValue(isChecked);
            }
        });
        currentStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = new ProfileActivity.TimePickerFragmentNav();
                timePickerFragment.show(getSupportFragmentManager(), "timePickerNav");
            }
        });

        // Set up listener for log out in nav drawer
        ImageView exitImageView = findViewById(R.id.exitImageView_nav);
        TextView logoutTextView = findViewById(R.id.logout_nav);
        exitImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, LogIn.class));
                finish();
            }
        });
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, LogIn.class));
                finish();
            }
        });

        ImageView picView = (ImageView) findViewById(R.id.profilePic_profile);
        picView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.app.AlertDialog.Builder pictureDialog =
                        new android.app.AlertDialog.Builder(ProfileActivity.this);
                pictureDialog.setTitle("Choose Profile Picture");
                String[] pictureDialogItems = {
                        "Select photo from gallery",
                        "Capture photo from camera" };
                pictureDialog.setItems(pictureDialogItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        choosePhotoFromGallery();
                                        break;
                                    case 1:
                                        takePhotoFromCamera();
                                        break;
                                }
                            }
                        });
                pictureDialog.show();
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
                        dbRef.child("users").child(userId).child("fullName").setValue(value);
                        nameEditView.setEnabled(false);
                        userNameButton.setImageResource(R.drawable.ic_edit_orange_24dp);
                    }
                }
            }
        });


        passView.setOnClickListener(new View.OnClickListener() {
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
                        dbRef.child("users").child(userId).child("phone").setValue(value);
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

                        dbRef.child("users").child(userId).removeValue();

                        Intent intent = new Intent(getApplicationContext(), LogIn.class);
                        startActivity(intent);
                        dialog.dismiss();
                        finish();
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
        });



    }

    private void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, this.GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, this.CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri filePath = null;
        // Something went wrong/user cancelled request
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        // If user chose gallery
        if (requestCode == GALLERY) {
            if (data != null) {
                filePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                    ImageView picView = (ImageView) findViewById(R.id.profilePic_profile);
                    picView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Gallery image failed");
                }
            }
        }
        if (requestCode == CAMERA) {
            filePath = data.getData();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ImageView picView = (ImageView) findViewById(R.id.profilePic_profile);
            picView.setImageBitmap(bitmap);
        }

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference(
                "profilePics/" + currentUser.getEmail().replaceAll("[^a-zA-Z0-9]", "") + ".jpg");
        if (filePath != null) {
            storageReference.putFile(filePath).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    dbref.child("users").child(currentUser.getEmail().replaceAll("[^a-zA-Z0-9]", ""))
                                            .child("profilePic").setValue(uri.toString());
                                }
                            });
                        }
                    }
            );
        }
    }

    // Time picker for time button in the ** nav drawer **
    public static class TimePickerFragmentNav extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of TimePickerDialog and return it
            // TODO: DIRECTLY GET USER ID FROM DATABASE
            int endHour = 0;
            int endMinute = 0;
            if (currentUser != null) {
                endHour = currentUser.getEndHour();
                endMinute = currentUser.getEndMinute();
            } else {
                Log.d("debug", "current user is null from time picker in nav drawer");
            }
            return new TimePickerDialog(getActivity(), this, endHour, endMinute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calendar = Calendar.getInstance();
            int currentDay = calendar.get(Calendar.DAY_OF_YEAR);
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMinute = calendar.get(Calendar.MINUTE);
            int currentTime = currentHour * 60 + currentMinute;
            int endDay = currentUser.getEndDay();
            // if user set free time less than current time
            if (currentDay == endDay && currentTime >= hourOfDay * 60 + minute) {
                Toast.makeText(getContext(), "You cannot set free time before current time", Toast.LENGTH_LONG).show();
                DialogFragment timePickerFragment = new ProfileActivity.TimePickerFragmentNav();
                timePickerFragment.show(getActivity().getSupportFragmentManager(), "timePickerNav");
            } else {
                // update selected calendar object
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = database.getReference();
                dbRef.child("users").child(userId).child("endHour").setValue(hourOfDay);
                dbRef.child("users").child(userId).child("endMinute").setValue(minute);
                Log.d("debug", "user id is null");

                // TODO: enable change date
                // change text view for time button
                Button timeButton = getActivity().findViewById(R.id.timeButton_nav);
                Time selectedTime = new Time(hourOfDay, minute, 0);
                timeButton.setText(MainActivity.timeFormat.format(selectedTime));
            }
        }
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
