package com.example.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Info extends AppCompatActivity {
    private static final String BUNDLE_NAME = "";
    private static final String BUNDLE_PHONE = "";
    private static final String BUNDLE_PRICE = "";
    private static final String TAG = "Info";
    private Button nextPageButton;
    private Button sendButton;
    private EditText nameInput;
    private EditText phoneInput;
    private EditText priceInput;
    private TimePicker startTimeInput;
    private TimePicker endTimeInput;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Location pickupLocation;
    private boolean isProvider;


    private double placeLatitude;
    private double placeLongitude;
    private String placeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        db = FirebaseFirestore.getInstance();

        nextPageButton = findViewById(R.id.finishedButton);
        sendButton = findViewById(R.id.sendButton);
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        priceInput = findViewById(R.id.priceInput);
        startTimeInput = findViewById(R.id.startTimePicker);
        endTimeInput = findViewById(R.id.endTimePicker);
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWait();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertInfo();
            }
        });

        Intent intent = getIntent();

        placeLatitude = intent.getDoubleExtra("DestLatitude", 0);
        placeLongitude = intent.getDoubleExtra("DestLongitude", 0);
        placeName = intent.getStringExtra("PlaceName");
        isProvider = intent.getBooleanExtra("Provider", false);
        // could be null.
        pickupLocation = intent.getParcelableExtra("PickupLocation");

        mAuth = FirebaseAuth.getInstance();
    }

    //For now goes to next page
    private void openWait() {
        Intent intent = new Intent(this, Wait.class);
        startActivity(intent);
    }

    //Anderson put the stuff you need to have happen when the button is pressed here
    private void insertInfo() {
        DocumentReference dRec = db.collection("users").document(mAuth.getCurrentUser().getUid());

        if(priceInput.getText().toString().isEmpty()){
            Toast.makeText(Info.this, "Please enter info in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        dRec.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot data = task.getResult();
                    if(data.exists()) {
                        User user = new User(data.getData());
                        // Get text from fields
                        String price = priceInput.getText().toString();

                        // Save provider info into a hashmap
                        HashMap<String, Object> provInfoMap = new HashMap<>();

                        provInfoMap.put("name", user.getName());
                        provInfoMap.put("phone", user.getPhone());
                        provInfoMap.put("dest_latitude", placeLatitude);
                        provInfoMap.put("dest_longitude", placeLongitude);

                        if(pickupLocation != null)
                        {
                            provInfoMap.put("pickup_latitude", pickupLocation.getLatitude());
                            provInfoMap.put("pickup_longitude", pickupLocation.getLongitude());
                        }

                        provInfoMap.put("place_name", placeName);
                        provInfoMap.put("price", price);

                        finishInsertRide(provInfoMap);
                    }
                }
                else {

                }
            }
        });
    }

    private void finishInsertRide(Map<String, Object> provInfoMap) {
        // Save to db
        if(isProvider)
            db.collection("Providers")
                .document(mAuth.getCurrentUser().getUid())
                .set(provInfoMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "insertRide: added ride info successfully to firestore.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "insertRide: Error writing user document: ", e);
                    }
                });
        else
            db.collection("Requests")
                    .document(mAuth.getCurrentUser().getUid())
                    .set(provInfoMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "insertRide: added request info successfully to firestore.");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "insertRide: Error writing user document: ", e);
                        }
                    });

        Toast.makeText(Info.this, "Info saved!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_NAME, nameInput.getText().toString());
        outState.putString(BUNDLE_PHONE, phoneInput.getText().toString());
        outState.putString(BUNDLE_PRICE, priceInput.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        nameInput.setText(savedInstanceState.getString(BUNDLE_NAME));
        phoneInput.setText(savedInstanceState.getString(BUNDLE_PHONE));
        priceInput.setText(savedInstanceState.getString(BUNDLE_PRICE));
    }
}
