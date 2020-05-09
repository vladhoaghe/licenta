package com.example.visualpost_it.dtos;

import java.util.Date;

public class User {

    public User(String userId, String nickname, String email, String password, String fullName) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    public User(String nickname, String email, String password, String fullName) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }
    public User(){

    }

    private String userId;
    private String nickname;
    private String email;
    private String password;
    private String fullName;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
