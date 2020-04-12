package com.example.skilift.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.skilift.interfaces.FirebaseResultListener;
import com.example.skilift.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class OnboardingVM extends ViewModel {
    private FSRepository repo = new FSRepository();
    private static final String TAG = "OnboardingVM";

    public LiveData<FirebaseUser> loginNormal(String email, String pass) {
        return repo.attemptLogin(email, pass);
    }

    public LiveData<FirebaseUser> loginGoogle(AuthCredential googleCred) {
        return repo.attemptLoginGoogle(googleCred);
    }

    public LiveData<FirebaseUser> createAccount(String email, String pass) {
        return repo.attemptAccountCreation(email, pass);
    }

    public void getCurrentUser(FirebaseResultListener<User> callback) {
        repo.getCurrentUser().addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Failed to get providers from FS: ", e);
                    return;
                }

                if(documentSnapshot != null) {
                    User user = documentSnapshot.toObject(User.class);
                    callback.onComplete(user);
                }
            }
        });
    }

    public void saveUser(User user) {
        repo.saveUserInfo(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.w(TAG, "Saved user successfully to DB in VM.");
                }
                else {
                    Log.w(TAG, "ERROR: Saving user not successful: ", task.getException());
                }
            }
        });
    }

    public void getUser(FirebaseUser user, FirebaseResultListener<User> callback) {
        repo.getCurrentUser().addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Failed to get providers from FS: ", e);
                    return;
                }

                if(documentSnapshot != null) {
                    User user = documentSnapshot.toObject(User.class);
                    callback.onComplete(user);
                }
            }
        });
    }
}
