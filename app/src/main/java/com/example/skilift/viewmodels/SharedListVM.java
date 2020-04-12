package com.example.skilift.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.skilift.models.Provider;
import com.example.skilift.models.RideRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SharedListVM extends ViewModel {
    private static final String TAG = "ProviderVM";
    private MutableLiveData<ArrayList<Provider>> providerList;
    private MutableLiveData<ArrayList<RideRequest>> requestsList;
    private FSRepository repository = new FSRepository();

    public LiveData<ArrayList<Provider>> getProviderList() {
        if (providerList == null ) {
            providerList = new MutableLiveData<>();

            repository.getProviders().addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Failed to get providers from FS: ", e);
                        providerList.setValue(null);
                        return;
                    }

                    ArrayList<Provider> savedProviders = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Provider prov = doc.toObject(Provider.class);
                        savedProviders.add(prov);
                    }

                    providerList.setValue(savedProviders);
                }
            });
        }

        return providerList;
    }

    public LiveData<ArrayList<RideRequest>> getRequestList() {
        if (requestsList == null ) {
            requestsList = new MutableLiveData<>();

            repository.getRequests().addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Failed to get providers from FS: ", e);
                        requestsList.setValue(null);
                        return;
                    }

                    ArrayList<RideRequest> savedRequests = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        RideRequest rr = doc.toObject(RideRequest.class);
                        savedRequests.add(rr);
                    }

                    requestsList.setValue(savedRequests);
                }
            });
        }

        return requestsList;
    }
}
