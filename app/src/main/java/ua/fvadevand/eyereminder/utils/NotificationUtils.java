package ua.fvadevand.eyereminder.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.drawable.Icon;

import ua.fvadevand.eyereminder.R;

public class NotificationUtils {

    private static int NOTIFICATION_ID = 147;

    private NotificationUtils() {
        //no instance
    }

    public static void registerNotificationChannels(Context context) {
        String id = context.getString(R.string.reminder_channel_id);
        String name = context.getString(R.string.reminder_channel_name);
        String description = context.getString(R.string.reminder_channel_description);
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(description);
        channel.enableLights(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void showNotification(Context context, long nextReminderTime) {
        String contentText = context.getString(R.string.notification_text, Utils.formatTime(context, nextReminderTime));
        Notification.Builder notificationBuilder = new Notification.Builder(context, context.getString(R.string.reminder_channel_id))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(contentText)
                .setStyle(new Notification.BigTextStyle().bigText(contentText))
                .setSmallIcon(R.drawable.ic_monitor_monocolor)
                .setShowWhen(true)
                .addAction(getRestartAction(context))
                .addAction(getStopAction(context));
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    public static void cancelNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }

    private static Notification.Action getStopAction(Context context) {
        return new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.ic_close),
                context.getString(R.string.action_stop),
                Utils.getStopPendingIntent(context))
                .build();
    }

    private static Notification.Action getRestartAction(Context context) {
        return new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.ic_restart),
                context.getString(R.string.action_restart),
                Utils.getStartPendingIntent(context))
                .build();
    }

}
