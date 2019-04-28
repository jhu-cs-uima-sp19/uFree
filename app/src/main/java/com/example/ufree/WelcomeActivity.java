package com.example.ufree;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class WelcomeActivity extends AppCompatActivity {

    static View popupView;
    private FirebaseUser user;
    static Calendar endCalendar;
    static String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // get user id from Shared Preferences
        SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
        userId = sp.getString("userID", "dummy");
        // if there is no user id in Shared Preferences, go back to log in
        if (userId.equals("dummy")) {
            Log.d("debug", "userId in Shared Preferences is null");
            startActivity(new Intent(WelcomeActivity.this, LogIn.class));
            finish();
            return;
        }

        endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.MINUTE, 30);

        ImageView yes = (ImageView) findViewById(R.id.yes_welcome);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                popupView = inflater.inflate(R.layout.popup_window, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    popupWindow.setElevation(20);
                }
                // show the popup window with shadow
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

                // set up time picker for date button
                Button timeButton = popupView.findViewById(R.id.timeButton_welcome);
                timeButton.setText(MainActivity.timeFormat.format(endCalendar.getTime()));
                timeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment timePickerFragment = new TimePickerFragmentWelcome();
                        timePickerFragment.show(getSupportFragmentManager(), "timePickerWelcome");
                    }
                });

                // set up date button
                final Button dateButton = popupView.findViewById(R.id.dateButton_welcome);
                dateButton.setText(getString(R.string.today_nav));
                dateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dateButton.getText().toString().equals(getString(R.string.today_nav))) {
                            dateButton.setText(getString(R.string.tomorrow_nav));
                            endCalendar.add(Calendar.DATE, 1);
                        } else {
                            dateButton.setText(getString(R.string.today_nav));
                            endCalendar.add(Calendar.DATE, -1);
                        }
                    }
                });

                // set up onclick method for CANCEL
                TextView cancel = popupView.findViewById(R.id.cancel_welcome);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });

                // set up onclick method for CONFIRM
                TextView confirm = popupView.findViewById(R.id.confirm_welcome);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // initialize firebase
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference dbRef = database.getReference();

                        Calendar now = Calendar.getInstance();

                        if (now.getTimeInMillis() < endCalendar.getTimeInMillis()) {
                            dbRef.child("users").child(userId).child("startTime").setValue(now.getTimeInMillis());
                            dbRef.child("users").child(userId).child("endTime").setValue(endCalendar.getTimeInMillis());
                            dbRef.child("users").child(userId).child("isFree").setValue(true);
                            popupWindow.dismiss();
                            finish();
                        } else {
                            Toast.makeText(view.getContext(), "You cannot select time before current time", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        ImageView no = (ImageView) findViewById(R.id.no_welcome);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // initialize firebase
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = database.getReference();
                Calendar now = Calendar.getInstance();
                dbRef.child("users").child(userId).child("isFree").setValue(false);
                dbRef.child("users").child(userId).child("endTime").setValue(now.getTimeInMillis());
                finish();
            }
        });

    }

    // Time picker for time button in the pop up window
    public static class TimePickerFragmentWelcome extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of TimePickerDialog and return it
            Calendar calendar = Calendar.getInstance();
            // free for 30 minutes by default
            calendar.add(Calendar.MINUTE, 30);
            int endHour = calendar.get(Calendar.HOUR_OF_DAY);
            int endMinute = calendar.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, endHour, endMinute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            endCalendar.set(
                    endCalendar.get(Calendar.YEAR),
                    endCalendar.get(Calendar.MONTH),
                    endCalendar.get(Calendar.DAY_OF_MONTH),
                    hourOfDay, minute
            );
            Button timeButton = popupView.findViewById(R.id.timeButton_welcome);
            timeButton.setText(MainActivity.timeFormat.format(endCalendar.getTime()));
        }
    }

}
