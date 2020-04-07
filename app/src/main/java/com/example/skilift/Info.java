package com.example.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.NumberFormat;
import android.icu.util.Calendar;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cottacush.android.currencyedittext.CurrencyInputWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Info extends AppCompatActivity {
    private static final String BUNDLE_PRICE = "price";
    private static final String BUNDLE_ARRIVAL_TIME = "arrival";
    private static final String BUNDLE_DEPART_TIME = "depart";
    private static final String TAG = "Info";
    private Button nextPageButton;
    private Button sendButton;
    private EditText priceInput;
    private TextView arrivalTimeText;
    private TextView departTimeText;
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
        priceInput = findViewById(R.id.priceInput);
        arrivalTimeText = findViewById(R.id.arrivalTimeText);
        departTimeText = findViewById(R.id.departTimeText);
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
        priceInput.addTextChangedListener(new CurrencyInputWatcher(priceInput, "$", Locale.getDefault()));
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
        outState.putString(BUNDLE_PRICE, priceInput.getText().toString());
        outState.putString(BUNDLE_ARRIVAL_TIME, arrivalTimeText.getText().toString());
        outState.putString(BUNDLE_DEPART_TIME, departTimeText.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        priceInput.setText(savedInstanceState.getString(BUNDLE_PRICE));
        arrivalTimeText.setText(savedInstanceState.getString(BUNDLE_ARRIVAL_TIME));
        departTimeText.setText(savedInstanceState.getString(BUNDLE_DEPART_TIME));
    }

    public void showArrivalTimePicker(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "arrivalTimePicker");
    }

    public void showDepartTimePicker(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "departTimePicker");
    }

    public static class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Log.d("TPDialog", "Tag is " + getTag());

            String am_pm = "";

            Calendar datetime = Calendar.getInstance();
            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            datetime.set(Calendar.MINUTE, minute);

            if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                am_pm = "AM";
            else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                am_pm = "PM";

            String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ?"12":datetime.get(Calendar.HOUR)+"";

            switch(getTag()) {
                case "arrivalTimePicker":
                    TextView arrivalTextView = getActivity().findViewById(R.id.arrivalTimeText);
                    arrivalTextView.setText(DateFormat.getTimeFormat(getActivity()).format(datetime.getTime()));
                    break;
                case "departTimePicker":
                    TextView departTextView = getActivity().findViewById(R.id.departTimeText);
                    departTextView.setText(DateFormat.getTimeFormat(getActivity()).format(datetime.getTime()));
                    break;
            }
        }
    }

}
