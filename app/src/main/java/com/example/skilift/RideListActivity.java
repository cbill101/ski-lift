package com.example.skilift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RideListActivity extends AppCompatActivity {
    private Button confirmButton;
    private ListView listView;
    private EditText searchQueryText;
    private FirebaseAuth mAuth;
    private ArrayList<Provider> rideList;
    private ArrayList<RideRequest> requestList;
    private ArrayAdapter rideAdapter;
    private boolean isProvider;

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
            public void onClick(View v) {
                if(listRadioButton != null) {
                    if(isProvider)
                        openWaitScreen();
                    else
                        openPayment();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Must select a ride.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        searchQueryText = findViewById(R.id.filterTextInput);
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        isProvider = intent.getBooleanExtra("Provider", false);

        TextView descText = findViewById(R.id.textView);

        if(isProvider) {
            descText.setText(R.string.list_of_requests);
            confirmButton.setText(R.string.confirm);
        }

        // Array list to save from db
        rideList = new ArrayList<>();
        requestList = new ArrayList<>();

        getContextualList();
    }

    private void getContextualList() {
        CollectionReference dRef;

        if(isProvider) {
            dRef = FirebaseFirestore.getInstance().collection("Requests");
            rideAdapter = new ResultAdapter<>(this, R.layout.list_item, requestList);
            listView.setAdapter(rideAdapter);

            dRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        requestList.clear();
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            if(document.exists())
                            {
                                RideRequest info = new RideRequest(document.getData());
                                info.setUID(document.getId());
                                requestList.add(info);
                            }
                        }
                        rideAdapter.notifyDataSetChanged();
                    }
                }
            });

            searchQueryText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        else {
            dRef = FirebaseFirestore.getInstance().collection("Providers");

            rideAdapter = new ResultAdapter<>(this, R.layout.list_item, rideList);
            listView.setAdapter(rideAdapter);

            dRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        rideList.clear();
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            if(document.exists())
                            {
                                Provider info = new Provider(document.getData());
                                info.setUID(document.getId());
                                rideList.add(info);
                            }
                        }
                        rideAdapter.notifyDataSetChanged();
                    }
                }
            });

            searchQueryText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String search = s.toString();
                    rideAdapter.getFilter().filter(search);
                }
            });
        }

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
    private void openWaitScreen() {
        Intent intent = new Intent(this, Wait.class);
        intent.putExtra("requestUID", requestList.get(listIndex).getUID());
        startActivity(intent);
    }

    //For now goes to next page
    private void openPayment() {
        Intent intent = new Intent(this, Payment.class);
        intent.putExtra("providerUID", rideList.get(listIndex).getUID());
        startActivity(intent);
    }

    private class ResultAdapter<T> extends ArrayAdapter<T> implements Filterable {

        private List<T> listItems;
        private List<T> filteredItems;
        private Filter providerFilter;

        public ResultAdapter(@NonNull Context context, int resource, @NonNull List<T> objects) {
            super(context, resource, objects);
            listItems = objects;
            filteredItems = objects;
        }

        public int getCount() {
            return filteredItems.size();
        }

        public T getItem(int position) {
            return filteredItems.get(position);
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            listRadioButton = null;
        }

        @NonNull
        @Override
        public Filter getFilter() {

            return new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    String constr = constraint.toString().toLowerCase().trim();
                    ArrayList<Object> filterProviders = new ArrayList<>();

                    for(int index = 0; index < listItems.size(); index++) {
                        T prov = listItems.get(index);
                        if(prov instanceof Provider) {
                            Provider p = (Provider) prov;

                            if(p.getName().toLowerCase().contains(constr)
                                    || p.getPlaceName().toLowerCase().contains(constr)
                                    || p.getPrice().toLowerCase().contains(constr)) {
                                filterProviders.add(p);
                            }
                        }
                        else if (prov instanceof RideRequest) {
                            RideRequest rr = (RideRequest) prov;
                            if(rr.getName().toLowerCase().contains(constr)
                                    || rr.getDestName().toLowerCase().contains(constr)
                                    || rr.getPrice().toLowerCase().contains(constr)) {
                                filterProviders.add(rr);
                            }
                        }
                    }

                    results.count = filterProviders.size();
                    results.values = filterProviders;

                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredItems = (List<T>) results.values;
                    notifyDataSetChanged();
                }
            };
        }
    }
}
