package ua.fvadevand.eyereminder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ua.fvadevand.eyereminder.utils.AppPrefs;
import ua.fvadevand.eyereminder.utils.NotificationUtils;
import ua.fvadevand.eyereminder.utils.Utils;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_STOP_REMINDER = "ua.fvadevand.eyereminder.STOP_REMINDER";
    public static final String ACTION_START_REMINDER = "ua.fvadevand.eyereminder.START_REMINDER";
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: ");
        String action = intent.getAction();
        if (action == null) return;
        switch (action) {
            case ACTION_START_REMINDER:
                NotificationUtils.showNotification(context);
                long periodInMillis = AppPrefs.getPeriodReminderInMinutes(context);
                if (periodInMillis > 0) {
                    long nextTimeInMillis = Utils.convertPeriodInNextTime(periodInMillis);
                    Utils.setAlarm(context, nextTimeInMillis);
                    AppPrefs.saveNextReminderInMillis(context, nextTimeInMillis);
                }
                break;
            case ACTION_STOP_REMINDER:
                NotificationUtils.cancelNotification(context);
                AppPrefs.removeNextReminderInMillis(context);
        }
    }
}