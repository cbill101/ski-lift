package com.example.skilift.models;

import com.google.firebase.Timestamp;

public class ChatMetadata {
    private String lastMessage;
    private Timestamp creationTime;

    public ChatMetadata() {
    }

    public ChatMetadata(String lastMessage, Timestamp creationTime) {
        this.lastMessage = lastMessage;
        this.creationTime = creationTime;
    }
}
