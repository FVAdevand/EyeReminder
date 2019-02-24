package ua.fvadevand.eyereminder.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ua.fvadevand.eyereminder.R;
import ua.fvadevand.eyereminder.utils.AppPrefs;
import ua.fvadevand.eyereminder.utils.NotificationUtils;
import ua.fvadevand.eyereminder.utils.Utils;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int MINIMUM_MINUTE_REMINDER = 1;
    private NumberPicker mHourNumPicker;
    private NumberPicker mMinuteNumPicker;
    private Button mStartBtn;
    private ViewGroup mContainer;
    private TextView mReminderTimeView;
    private TextView mReminderLabelView;
    private SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setupPickers();
        mSharedPref = AppPrefs.getSharedPref(getApplicationContext());
        displayNextReminderTime(AppPrefs.getNextReminderInMillis(getApplicationContext()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSharedPref.registerOnSharedPreferenceChangeListener(this);
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
        mReminderTimeView = findViewById(R.id.textview_reminder_time);
        mReminderLabelView = findViewById(R.id.textview_reminder_time_label);
        mStartBtn = findViewById(R.id.btn_start);
        mStartBtn.setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
    }

    private void setupPickers() {
        mHourNumPicker.setMinValue(0);
        mHourNumPicker.setMaxValue(23);
        mHourNumPicker.setValue(1);
        NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format(Locale.getDefault(), "%02d", value);
            }
        };
        mHourNumPicker.setFormatter(formatter);
        mMinuteNumPicker.setMinValue(0);
        mMinuteNumPicker.setMaxValue(59);
        mMinuteNumPicker.setValue(0);
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
        long periodInMillis = TimeUnit.HOURS.toMinutes(hour) + mMinuteNumPicker.getValue();
        if (periodInMillis < MINIMUM_MINUTE_REMINDER) {
            showSnackbar(R.string.message_error_minimum_time, Snackbar.LENGTH_SHORT);
            mMinuteNumPicker.setValue(15);
            return;
        }
        long nextTimeInMillis = Utils.convertPeriodInNextTime(periodInMillis);
        Utils.setAlarm(getApplicationContext(), nextTimeInMillis);
        AppPrefs.savePeriodReminderInMinutes(getApplicationContext(), periodInMillis);
        AppPrefs.saveNextReminderInMillis(getApplicationContext(), nextTimeInMillis);
    }

    private void stopReminder() {
        Utils.cancelAlarm(getApplicationContext());
        AppPrefs.removeNextReminderInMillis(getApplicationContext());
        NotificationUtils.cancelNotification(getApplicationContext());
    }

    private void showSnackbar(@StringRes int textResId, int duration) {
        Snackbar snackbar = Snackbar.make(mContainer, textResId, duration);
        if (duration == Snackbar.LENGTH_INDEFINITE) {
            snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
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
}
