package com.example.ufree;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.Calendar;

import static com.example.ufree.MainActivity.RESULT_CANCEL;
import static com.example.ufree.MainActivity.RESULT_CONFIRM;

public class CalenderPickerActivity extends AppCompatActivity {

    static Calendar selectedCalendar = Calendar.getInstance();
    static String[] dayOfWeeks = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    static String[] months = new String[]{"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_picker);

        Calendar calendar = Calendar.getInstance();
        Intent intent = getIntent();
        int year = intent.getIntExtra("year", calendar.get(Calendar.YEAR));
        int month = intent.getIntExtra("month", calendar.get(Calendar.MONTH));
        int dayOfMonth = intent.getIntExtra("dayOfMonth", calendar.get(Calendar.DAY_OF_MONTH));

        final CalendarView calendarView = findViewById(R.id.calendarView);

        selectedCalendar.set(year, month, dayOfMonth, 0, 0, 0);
        // display selected date
        calendarView.setDate(selectedCalendar.getTimeInMillis());

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                selectedCalendar.set(year, month, dayOfMonth, 0, 0, 0);
                TextView dayOfWeekTextView = findViewById(R.id.dayOfWeek_calendarView);
                dayOfWeekTextView.setText(dayOfWeeks[selectedCalendar.get(Calendar.DAY_OF_WEEK) - 1]);
                TextView monthTextView = findViewById(R.id.month_calendarPicker);
                monthTextView.setText(months[selectedCalendar.get(Calendar.MONTH)]);
                TextView dayTextView = findViewById(R.id.day_calendarPicker);
                dayTextView.setText(selectedCalendar.get(Calendar.DAY_OF_MONTH) + "");
                TextView yearTextView = findViewById(R.id.year_calendarPicker);
                yearTextView.setText(selectedCalendar.get(Calendar.YEAR) + "");
            }
        });

        /* Set up CANCEL */
        TextView cancel = findViewById(R.id.cancel_calendarPicker);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emptyIntent = new Intent();
                setResult(RESULT_CANCEL, emptyIntent);
                finish();
            }
        });

        /* Set up CONFIRM */
        TextView confirm = findViewById(R.id.confirm_calendarPicker);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                int dayNow = now.get(Calendar.DAY_OF_YEAR);
                int selectedDay = selectedCalendar.get(Calendar.DAY_OF_YEAR);
                if (selectedDay == dayNow || selectedDay - dayNow == 1) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("year", selectedCalendar.get(Calendar.YEAR));
                    returnIntent.putExtra("month", selectedCalendar.get(Calendar.MONTH));
                    returnIntent.putExtra("dayOfMonth", selectedCalendar.get(Calendar.DAY_OF_MONTH));
                    setResult(RESULT_CONFIRM, returnIntent);
                    finish();
                } else {
                    // show error message
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.calendarView),
                            "You can only select either today or tomorrow",
                            Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
            }
        });

    }
}
