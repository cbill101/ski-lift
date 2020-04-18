package com.example.skilift.models;

import com.google.firebase.Timestamp;

import java.sql.Time;

public class Message {
    private String uID;
    private String rawMessage;
    private Timestamp creationTime;

    public Message() {

    }

    public Message(String uID, String rawMessage, Timestamp creationTime) {
        this.uID = uID;
        this.rawMessage = rawMessage;
        this.creationTime = creationTime;
    }
}
