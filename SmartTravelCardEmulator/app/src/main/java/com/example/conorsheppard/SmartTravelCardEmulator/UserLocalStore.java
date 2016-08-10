package com.example.conorsheppard.SmartTravelCardEmulator;

import android.content.Context;
import android.content.SharedPreferences;

public class UserLocalStore {
    // a shared preference allows us to store data on the phone
    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;

    // create constructor, the first thing that runs when a user local store is created
    public UserLocalStore(Context context) {
        // Parameters: name of the file where shared preference data comes from and 0, default value
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    // methods to get user data from local database, stores attributes of logged in user
    public void storeUserData(User user) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("email", user.email);
        spEditor.putString("password", user.password);
        spEditor.putString("uuid", user.uuid);
        spEditor.commit();
    }

    // returns attributes of logged in user
    public User getLoggedInUser() {
        String email = userLocalDatabase.getString("email", "");
        String password = userLocalDatabase.getString("password", "");
        String uuid = userLocalDatabase.getString("uuid", "");
        boolean accountActive = userLocalDatabase.getBoolean("accountAcount", false);
        User storedUser = new User(email, password, uuid, accountActive);
        return storedUser;
    }

    // if a user's logged in, this returns true, else false
    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("LoggedIn", loggedIn);
        spEditor.commit();
    }

    // checks the local store database and indicates whether a user has been logged in
    public boolean isUserLoggedIn() {
        boolean isLoggedIn;
        if (userLocalDatabase.getBoolean("LoggedIn", false) == true) {
            isLoggedIn = true;
        } else {
            isLoggedIn = false;
        }
        return isLoggedIn;
    }

    public boolean isAccountActive() {
        boolean isAccountActive;
        if (userLocalDatabase.getBoolean("LoggedIn", false) == true) {
            isAccountActive = true;
        } else {
            isAccountActive = false;
        }
        return isAccountActive;
    }

    // clear current user's data from the shared preferences
    public void clearUserData() {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("LoggedIn", false);
        spEditor.putString("email", null);
        spEditor.putString("password", null);
        spEditor.putString("uuid", null);
        spEditor.commit();
    }
}