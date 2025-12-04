package com.example.da1.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private static final String PREFS_NAME = "DA1_PREFS";
    private static final String KEY_USER_TOKEN = "user_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences prefs;

    public SharedPreferencesHelper(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveUserToken(String token) {
        prefs.edit().putString(KEY_USER_TOKEN, token).apply();
    }

    public String getUserToken() {
        return prefs.getString(KEY_USER_TOKEN, null);
    }

    public void saveUserId(String userId) {
        prefs.edit().putString(KEY_USER_ID, userId).apply();
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public void saveUserRole(String role) {
        prefs.edit().putString(KEY_USER_ROLE, role).apply();
    }

    public String getUserRole() {
        return prefs.getString(KEY_USER_ROLE, null);
    }

    public void setLoggedIn(boolean isLoggedIn) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }
    
    public void clearToken() {
        prefs.edit().remove(KEY_USER_TOKEN).apply();
    }
    
    // Static method để clear token
    public static void clearToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_USER_TOKEN).apply();
    }
}


