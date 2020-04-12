package com.example.skilift.adapters;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skilift.R;
import com.example.skilift.interfaces.OnResultClickListener;
import com.example.skilift.models.RideRequest;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> implements Filterable {

    private List<RideRequest> requestList;
    private List<RideRequest> filteredList;
    private Filter providerFilter;
    private Context context;
    private OnResultClickListener mResultClickListener;
    private int lastSelectedPosition = -1;

    public RequestsAdapter(List<RideRequest> rrList, Context ctx) {
        requestList = rrList;
        filteredList = rrList;
        context = ctx;
        providerFilter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                String constr = constraint.toString().toLowerCase().trim();
                ArrayList<Object> filterProviders = new ArrayList<>();

                for(int index = 0; index < requestList.size(); index++) {
                    RideRequest rr = requestList.get(index);

                    if(rr.getName().toLowerCase().contains(constr)
                            || rr.getDestName().toLowerCase().contains(constr)
                            || rr.getPrice().toLowerCase().contains(constr)) {
                        filterProviders.add(rr);
                    }
                }

                results.count = filterProviders.size();
                results.values = filterProviders;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<RideRequest>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public Filter getFilter() {
        return providerFilter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RideRequest rr = requestList.get(position);
        holder.rButton.setText(rr.toString());
        holder.rButton.setChecked(lastSelectedPosition == position);
    }

    @Override
    public int getItemCount() {
        if(filteredList != null){
            return filteredList.size();
        }
        return 0;
    }

    public void setPosition(int position) {
        lastSelectedPosition = position;
    }

    public void setRequestList(List<RideRequest> providers) {
        requestList = providers;
        filteredList = providers;
        notifyDataSetChanged();
    }

    public void setOnResultClickListener(OnResultClickListener l) {
        mResultClickListener = l;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RadioButton rButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rButton = itemView.findViewById(R.id.choice_desc_button);

            rButton.setOnClickListener(v -> {
                lastSelectedPosition = getAdapterPosition();
                notifyDataSetChanged();
                mResultClickListener.onItemClick(requestList.get(lastSelectedPosition), lastSelectedPosition);
            });
        }
    }
}
