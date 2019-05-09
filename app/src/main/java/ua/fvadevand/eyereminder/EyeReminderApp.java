package ua.fvadevand.eyereminder;

import android.app.Application;

import ua.fvadevand.eyereminder.utils.AppPrefs;
import ua.fvadevand.eyereminder.utils.NotificationUtils;

public class EyeReminderApp extends Application {

    private static EyeReminderApp sInstance;
    private AppPrefs mAppPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        NotificationUtils.registerNotificationChannels(this);
        mAppPrefs = new AppPrefs(getApplicationContext());
    }

    public static EyeReminderApp getInstance() {
        return sInstance;
    }

    public static AppPrefs getAppPrefs() {
        return getInstance().mAppPrefs;
    }
}
