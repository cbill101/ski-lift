package com.example.skilift.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.skilift.models.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;

public class FSRepository {
    private final static String TAG = "FB_REPO";
    private FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /**
     * Builds a query to insert provider info to our firestore DB.
     * @param provider - the Provider object to add
     * @return a Task object that will attempt to insert the document.
     */
    public Task<Void> saveProviderInfo(Provider provider) {
        DocumentReference providerDocRef = getProviders().document(mAuth.getCurrentUser().getUid());
        return providerDocRef.set(provider);
    }

    /**
     * Builds a query to insert ride request information to our firestore DB.
     * @param rr - the RideRequest object to add
     * @return a Task object that will attempt to insert the document.
     */
    public Task<Void> saveRequestInfo(RideRequest rr) {
        DocumentReference userRequestDocRef = getRequests().document(mAuth.getCurrentUser().getUid());
        return userRequestDocRef.set(rr);
    }

    /**
     * Builds a query to insert user information to our firestore DB.
     * @param user - the User object to add
     * @return a Task object that will attempt to insert the document.
     */
    public Task<Void> saveUserInfo(User user) {
        return getUsers().document(user.getuID()).set(user);
    }

    /**
     * Gets a reference for all the Providers in the DB.
     * @return a CollectionReference from firestore pointing to the Provider objects in DB.
     */
    public CollectionReference getProviders() {
        CollectionReference providersRef = firestoreDB.collection("Providers");
        return providersRef;
    }

    /**
     * Gets a reference for all the Users in the DB.
     * @return a CollectionReference from firestore pointing to the User objects in DB.
     */
    public CollectionReference getUsers() {
        CollectionReference usersRef = firestoreDB.collection("users");
        return usersRef;
    }

    /**
     * Gets a reference for all the RideRequests in the DB.
     * @return a CollectionReference from firestore pointing to the RideRequest objects in DB.
     */
    public CollectionReference getRequests() {
        CollectionReference requestsRef = firestoreDB.collection("Requests");
        return requestsRef;
    }

    /**
     * Gets a reference for the current user (aka... the person logged in) in the DB.
     * Now... this is assuming you're using this after login, yes? :)
     * @return a DocumentReference from firestore pointing to the current users' User object.
     */
    public DocumentReference getCurrentUser() {
        DocumentReference userRef = firestoreDB.collection("users").document(mAuth.getCurrentUser().getUid());
        return userRef;
    }

    /**
     * Gets a reference for a user in the DB.
     * If you have to get a user, use this 9/10. :D
     * @return a DocumentReference from firestore pointing to the current users' User object.
     */
    public DocumentReference getUser(User user) {
        DocumentReference userRef = firestoreDB.collection("users").document(user.getuID());
        return userRef;
    }

    // ------------------------------ CHAT -----------------------------------------
    public LiveData<ArrayList<ChatItem>> getChatHistoryForUser(User user) {
        MutableLiveData<ArrayList<ChatItem>> chatItems = new MutableLiveData<>();

        CollectionReference historyRef = getUser(user).collection("ChatHistory");

        historyRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Failed to get chat history items from FS: ", e);
                    return;
                }

                ArrayList<ChatItem> chatListItems = new ArrayList<>();

                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    ChatItem ci = doc.toObject(ChatItem.class);
                    chatListItems.add(ci);
                }

                chatItems.setValue(chatListItems);
            }
        });

        return chatItems;
    }

    public LiveData<ArrayList<ChatItem>> getChatHistoryForCurrentUser() {
        MutableLiveData<ArrayList<ChatItem>> chatItems = new MutableLiveData<>();

        CollectionReference historyRef = getCurrentUser().collection("ChatHistory");

        historyRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Failed to get chat history items from FS: ", e);
                    return;
                }

                ArrayList<ChatItem> chatListItems = new ArrayList<>();

                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    ChatItem ci = doc.toObject(ChatItem.class);
                    chatListItems.add(ci);
                }

                chatItems.setValue(chatListItems);
            }
        });

        return chatItems;
    }

    // ------------------------------ AUTH -----------------------------------------

    public LiveData<FirebaseUser> attemptLogin(String email, String pass) {
        MutableLiveData<FirebaseUser> loginData = new MutableLiveData<>();

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            loginData.setValue(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            loginData.setValue(null);
                            return;
                        }
                    }
                });

        return loginData;
    }

    public LiveData<FirebaseUser> attemptLoginGoogle(AuthCredential credential) {
        MutableLiveData<FirebaseUser> loginData = new MutableLiveData<>();

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            loginData.setValue(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            loginData.setValue(null);
                        }

                        // ...
                    }
                });

        return loginData;
    }

    public LiveData<FirebaseUser> attemptAccountCreation(String email, String pass) {
        MutableLiveData<FirebaseUser> creationData = new MutableLiveData<>();

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Account creation successful.");
                    creationData.setValue(mAuth.getCurrentUser());
                }
                else {
                    Log.d(TAG, "Account creation failed! : ", task.getException());
                    creationData.setValue(null);
                }
            }
        });

        return creationData;
    }

}
