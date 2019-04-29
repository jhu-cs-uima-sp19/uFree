package com.example.ufree;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
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
import android.os.Bundle;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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

import static com.example.ufree.MainActivity.timeFormat;

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
        String temp = user.getEmail().replaceAll("[^a-zA-Z0-9]", "");
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

                    /* NAV DRAWER */
                    /* Display user info in navigation header */
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    View navHeader = navigationView.getHeaderView(0);

                    TextView nameTextView = navHeader.findViewById(R.id.name_nav);
                    TextView emailTextView = navHeader.findViewById(R.id.email_nav);
                    nameTextView.setText(currentUser.getFullName());
                    emailTextView.setText(currentUser.getEmail());

                    Switch toggle = findViewById(R.id.toggle_nav);
                    toggle.setChecked(currentUser.getIsFree());

                    Button currentStatusButton = findViewById(R.id.timeButton_nav);
                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.setTimeInMillis(currentUser.getEndTime());
                    currentStatusButton.setText(timeFormat.format(endCalendar.getTime()));

                    Button dateButtonNav = findViewById(R.id.dateButton_nav);
                    Calendar today = Calendar.getInstance();
                    if (today.get(Calendar.DAY_OF_YEAR) == endCalendar.get(Calendar.DAY_OF_YEAR)) {
                        dateButtonNav.setText(getString(R.string.today_nav));
                    } else {
                        dateButtonNav.setText(getString(R.string.tomorrow_nav));
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

        /* NAV DRAWER */
        // Set up listener for toggle and time button in nav drawer
        Switch toggleNav = findViewById(R.id.toggle_nav);
        Button currentStatusButton = findViewById(R.id.timeButton_nav);
        Button dateButtonNav = findViewById(R.id.dateButton_nav);
        toggleNav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dbRef.child("users").child(userId).child("isFree").setValue(isChecked);
            }
        });
        currentStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = new TimePickerFragmentNav();
                timePickerFragment.show(getSupportFragmentManager(), "timePickerNav");
            }
        });
        dateButtonNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button dateButtonNav = v.findViewById(R.id.dateButton_nav);

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTimeInMillis(currentUser.getEndTime());

                Calendar newEnd = Calendar.getInstance();
                newEnd.set(
                        newEnd.get(Calendar.YEAR),
                        newEnd.get(Calendar.MONTH),
                        newEnd.get(Calendar.DAY_OF_MONTH),
                        endCalendar.get(Calendar.HOUR_OF_DAY),
                        endCalendar.get(Calendar.MINUTE)
                );

                // today --> tomorrow
                if (dateButtonNav.getText().toString().equals(getString(R.string.today_nav))) {
                    // add one day
                    newEnd.add(Calendar.DATE, 1);
                    // update end time in database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference dbRef = database.getReference();
                    dbRef.child("users").child(userId).child("endTime").setValue(newEnd.getTimeInMillis());
                    // change button text
                    dateButtonNav.setText(getString(R.string.tomorrow_nav));
                } else {
                    // tomorrow --> today
                    // do not change day
                    Calendar now = Calendar.getInstance();
                    if (newEnd.getTimeInMillis() < now.getTimeInMillis()) {
                        Toast.makeText(v.getContext(), "You cannot set free time before current time", Toast.LENGTH_SHORT).show();
                    } else {
                        // update end time in database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference dbRef = database.getReference();
                        dbRef.child("users").child(userId).child("endTime").setValue(newEnd.getTimeInMillis());
                        // change button text
                        dateButtonNav.setText(getString(R.string.today_nav));
                    }
                }
            }
        });

        // Set up listener for log out
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
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    userNameButton.setImageResource(R.drawable.ic_check_orange_24dp);
                }
                else if(nameEditView.isEnabled()){
                    String value = nameEditView.getText().toString().trim();
                    boolean valid = true;
                    for (int i = 0; i < value.length(); i++) {
                        char c = value.charAt(i);
                        if (!Character.isLetter(c) && c != ' ') {
                            valid = false;
                            break;
                        }
                    }
                    if(value.length() == 0){
                        Toast.makeText(getBaseContext(), "Please enter a valid name", Toast.LENGTH_SHORT).show();
                    }
                    else if(!valid){
                        Toast.makeText(getBaseContext(), "A valid name cannot contain non-letter character", Toast.LENGTH_LONG).show();
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
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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

        //ToDO
        //delete Auth???? maybe lost in merging
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
    /* NAV DRAWER */
    // Time picker for time button in the ** NAV DRAWER **
    public static class TimePickerFragmentNav extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of TimePickerDialog and return it
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentUser.getEndTime());
            int endHour = calendar.get(Calendar.HOUR_OF_DAY);
            int endMinute = calendar.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, endHour, endMinute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar now = Calendar.getInstance();
            int currentDay = now.get(Calendar.DAY_OF_YEAR);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentUser.getEndTime());
            calendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    hourOfDay, minute);
            int endDay = calendar.get(Calendar.DAY_OF_YEAR);

            // if user set free time less than current time
            if (currentDay == endDay && now.getTimeInMillis() >= calendar.getTimeInMillis()) {
                Toast.makeText(getContext(), "You cannot set free time before current time", Toast.LENGTH_LONG).show();
                DialogFragment timePickerFragment = new TimePickerFragmentNav();
                timePickerFragment.show(getActivity().getSupportFragmentManager(), "timePickerNav");
            } else {
                // update selected calendar object
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = database.getReference();

                dbRef.child("users").child(userId).child("endTime").setValue(calendar.getTimeInMillis());

                // change text view for time button
                Button timeButton = getActivity().findViewById(R.id.timeButton_nav);
                timeButton.setText(timeFormat.format(calendar.getTime()));
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
            Intent intent = new Intent(this, FriendsActivity.class);
            startActivity(intent);
        } else if (id == R.id.calendar_nav) {

        } else if (id == R.id.profile_nav) {
            // SHOULD NOT DO ANYTHING
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        // set Who's Free to be selected
        navigationView.getMenu().getItem(4).setChecked(true);
    }
}
