package com.example.visualpost_it.util;

import android.app.Application;
import android.util.Log;

import com.example.visualpost_it.dtos.User;


public class UserClientSingleton extends Application {
    private static final String TAG = "UserClientSingleton";
    private User user = null;

    public User getUser() {
        Log.d(TAG, "getUser: " + user.toString());
        return user;
    }

    public void setUser(User user) {

        this.user = user;
        Log.d(TAG, "setUser: " + this.user.toString());

    }

}
