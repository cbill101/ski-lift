package com.example.skilift.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skilift.R;
import com.example.skilift.adapters.RequestsAdapter;
import com.example.skilift.adapters.RideListAdapter;
import com.example.skilift.interfaces.OnResultClickListener;
import com.example.skilift.models.Provider;
import com.example.skilift.models.RideRequest;
import com.example.skilift.viewmodels.ProviderVM;

import java.util.ArrayList;
import java.util.List;

public class ProviderFragment extends Fragment implements OnResultClickListener {
    private ProviderVM provViewModel;
    private RideListAdapter rideAdapter;
    private RequestsAdapter requestsAdapter;
    private RecyclerView listView;
    private List<Provider> provList;
    private List<RideRequest> reqList;
    private EditText searchQueryText;
    private OnResultClickListener listener;
    private boolean isProvider = false;

    public ProviderFragment() {
        // Required empty public constructor
    }

    public ProviderFragment(boolean isAProvider) {
        isProvider = isAProvider;
    }

    public static ProviderFragment newInstance() {
        return new ProviderFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        provViewModel = ViewModelProviders.of(requireActivity()).get(ProviderVM.class);
        provList = new ArrayList<>();
        reqList = new ArrayList<>();

        if (isProvider)
            requestsAdapter = new RequestsAdapter(reqList, getContext());
        else
            rideAdapter = new RideListAdapter(provList, getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result_list, container, false);
        listView = view.findViewById(R.id.listView);
        searchQueryText = view.findViewById(R.id.filterTextInput);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        listView.setLayoutManager(llm);

        retrieveList();

        return view;
    }

    public void setOnResultClickListener(OnResultClickListener listener) {
        this.listener = listener;
    }

    public void setProviderFlag(boolean providerFlag) {
        isProvider = providerFlag;
    }

    private void retrieveList() {
        if(requestsAdapter != null) {
            requestsAdapter.setOnResultClickListener(this);
            listView.setAdapter(requestsAdapter);

            provViewModel.getRequestList().observe(this, requests -> {
                reqList = requests;
                requestsAdapter.setRequestList(reqList);
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
                    requestsAdapter.getFilter().filter(s);
                }
            });
        }
        else {
            rideAdapter.setOnResultClickListener(this);
            listView.setAdapter(rideAdapter);

            provViewModel.getProviderList().observe(this, providers -> {
                provList = providers;
                rideAdapter.setProviderList(provList);
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
                    rideAdapter.getFilter().filter(s);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(Parcelable result, int position) {
        if(listener != null)
            listener.onItemClick(result, position);
    }
}
