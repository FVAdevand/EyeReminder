package ua.fvadevand.eyereminder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class AppPrefs {

    public static final String PREF_NEXT_REMINDER = "NEXT_REMINDER";
    private static final String PREF_PERIOD_REMINDER = "PERIOD_REMINDER";
    private final SharedPreferences mSharedPref;

    public AppPrefs(final Context context) {
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public SharedPreferences getSharedPref() {
        return mSharedPref;
    }

    public void savePeriodReminderInMinutes(final long minutes) {
        mSharedPref.edit()
                .putLong(PREF_PERIOD_REMINDER, minutes)
                .apply();
    }

    public long getPeriodReminderInMinutes() {
        return mSharedPref.getLong(PREF_PERIOD_REMINDER, 0);
    }

    public void saveNextReminderInMillis(final long timeInMillis) {
        mSharedPref.edit()
                .putLong(PREF_NEXT_REMINDER, timeInMillis)
                .apply();
    }

    public long getNextReminderInMillis() {
        return mSharedPref.getLong(PREF_NEXT_REMINDER, 0);
    }

    public void removeNextReminderInMillis() {
        mSharedPref.edit()
                .remove(PREF_NEXT_REMINDER)
                .apply();
    }

}
