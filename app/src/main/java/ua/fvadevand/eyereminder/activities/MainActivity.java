package ua.fvadevand.eyereminder.activities;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ua.fvadevand.eyereminder.R;
import ua.fvadevand.eyereminder.utils.AppPrefs;
import ua.fvadevand.eyereminder.utils.NotificationUtils;
import ua.fvadevand.eyereminder.utils.Utils;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int MINIMUM_MINUTE_REMINDER = 15;
    private static final int DEFAULT_PERIOD_HOURS = 1;
    private static final int DEFAULT_PERIOD_MINUTES = 0;
    private NumberPicker mHourNumPicker;
    private NumberPicker mMinuteNumPicker;
    private Button mStartBtn;
    private ViewGroup mContainer;
    private TextView mReminderTimeView;
    private TextView mReminderLabelView;
    private TextView mStartTimeView;
    private SharedPreferences mSharedPref;
    private long mStartTimeInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setupPeriodPickers();
        mSharedPref = AppPrefs.getSharedPref(getApplicationContext());
        displayPeriod(AppPrefs.getPeriodReminderInMinutes(getApplicationContext()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSharedPref.registerOnSharedPreferenceChangeListener(this);
        displayNextReminderTime(AppPrefs.getNextReminderInMillis(getApplicationContext()));
        displayStartTime();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSharedPref.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initView() {
        mContainer = findViewById(R.id.main_container);
        mHourNumPicker = findViewById(R.id.num_picker_hour);
        mMinuteNumPicker = findViewById(R.id.num_picker_minute);
        mReminderTimeView = findViewById(R.id.text_view_reminder_time);
        mReminderLabelView = findViewById(R.id.text_view_reminder_time_label);
        mStartTimeView = findViewById(R.id.text_view_start_time_reminders);
        mStartBtn = findViewById(R.id.btn_start);
        mStartBtn.setOnClickListener(this);
        findViewById(R.id.ibtn_time_picker).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
    }

    private void setupPeriodPickers() {
        mHourNumPicker.setMinValue(0);
        mHourNumPicker.setMaxValue(23);
        NumberPicker.Formatter formatter = value -> String.format(Locale.getDefault(), "%02d", value);
        mHourNumPicker.setFormatter(formatter);
        mMinuteNumPicker.setMinValue(0);
        mMinuteNumPicker.setMaxValue(59);
        mMinuteNumPicker.setFormatter(formatter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                startReminder();
                break;
            case R.id.btn_stop:
                stopReminder();
                break;
            case R.id.ibtn_time_picker:
                setupTimePicker();
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (AppPrefs.PREF_NEXT_REMINDER.equals(key)) {
            long nextReminderInMillis = sharedPreferences.getLong(AppPrefs.PREF_NEXT_REMINDER, 0);
            displayNextReminderTime(nextReminderInMillis);
        }
    }

    private void startReminder() {
        int hour = mHourNumPicker.getValue();
        long periodInMinutes = TimeUnit.HOURS.toMinutes(hour) + mMinuteNumPicker.getValue();
        if (periodInMinutes < MINIMUM_MINUTE_REMINDER) {
            showSnackbar(getString(R.string.message_error_period_time, MINIMUM_MINUTE_REMINDER), Snackbar.LENGTH_SHORT);
            mMinuteNumPicker.setValue(15);
            return;
        }
        long nextTimeInMillis;
        if (mStartTimeInMillis > System.currentTimeMillis()) {
            nextTimeInMillis = mStartTimeInMillis;
        } else {
            nextTimeInMillis = Utils.convertPeriodInNextTime(periodInMinutes);
        }
        Utils.setAlarm(getApplicationContext(), nextTimeInMillis);
        AppPrefs.savePeriodReminderInMinutes(getApplicationContext(), periodInMinutes);
        AppPrefs.saveNextReminderInMillis(getApplicationContext(), nextTimeInMillis);
        displayStartTime();
    }

    private void stopReminder() {
        Utils.cancelAlarm(getApplicationContext());
        AppPrefs.removeNextReminderInMillis(getApplicationContext());
        AppPrefs.savePeriodReminderInMinutes(getApplicationContext(), 0);
        NotificationUtils.cancelNotification(getApplicationContext());
        mStartTimeInMillis = 0;
        displayStartTime();
    }

    private void setupTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            if (calendar.getTimeInMillis() >= System.currentTimeMillis()) {
                mStartTimeInMillis = calendar.getTimeInMillis();
            } else {
                mStartTimeInMillis = 0;
                showSnackbar(getString(R.string.message_error_start_time), Snackbar.LENGTH_SHORT);
            }
            displayStartTime();
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    private void showSnackbar(String text, int duration) {
        Snackbar snackbar = Snackbar.make(mContainer, text, duration);
        if (duration == Snackbar.LENGTH_INDEFINITE) {
            snackbar.setAction(android.R.string.ok, v -> {
            });
        }
        snackbar.show();
    }

    private void displayNextReminderTime(long timeInMillis) {
        if (timeInMillis > 0) {
            mStartBtn.setText(R.string.btn_restart);
            mReminderTimeView.setVisibility(View.VISIBLE);
            mReminderLabelView.setText(R.string.label_next_reminder);
            mReminderTimeView.setText(Utils.formatTime(this, timeInMillis));
        } else {
            mStartBtn.setText(R.string.btn_start);
            mReminderLabelView.setText(R.string.label_reminder_not_set);
            mReminderTimeView.setVisibility(View.INVISIBLE);
        }
    }

    private void displayStartTime() {
        if (mStartTimeInMillis > System.currentTimeMillis()) {
            mStartTimeView.setText(Utils.formatTime(this, mStartTimeInMillis));
        } else {
            mStartTimeView.setText(R.string.default_start_time_picker);
        }
    }

    private void displayPeriod(long periodInMillis) {
        if (periodInMillis > 0) {
            mHourNumPicker.setValue(Utils.getHoursFromPeriod(periodInMillis));
            mMinuteNumPicker.setValue(Utils.getMinutesFromPeriod(periodInMillis));
        } else {
            mHourNumPicker.setValue(DEFAULT_PERIOD_HOURS);
            mMinuteNumPicker.setValue(DEFAULT_PERIOD_MINUTES);
        }
    }
}
