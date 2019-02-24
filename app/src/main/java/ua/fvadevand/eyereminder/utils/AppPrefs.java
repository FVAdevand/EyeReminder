package ua.fvadevand.eyereminder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPrefs {

    public static final String PREF_NEXT_REMINDER = "NEXT_REMINDER";
    private static final String PREF_PERIOD_REMINDER = "PERIOD_REMINDER";

    private AppPrefs() {
        //no instance
    }

    public static void savePeriodReminderInMinutes(Context context, long minutes) {
        getSharedPref(context).edit()
                .putLong(PREF_PERIOD_REMINDER, minutes)
                .apply();
    }

    public static long getPeriodReminderInMinutes(Context context) {
        return getSharedPref(context).getLong(PREF_PERIOD_REMINDER, 0);
    }

    public static void saveNextReminderInMillis(Context context, long timeInMillis) {
        getSharedPref(context).edit()
                .putLong(PREF_NEXT_REMINDER, timeInMillis)
                .apply();
    }

    public static long getNextReminderInMillis(Context context) {
        return getSharedPref(context).getLong(PREF_NEXT_REMINDER, 0);
    }

    public static void removeNextReminderInMillis(Context context) {
        getSharedPref(context).edit()
                .remove(PREF_NEXT_REMINDER)
                .apply();
    }

    public static SharedPreferences getSharedPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
