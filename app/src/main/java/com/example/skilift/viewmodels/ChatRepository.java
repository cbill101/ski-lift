package com.example.skilift.viewmodels;

import androidx.annotation.NonNull;

import com.example.skilift.models.ChatMetadata;
import com.example.skilift.models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatRepository {
    private final static String TAG = "RTDB_REPO";
    private DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public Task<Void> sendMessage(String cID, Message msg) {
       return db_ref.child("messages").child(cID).setValue(msg);
    }

    public Task<Void> startChat(ChatMetadata cm) {
        String key = db_ref.child("chats").push().getKey();
        return null;
    }
}
