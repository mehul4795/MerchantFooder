package com.blackdogs.merchantfooderbooktable;

import android.app.Application;
import android.content.Context;

/**
 * Created by BlackDogs on 19-09-2016.
 */
public class MyApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
