package com.example.skilift.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.skilift.R;
import com.example.skilift.fragments.ProviderFragment;
import com.example.skilift.interfaces.OnResultClickListener;
import com.example.skilift.models.Provider;
import com.example.skilift.models.RideRequest;
import com.google.firebase.auth.FirebaseAuth;

public class RideListActivity extends AppCompatActivity implements OnResultClickListener {
    private static final String STATE_LIST = "Results Adapter Data";
    private Button confirmButton;
    private FirebaseAuth mAuth;
    private TextView descText;
    private ProviderFragment listFragment;
    private Provider selectedProvider;
    private RideRequest selectedRequest;
    private boolean isProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_list);
        confirmButton = findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(v -> {
            if(selectedRequest != null) {
                selectedRequest.setChecked(false);
                openWaitScreen();
            }
            else if (selectedProvider != null){
                selectedProvider.setChecked(false);
                openPayment();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        isProvider = intent.getBooleanExtra("Provider", false);

        if (savedInstanceState != null) {
            listFragment = (ProviderFragment) getSupportFragmentManager().findFragmentByTag("RESULTS_FRAG");
            isProvider = savedInstanceState.getBoolean("isProvider");
            selectedProvider = savedInstanceState.getParcelable("selectedProv");
            selectedRequest = savedInstanceState.getParcelable("selectedReq");

            if (selectedRequest != null || selectedProvider != null) {
                confirmButton.setVisibility(View.VISIBLE);
            }
            else {
                confirmButton.setVisibility(Button.INVISIBLE);
            }
        }
        else {
            listFragment = new ProviderFragment(isProvider);
            listFragment.setOnResultClickListener(this);

            getSupportFragmentManager().beginTransaction().add(R.id.listFragLayout, listFragment, "RESULTS_FRAG").commit();

            confirmButton.setVisibility(Button.INVISIBLE);
        }

        descText = findViewById(R.id.textView);

        if(isProvider) {
            descText.setText(R.string.list_of_requests);
            confirmButton.setText(R.string.confirm);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //For now goes to next page
    private void openWaitScreen() {
        Intent intent = new Intent(this, Wait.class);
        intent.putExtra("requestUID", selectedRequest.getUID());
        startActivity(intent);
    }

    //For now goes to next page
    private void openPayment() {
        Intent intent = new Intent(this, Payment.class);
        intent.putExtra("providerUID", selectedProvider.getUID());
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isProvider", isProvider);

        if(selectedRequest != null) {
            outState.putParcelable("selectedReq", selectedRequest);
        }
        else if (selectedProvider != null) {
            outState.putParcelable("selectedProv", selectedProvider);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    public void onItemClick(Parcelable result, int position) {
        if(confirmButton.getVisibility() == View.INVISIBLE)
            confirmButton.setVisibility(View.VISIBLE);

        if(result instanceof Provider)
            selectedProvider = (Provider) result;
        else if (result instanceof RideRequest)
            selectedRequest = (RideRequest) result;
    }
}
