package com.example.visualpost_it.util;

import android.app.Application;

import com.example.visualpost_it.dtos.User;


public class UserClientSingleton extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
