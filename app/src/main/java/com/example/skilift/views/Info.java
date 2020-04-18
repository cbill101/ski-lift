package com.example.skilift.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.location.Location;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cottacush.android.currencyedittext.CurrencyInputWatcher;
import com.example.skilift.R;
import com.example.skilift.interfaces.FirebaseResultListener;
import com.example.skilift.models.Provider;
import com.example.skilift.models.RideRequest;
import com.example.skilift.models.User;
import com.example.skilift.viewmodels.InformationVM;

import java.util.Locale;

public class Info extends AppCompatActivity implements FirebaseResultListener {
    private static final String BUNDLE_PRICE = "price";
    private static final String BUNDLE_ARRIVAL_TIME = "arrival";
    private static final String BUNDLE_DEPART_TIME = "depart";
    private static final String TAG = "Info";
    private Button nextPageButton;
    private Button sendButton;
    private EditText priceInput;
    private TextView arrivalTimeText;
    private TextView departTimeText;
    private Location pickupLocation;
    private boolean isProvider;
    private InformationVM infoViewModel;
    private User currentUser;

    private double placeLatitude;
    private double placeLongitude;
    private String placeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        infoViewModel = ViewModelProviders.of(this).get(InformationVM.class);

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
    }

    //For now goes to next page
    private void openWait() {
        Intent intent = new Intent(this, Wait.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_context_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.account:
                startActivity(new Intent(getApplicationContext(), AccountPage.class));
                return true;
            case R.id.settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.chats:
                startActivity(new Intent(getApplicationContext(), ChatHistoryActivity.class));
                return true;
            case R.id.help:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Anderson put the stuff you need to have happen when the button is pressed here
    private void insertInfo() {

        if(priceInput.getText().toString().isEmpty()){
            Toast.makeText(Info.this, "Please enter info in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        infoViewModel.getCurrentUser(this);

        if (currentUser != null) {
            if(pickupLocation == null) {
                Provider prov = new Provider();
                prov.setName(currentUser.getName());
                prov.setPhone(currentUser.getPhone());
                prov.setDest_latitude(placeLatitude);
                prov.setDest_longitude(placeLongitude);
                prov.setPlace_name(placeName);
                prov.setPrice(priceInput.getText().toString());
                infoViewModel.saveProvider(prov);
            }
            else {
                RideRequest rr = new RideRequest();
                rr.setName(currentUser.getName());
                rr.setPhone(currentUser.getPhone());
                rr.setDestLatitude(placeLatitude);
                rr.setDestLongitude(placeLongitude);
                rr.setPickupLatitude(pickupLocation.getLatitude());
                rr.setPickupLongitude(pickupLocation.getLongitude());
                rr.setDestName(placeName);
                rr.setPrice(priceInput.getText().toString());
                infoViewModel.saveRequest(rr);
            }

            Toast.makeText(Info.this, "Info saved!", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onComplete(Object result) {
        if(result instanceof User) {
            currentUser = (User) result;
        }
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
