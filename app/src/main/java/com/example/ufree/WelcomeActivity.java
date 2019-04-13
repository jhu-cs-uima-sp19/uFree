package com.example.ufree;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class WelcomeActivity extends AppCompatActivity {

    View popupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

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

                // set up seek bar
                SeekBar seekbar = popupView.findViewById(R.id.seekBar_welcome);
                seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int currentProgress;
                    TextView currentProgressTextView = popupView.findViewById(R.id.currentProgressTextView_welcome);

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        currentProgress = progress;
                        double currentHours = currentProgress * 23.5 / 100 + 0.5;
                        String text = getString(R.string.currentProgressText_welcome, currentHours);
                        currentProgressTextView.setText(text);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        double currentHours = currentProgress * 23.5 / 100 + 0.5;
                        String text = getString(R.string.currentProgressText_welcome, currentHours);
                        currentProgressTextView.setText(text);
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        double currentHours = currentProgress * 23.5 / 100 + 0.5;
                        String text = getString(R.string.currentProgressText_welcome, currentHours);
                        currentProgressTextView.setText(text);
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

                        // TODO: get user id

                        String userId = "minqitest";

                        // calculate start and end time
                        Calendar calendar = Calendar.getInstance();
                        // TODO: integrate year as part of free time
                        int startDay = calendar.get(Calendar.DAY_OF_YEAR);
                        int startHour = calendar.get(Calendar.HOUR_OF_DAY);
                        int startMinute = calendar.get(Calendar.MINUTE);

                        SeekBar seekbar = popupView.findViewById(R.id.seekBar_welcome);
                        int freeMinute = (int)((seekbar.getProgress() * 23.5 / 100 + 0.5) * 60);
                        Log.d("free time", "freeMinute: " + freeMinute);

                        int endDay = startDay;
                        int endTime = startHour * 60 + startMinute + freeMinute;
                        //Log.d("free time", "endTime: " + endTime);
                        if (endTime >= 24 * 60) {
                            endTime -= 24 * 60;
                            endDay++;
                        }

                        int endHour = endTime / 60;
                        int endMinute = endTime - endHour * 60;

                        dbRef.child("users").child(userId).child("startDay").setValue(startDay);
                        dbRef.child("users").child(userId).child("startHour").setValue(startHour);
                        dbRef.child("users").child(userId).child("startMinute").setValue(startMinute);
                        dbRef.child("users").child(userId).child("endDay").setValue(endDay);
                        dbRef.child("users").child(userId).child("endHour").setValue(endHour);
                        dbRef.child("users").child(userId).child("endMinute").setValue(endMinute);
                        dbRef.child("users").child(userId).child("isFree").setValue(true);

                        finish();
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
                // TODO: get user id
                String userId = "minqitest";
                dbRef.child("users").child(userId).child("isFree").setValue(false);
                finish();
            }
        });

    }

}
