package ua.fvadevand.eyereminder;

import android.app.Application;

import ua.fvadevand.eyereminder.utils.NotificationUtils;

public class EyeReminderApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationUtils.registerNotificationChannels(this);
    }
}
