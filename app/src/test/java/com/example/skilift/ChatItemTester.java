package com.example.skilift;

import com.example.skilift.models.ChatItem;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class ChatItemTester {
    private ChatItem chatItem;

    @Before
    public void initialize() {
        chatItem = new ChatItem("https://www.google.com", "Anderson", "Welcome!!!");
    }

    @Test
    public void getProfPicUri() { assertEquals("https://www.google.com", chatItem.getProfPicUri()); }

    @Test
    public void getName() { assertEquals("Anderson", chatItem.getName());}

    @Test
    public void getChatSnippet() { assertEquals("Welcome!!!", chatItem.getChatSnippet()); }

    @Test
    public void getProfPicUriFail() { assertNotEquals("https://www.yes.com", chatItem.getProfPicUri()); }

    @Test
    public void getNameFail() { assertNotEquals("546841654", chatItem.getName());}

    @Test
    public void getChatSnippetFail() { assertNotEquals("Bye!!!", chatItem.getChatSnippet()); }
}
