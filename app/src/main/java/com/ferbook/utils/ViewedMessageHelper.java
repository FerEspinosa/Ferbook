package com.ferbook.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.ferbook.providers.Authprovider;
import com.ferbook.providers.UsersProvider;

import java.security.AuthProvider;
import java.util.List;

public class ViewedMessageHelper {

    public static void updateOnline (boolean connected, final Context context){
        UsersProvider usersProvider = new UsersProvider();
        Authprovider authProvider = new Authprovider();

        if (authProvider.getUid()!=null){
            if (isApplicationSentToBackground(context)){
                usersProvider.updateOnline(authProvider.getUid(),false);
            } else if (connected) {
                usersProvider.updateOnline(authProvider.getUid(),true);
            }
        }
    }

    public static boolean isApplicationSentToBackground (final Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List <ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);

        if (!tasks.isEmpty()){
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())){
                return true;
            }
        }
        return false;

    }
}
