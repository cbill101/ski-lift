package com.example.skilift.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.skilift.interfaces.FirebaseResultListener;
import com.example.skilift.models.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class InformationVM extends ViewModel {
    private static final String TAG = "InformationVM";
    private FSRepository repository = new FSRepository();
    private FBQueryLiveData userQuery;
    private LiveData<User> currentUser;

    /**
     * Tries to save provider info to DB.
     * @param prov - the provider object.
     */
    public void saveProvider(Provider prov) {
        repository.saveProviderInfo(prov).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.w(TAG, "Saved provider successfully to DB in VM.");
                }
                else {
                    Log.w(TAG, "ERROR: Saving provider not successful: ", task.getException());
                }
            }
        });
    }

    /**
     * Tries to save ride request info to DB.
     * @param rr - the ride request object
     */
    public void saveRequest(RideRequest rr) {
        repository.saveRequestInfo(rr).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.w(TAG, "Saved ride request successfully to DB in VM.");
                }
                else {
                    Log.w(TAG, "ERROR: Saving ride request not successful: ", task.getException());
                }
            }
        });
    }

    public void getCurrentUser(FirebaseResultListener<User> callback) {
        repository.getCurrentUser().addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
