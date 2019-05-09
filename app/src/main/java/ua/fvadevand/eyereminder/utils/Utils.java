package ua.fvadevand.eyereminder.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ua.fvadevand.eyereminder.R;
import ua.fvadevand.eyereminder.receivers.NotificationReceiver;

public class Utils {

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

    public static int getHoursFromPeriod(long periodInMinutes) {
        return (int) TimeUnit.MINUTES.toHours(periodInMinutes);
    }

    public static int getMinutesFromPeriod(long periodInMinutes) {
        return (int) (periodInMinutes - TimeUnit.HOURS.toMinutes(TimeUnit.MINUTES.toHours(periodInMinutes)));
    }

    public static long convertPeriodInNextTime(long periodInMinutes) {
        return System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(periodInMinutes);
    }

    public static void setAlarm(Context context, long nextTimeInMillis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextTimeInMillis,
                getStartPendingIntent(context));
    }

    public static void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getStartPendingIntent(context));
    }

    public static PendingIntent getStartPendingIntent(Context context) {
        return PendingIntent.getBroadcast(context,
                1,
                new Intent(NotificationReceiver.ACTION_START_REMINDER)
                        .setComponent(NotificationReceiver.getComponentName(context)),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getStopPendingIntent(Context context) {
        return PendingIntent.getBroadcast(context,
                2,
                new Intent(NotificationReceiver.ACTION_STOP_REMINDER)
                        .setComponent(NotificationReceiver.getComponentName(context)),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
