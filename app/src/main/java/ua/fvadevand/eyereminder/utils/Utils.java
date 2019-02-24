package ua.fvadevand.eyereminder.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ua.fvadevand.eyereminder.R;
import ua.fvadevand.eyereminder.receivers.NotificationReceiver;

public class Utils {

    private static final String TAG = "Utils";

    private Utils() {
        //no instance
    }

    public static String formatTime(Context context, long timeInMillis) {
        String formatTimeString;
        if (DateFormat.is24HourFormat(context)) {
            formatTimeString = context.getString(R.string.utils_format_time_24h);
        } else {
            formatTimeString = context.getString(R.string.utils_format_time_12h);
        }
        Format timeFormat = new SimpleDateFormat(formatTimeString, Locale.getDefault());
        return timeFormat.format(timeInMillis);
    }

    public static void setAlarm(Context context, long nextTimeInMillis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                nextTimeInMillis,
                getPendingIntent(context));
        Log.i(TAG, "setAlarm: ");
    }

    public static void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getPendingIntent(context));
        Log.i(TAG, "cancelAlarm: ");
    }

    public static long convertPeriodInNextTime(long periodInMillis) {
        return System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(periodInMillis);
    }

    private static PendingIntent getPendingIntent(Context context) {
        return PendingIntent.getBroadcast(context,
                2,
                new Intent(NotificationReceiver.ACTION_START_REMINDER)
                        .setComponent(new ComponentName(context.getPackageName(),
                                "ua.fvadevand.eyereminder.receivers.NotificationReceiver")),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
