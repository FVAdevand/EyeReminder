package ua.fvadevand.eyereminder.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;

import ua.fvadevand.eyereminder.R;
import ua.fvadevand.eyereminder.receivers.NotificationReceiver;

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
        notificationManager.createNotificationChannel(channel);
    }

    public static void showNotification(Context context) {
        Notification.Builder notificationBuilder = new Notification.Builder(context, context.getString(R.string.reminder_channel_id))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getText(R.string.notification_title))
                .setSmallIcon(R.drawable.ic_monitor_monocolor)
                .addAction(getStopAction(context));
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void cancelNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private static Notification.Action getStopAction(Context context) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                1,
                new Intent(NotificationReceiver.ACTION_STOP_REMINDER)
                        .setComponent(new ComponentName(context.getPackageName(),
                                "ua.fvadevand.eyereminder.receivers.NotificationReceiver")),
                PendingIntent.FLAG_UPDATE_CURRENT);
        return new Notification.Action.Builder(Icon.createWithResource(context, R.drawable.ic_close),
                context.getString(R.string.action_stop),
                pendingIntent)
                .build();
    }

}
