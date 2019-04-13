package com.example.ufree;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

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


            }
        });


    }

}
