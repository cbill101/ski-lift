package com.example.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RideList extends AppCompatActivity {
    private Button confirmButton;
    private ListView listView;

    private RadioButton listRadioButton = null;
    int listIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_list);

        confirmButton = findViewById(R.id.confirmButton);
        listView = findViewById(R.id.listView);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openPayment();
            }
        });

        // Array list to save from db
        final ArrayList<String> list = new ArrayList<>();
        final ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_item, list);
        listView.setAdapter(adapter);

        CollectionReference dRef = FirebaseFirestore.getInstance().collection("Providers");

        dRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    list.clear();
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Information info = new Information(document.getData());
                        String infoRow = "Name: " + info.getName() +
                                "\nPhone: " + info.getPhone() +
                                "\nPrice: " + info.getPrice() +
                                "\nDestination: " + info.getPlaceName();
                        list.add(infoRow);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void onClickRadioButton(View v) {
        View vMain = ((View) v.getParent());

        // uncheck previous checked button.
        if (listRadioButton != null) listRadioButton.setChecked(false);
        // assign to the variable the new one
        listRadioButton = (RadioButton) v;
        // find if the new one is checked or not, and set "listIndex"
        if (listRadioButton.isChecked()) {
            listIndex = ((ViewGroup) vMain.getParent()).indexOfChild(vMain);
        } else {
            listRadioButton = null;
            listIndex = -1;
        }
    }

    //For now goes to next page
    private void openPayment() {
        Intent intent = new Intent(this, Payment.class);
        startActivity(intent);
    }
}
