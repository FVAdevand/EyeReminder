package ua.fvadevand.eyereminder.receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import ua.fvadevand.eyereminder.utils.AppPrefs;
import ua.fvadevand.eyereminder.utils.NotificationUtils;
import ua.fvadevand.eyereminder.utils.Utils;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_STOP_REMINDER = "ua.fvadevand.eyereminder.STOP_REMINDER";
    public static final String ACTION_START_REMINDER = "ua.fvadevand.eyereminder.START_REMINDER";

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getPackageName(),
                NotificationReceiver.class.getName());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        switch (action) {
            case ACTION_START_REMINDER:
                NotificationUtils.cancelNotification(context);
                long periodInMillis = AppPrefs.getPeriodReminderInMinutes(context);
                if (periodInMillis > 0) {
                    long nextTimeInMillis = Utils.convertPeriodInNextTime(periodInMillis);
                    Utils.setAlarm(context, nextTimeInMillis);
                    AppPrefs.saveNextReminderInMillis(context, nextTimeInMillis);
                    NotificationUtils.showNotification(context, nextTimeInMillis);
                }
                break;
            case ACTION_STOP_REMINDER:
                Utils.cancelAlarm(context);
                NotificationUtils.cancelNotification(context);
                AppPrefs.removeNextReminderInMillis(context);
        }
    }
}
