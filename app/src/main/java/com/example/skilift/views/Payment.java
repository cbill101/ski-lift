package com.example.skilift.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.example.skilift.R;
import com.example.skilift.views.Communicate;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

//Evan do your stuff here
public class Payment extends AppCompatActivity {
    private Button nextPageButton;
    private CardInputWidget cardInfo;
    private TextView priceTextView;
    private String providerUID;
    private FirebaseFunctions mFunctions;
    private String paymentIntentClientSecret;
    private Stripe stripe;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mFunctions =FirebaseFunctions.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        String price = intent.getStringExtra("amountCharged");
        providerUID = intent.getStringExtra("providerUID");
        cardInfo = findViewById(R.id.cardInputWidget);
        priceTextView = findViewById(R.id.priceTextView);
        priceTextView.append("Price: $"+price);
        nextPageButton = findViewById(R.id.finishedButton);

        Button payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener((View view) -> {
            PaymentMethodCreateParams params = cardInfo.getPaymentMethodCreateParams();
            if (params != null) {
//                Map<String, String> extraParams = new HashMap<>();
//                extraParams.put("setup_future_usage", "off_session");
//
//                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
//                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret, null, false, extraParams);
//                stripe = new Stripe(getApplicationContext(), PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey());
//                stripe.confirmPayment(this, confirmParams);
                openCommunicate();
            }
        });

        //startCheckout((int)Double.parseDouble(price)*100);
    }

    private void startCheckout(int inputPrice){
        createPaymentIntent(inputPrice)
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }

                            // ...
                        }
                        Log.w("PAYMENT", task.getResult());
                        paymentIntentClientSecret = task.getResult();
                    }
                });
    }

    private Task<String> createPaymentIntent(int amountCharged) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("amount", amountCharged);
        data.put("uid", user.getUid());
        data.put("push", true);

        return mFunctions
                .getHttpsCallable("createPaymentIntent")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

    //For now goes to next page
    private void openCommunicate() {
        Intent intent = new Intent(this, Communicate.class);
        startActivity(intent);
    }
}
