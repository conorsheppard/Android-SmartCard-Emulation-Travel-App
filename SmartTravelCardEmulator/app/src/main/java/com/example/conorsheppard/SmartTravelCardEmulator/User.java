package com.example.conorsheppard.SmartTravelCardEmulator;

public class User {
    String email, password, uuid;
    boolean accountActive;

    public User(String email, String password, String uuid, boolean accountActive) {
        this.email = email;
        this.password = password;
        this.uuid = uuid;
        this.accountActive = accountActive;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String email, String password, String cardId) {
        this.email = email;
        this.password = password;
        this.uuid = cardId;
    }

    public String GetUuid() { return this.uuid; }
}