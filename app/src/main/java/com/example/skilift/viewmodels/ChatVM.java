package com.example.skilift.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.skilift.models.ChatItem;
import com.example.skilift.models.User;

import java.util.ArrayList;

public class ChatVM extends ViewModel {
    private static final String TAG = "ChatVM";
    private FSRepository repository = new FSRepository();

    public LiveData<ArrayList<ChatItem>> getCurrentChatsForUser(User user) {
        return repository.getChatHistoryForUser(user);
    }

    public LiveData<ArrayList<ChatItem>> getCurrentChatsForCurrentUser() {
        return repository.getChatHistoryForCurrentUser();
    }
}
