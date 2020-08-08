package com.vpipl.leadmanagement.Utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mukesh on 25-05-2020.
 */
public class AppController extends Application {

    /**
     * for State Listing
     */
    public static ArrayList<HashMap<String, String>> PhotoList = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> NotificationList = new ArrayList<>();

    private static AppController mInstance;
    private static SharedPreferences sp_userinfo;
    private static SharedPreferences sp_rememberuserinfo;
    private static SharedPreferences sp_isLogin;

    private static SharedPreferences sp_isInstall;
    private static SharedPreferences sp_currentSession;
    private static SharedPreferences sp_notification_count;

    /**
     * used to get instance globally of SharedPreferences
     */
    public static synchronized SharedPreferences getSpUserInfo() {
        return sp_userinfo;
    }

    public static synchronized SharedPreferences getSpRememberUserInfo() {
        return sp_rememberuserinfo;
    }

    public static synchronized SharedPreferences getSpIsInstall() {
        return sp_isInstall;
    }

    /**
     * used to get instance globally of SharedPreferences
     */
    public static synchronized SharedPreferences getSpIsLogin() {
        return sp_isLogin;
    }

    public static synchronized SharedPreferences getNotification_count() {
        return sp_notification_count;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            mInstance = this;

            /** to call initialize SharedPreferences  */
            initSharedPreferences();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * used to initialize instance globally of SharedPreferences
     */
    private void initSharedPreferences() {
        try {
            sp_currentSession = getApplicationContext().getSharedPreferences(SPUtils.USER_CURRENTSESS, Context.MODE_PRIVATE);
            sp_userinfo = getApplicationContext().getSharedPreferences(SPUtils.USER_INFO, Context.MODE_PRIVATE);
            sp_rememberuserinfo = getApplicationContext().getSharedPreferences(SPUtils.REMEMBER_USER_INFO, Context.MODE_PRIVATE);
            sp_isLogin = getApplicationContext().getSharedPreferences(SPUtils.IS_LOGIN, Context.MODE_PRIVATE);
            sp_notification_count = getApplicationContext().getSharedPreferences(SPUtils.notification_count, Context.MODE_PRIVATE);

            sp_isInstall = getApplicationContext().getSharedPreferences(SPUtils.IS_INSTALL, Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}