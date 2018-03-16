package com.finickyltd.finicky.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.finickyltd.finicky.models.FbUser;
import com.finickyltd.finicky.models.User;

public class SharedPrefManager {
    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private static final String SHARED_PREF_NAME = "finicky";

    private static final String KEY_USER_ID = "keyuserid";
    private static final String KEY_USER_NAME = "keyusername";
    private static final String KEY_USER_EMAIL = "keyuseremail";
    private static final String KEY_USER_FbID = "keyFbId";
    private static final String KEY_USER_FbName = "keyFbName";
    private static final String KEY_USER_PWD = "keyuserpwd";
    private static final String KEY_IS_FITERSETTING = "keyIsFiterSetting";
    private static final String KEY_USER_ACCOUNT_TYPE = "keyAccountType";
    private static final String KEY_USER_MEMBERSHIP_TYPE = "keymemberType";

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public boolean   userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_PWD, user.getPassword());
        editor.putString(KEY_USER_ACCOUNT_TYPE, user.getAccount_type());
        editor.putString(KEY_USER_MEMBERSHIP_TYPE, user.getMembership_type());
        editor.apply();
        return true;
    }

    public boolean fbUserLogin(FbUser fbUser) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, fbUser.getId());
        editor.putString(KEY_USER_EMAIL, fbUser.getFb_id());
        editor.putString(KEY_USER_FbName, fbUser.getFb_name());
        editor.putString(KEY_USER_MEMBERSHIP_TYPE, fbUser.getMembership_type());
        editor.apply();
        return true;
    }

    public boolean readIsFilterSetting(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        boolean isRegistered = sharedPreferences.getBoolean(KEY_IS_FITERSETTING, false);
        return isRegistered;
    }

    public void writeIsFilterSetting(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_FITERSETTING, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences.getString(KEY_USER_EMAIL, null) != null)
            return true;
        return false;
    }

    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt(KEY_USER_ID, 0),
                sharedPreferences.getString(KEY_USER_NAME, null),
                sharedPreferences.getString(KEY_USER_EMAIL, null),
                sharedPreferences.getString(KEY_USER_PWD, null),
                sharedPreferences.getString(KEY_USER_ACCOUNT_TYPE, "UNVERIFIED"),
                sharedPreferences.getString(KEY_USER_MEMBERSHIP_TYPE, "LITE")
        );
    }

    public FbUser getFbUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new FbUser(
                sharedPreferences.getInt(KEY_USER_ID, 0),
                sharedPreferences.getString(KEY_USER_EMAIL, null),
                sharedPreferences.getString(KEY_USER_FbName, null),
                sharedPreferences.getString(KEY_USER_MEMBERSHIP_TYPE, "LITE")
        );
    }

    public boolean updatePassword(String password){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_PWD, password);
        editor.apply();
        return true;
    }

    public boolean logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }
}
