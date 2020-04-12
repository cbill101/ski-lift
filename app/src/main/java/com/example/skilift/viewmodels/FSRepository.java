package com.example.skilift.viewmodels;

import com.example.skilift.models.*;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FSRepository {
    private final static String TAG = "FB_REPO";
    private FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    /**
     * Builds a query to insert provider info to our firestore DB.
     * @param provider - the Provider object to add
     * @return a Task object that will attempt to insert the document.
     */
    public Task<Void> saveProviderInfo(Provider provider) {
        DocumentReference providerDocRef = firestoreDB.collection("Providers").document(currentUser.getUid());
        return providerDocRef.set(provider);
    }

    /**
     * Builds a query to insert ride request information to our firestore DB.
     * @param rr - the RideRequest object to add
     * @return a Task object that will attempt to insert the document.
     */
    public Task<Void> saveRequestInfo(RideRequest rr) {
        DocumentReference userRequestDocRef = firestoreDB.collection("Requests").document(currentUser.getUid());
        return userRequestDocRef.set(rr);
    }

    /**
     * Builds a query to insert user information to our firestore DB.
     * @param user - the User object to add
     * @return a Task object that will attempt to insert the document.
     */
    public Task<Void> saveUserInfo(User user) {
        DocumentReference userDocRef = firestoreDB.collection("users").document(user.getuID());
        return userDocRef.set(user);
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
     * @return a DocumentReference from firestore pointing to the current users' User object.
     */
    public DocumentReference getCurrentUser() {
        DocumentReference userRef = firestoreDB.collection("users").document(currentUser.getUid());
        return userRef;
    }
}
