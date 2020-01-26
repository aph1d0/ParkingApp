package com.android.parkingapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Created by Abhi on 20 Jan 2018 020.
 */

public class SessionHandler {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EXPIRES = "expires";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMPTY = "";
    private Context mContext;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences;

    public SessionHandler(Context mContext) {
        this.mContext = mContext;
        mPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.mEditor = mPreferences.edit();
    }

    /**
     *
     * Loguje użytkownika zapisując jego dane i ustanawiając sesje
     *
     * @param username
     * @param fullName
     */
    public void loginUser(String username, String fullName) {
        mEditor.putString(KEY_USERNAME, username);
        mEditor.putString(KEY_FULL_NAME, fullName);
        Date date = new Date();

        //Set user session for next 7 days
        long millis = date.getTime() + (7 * 24 * 60 * 60 * 1000);
        mEditor.putLong(KEY_EXPIRES, millis);
        mEditor.commit();
    }

    /**
     *
     * Sprawdź czy użytkownik jest zalogowany
     *
     * @return
     */
    public boolean isLoggedIn() {
        Date currentDate = new Date();

        long millis = mPreferences.getLong(KEY_EXPIRES, 0);

        /* If shared preferences does not have a value
         then user is not logged in
         */
        if (millis == 0) {
            return false;
        }
        Date expiryDate = new Date(millis);

        /* Check if session is expired by comparing
        current date and Session expiry date
        */
        return currentDate.before(expiryDate);
    }

    /**
     * Pobiera i zwraca dane użytkownika
     *
     * @return user details
     */
    public User getUserDetails() {
        //Check if user is logged in first
        if (!isLoggedIn()) {
            return null;
        }
        User user = new User();
        user.setUsername(mPreferences.getString(KEY_USERNAME, KEY_EMPTY));
        user.setFullName(mPreferences.getString(KEY_FULL_NAME, KEY_EMPTY));
        user.setSessionExpiryDate(new Date(mPreferences.getLong(KEY_EXPIRES, 0)));

        return user;
    }

    /**
     * Wylogowuje uzytkownika z końcem sesji
     */
    public void logoutUser(){
        mEditor.clear();
        mEditor.commit();
    }

}
