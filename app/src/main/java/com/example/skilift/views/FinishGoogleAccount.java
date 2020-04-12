package com.example.skilift.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.skilift.R;
import com.example.skilift.interfaces.FirebaseResultListener;
import com.example.skilift.models.User;
import com.example.skilift.viewmodels.OnboardingVM;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;

public class FinishGoogleAccount extends AppCompatActivity {
    private static final String TAG = "FinishGoogleAccount";
    private static final String BUNDLE_PHONE = "Phone";

    private FirebaseAuth mAuth;

    private EditText phoneInput;
    private TextInputLayout phoneLayout;
    private Button finishAccountButton;
    private CountryCodePicker ccp;
    private OnboardingVM onboardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_google_account);
        onboardViewModel = ViewModelProviders.of(this).get(OnboardingVM.class);

        phoneInput = findViewById(R.id.phoneInput);
        phoneLayout = findViewById(R.id.phoneInputLayout);

        if(savedInstanceState != null) {
            phoneInput.setText(savedInstanceState.getString(BUNDLE_PHONE));
        }

        mAuth = FirebaseAuth.getInstance();

        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phoneInput);

        finishAccountButton = findViewById(R.id.finishAccountButton);

        finishAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUserInfo();
            }
        });
    }

    private boolean validPhone() {
        if(ccp.isValidFullNumber())
            return true;

        phoneLayout.setError("Phone number not valid.");
        requestFocus(phoneLayout);
        return false;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * Updates the UI depending on if a user is signed into the app or not through Firebase.
     *
     * @param user - the user signed in. This is null is no user is signed in.
     */
    public void updateUI(FirebaseUser user){
        if (user != null) {
            onboardViewModel.getUser(user, new FirebaseResultListener<User>() {
                @Override
                public void onComplete(User result) {
                    Toast.makeText(getApplicationContext(), "Welcome back, " + result.getFirstName() + "!",
                            Toast.LENGTH_SHORT).show();
                    openUserType();
                    finish();
                }
            });
        }
    }

    /**
     * Starts the userType activity, used in updateUI().
     */
    private void openUserType() {
        Intent intent = new Intent(this, UserType.class);
        startActivity(intent);
    }

    /**
     * Adds user info, called when auth is successful (meaning all verified so no need to re check.
     */
    private void addUserInfo() {
        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);

        if(!validPhone())
        {
            return;
        }

        String name = acc.getDisplayName();
        String phone = ccp.getFormattedFullNumber();
        String email = acc.getEmail();

        User newUser = new User(mAuth.getCurrentUser().getUid(), name, phone, email);
        onboardViewModel.saveUser(newUser);
        updateUI(mAuth.getCurrentUser());
    }
}
