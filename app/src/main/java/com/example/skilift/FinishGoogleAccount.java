package com.example.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_google_account);

        phoneInput = findViewById(R.id.phoneInput);
        phoneLayout = findViewById(R.id.phoneInputLayout);

        if(savedInstanceState != null) {
            phoneInput.setText(savedInstanceState.getString(BUNDLE_PHONE));
        }

        mAuth = FirebaseAuth.getInstance();

        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phoneInput);

        finishAccountButton = findViewById(R.id.finishAccountButton);
        finishAccountButton.setBackground(getDrawable(R.drawable.button_default));

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
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocRef = db.collection("users").document(user.getUid());
            userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            User user = new User(document.getData());
                            Toast.makeText(getApplicationContext(), "Welcome back, " + user.getFirstName() + "!",
                                    Toast.LENGTH_SHORT).show();
                            openUserType();
                            finish();
                        } else {
                            // check for google, send to finish if so.
                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                            Log.d(TAG, "No such document");

                            if(acct != null) {
                                Intent intent = new Intent(getApplicationContext(), FinishGoogleAccount.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);

        if(!validPhone())
        {
            return;
        }

        String name = acc.getDisplayName();
        String phone = ccp.getFormattedFullNumber();
        String email = acc.getEmail();

        // Save user info into a hashmap
        HashMap<String, Object> userInfoMap = new HashMap<>();
        userInfoMap.put("Name", name);
        userInfoMap.put("Phone", phone);
        userInfoMap.put("Email", email);

        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .set(userInfoMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateUI(mAuth.getCurrentUser());
                        Log.d(TAG, "addUserInfo: added user info successfully to firestore.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "addUserInfo: Error writing user document: ", e);
                    }
                });
    }
}
