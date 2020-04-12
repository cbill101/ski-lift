package com.example.skilift.models;

import android.net.Uri;

public class ChatItem {
    private String ProfPicUri;
    private String Name;
    private String ChatSnippet;

    public ChatItem() {

    }

    public ChatItem(String profPicUri, String name, String chatSnippet) {
        ProfPicUri = profPicUri;
        Name = name;
        ChatSnippet = chatSnippet;
    }

    public String getProfPicUri() {
        return ProfPicUri;
    }

    public void setProfPicUri(String profPicUri) {
        ProfPicUri = profPicUri;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getChatSnippet() {
        return ChatSnippet;
    }

    public void setChatSnippet(String chatSnippet) {
        ChatSnippet = chatSnippet;
    }
}
