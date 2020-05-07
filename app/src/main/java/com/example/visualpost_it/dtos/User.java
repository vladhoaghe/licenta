package com.example.visualpost_it.dtos;

import java.util.Date;

public class User {

    public User(String nickname, String email, String password, String fullName) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    private String nickname;
    private String email;
    private String password;
    private String fullName;


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    private Gender gender;


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

}
