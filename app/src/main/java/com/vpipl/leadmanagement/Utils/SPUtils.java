package com.vpipl.leadmanagement.Utils;

import android.content.Context;

/**
 * Created by mukesh on 25-05-2020.
 */
public class SPUtils {

    public static String IS_INSTALL = "sp_isInstall";

    public static String USER_INFO = "sp_userinfo";
    public static String REMEMBER_USER_INFO = "sp_remember_userinfo";
    public static String IS_LOGIN = "sp_isLogin";
    public static String USER_TYPE = "sp_user_type";
    public static String USER_FORM_NUMBER = "sp_user_form_id";
    public static String USER_profile_pic_byte_code = "bytecode";
    public static String USER_CURRENTSESS = "sp_currentSess";
    public static String notification_count = "notification_count";

    public static String Company_ID = "sp_Companyid";
    public static String Member_ID = "sp_Member_id";
    public static String MemberName = "sp_Member_name";
    public static String MemberMobileNo = "sp_Member_mobileNo";
    public static String MemberEMail = "sp_Member_EMail";
    public static String MemberDepartment = "sp_Department";
    public static String MemberAddress = "sp_Member_address";
    public static String MemberPasswd = "sp_member_password";
    public static String MemberDOJ = "sp_MemberDOJ";
    public static String MemberActiveStatus = "sp_ActiveStatus";
    public static String DeviceToken = "sp_DeviceToken";

    static Context context;
    private static SPUtils instance = new SPUtils();

    public SPUtils getInstance(Context con) {
        context = con;
        return instance;
    }
}