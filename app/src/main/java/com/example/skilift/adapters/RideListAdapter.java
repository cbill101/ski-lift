package com.example.skilift.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skilift.R;
import com.example.skilift.interfaces.OnResultClickListener;
import com.example.skilift.models.Provider;

import java.util.ArrayList;
import java.util.List;

public class RideListAdapter extends RecyclerView.Adapter<RideListAdapter.ViewHolder> implements Filterable {

    private List<Provider> providerList;
    private List<Provider> filteredList;
    private Filter providerFilter;
    private Context context;
    private OnResultClickListener mResultClickListener;
    private Provider currentSelection;
    private int lastSelectedPosition = -1;

    public RideListAdapter(List<Provider> provList, Context ctx) {
        providerList = provList;
        filteredList = provList;
        context = ctx;
        providerFilter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                String constr = constraint.toString().toLowerCase().trim();
                ArrayList<Object> filterProviders = new ArrayList<>();

                for(Provider p : providerList) {
                    if(p.getName().toLowerCase().contains(constr)
                            || p.getPlace_name().toLowerCase().contains(constr)
                            || p.getPrice().toLowerCase().contains(constr)) {
                        filterProviders.add(p);
                    }
                }

                results.count = filterProviders.size();
                results.values = filterProviders;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<Provider>) results.values;
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
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Provider provider = filteredList.get(position);
        holder.rButton.setText(provider.toString());
        holder.rButton.setChecked(provider.getChecked());
    }

    @Override
    public int getItemCount() {
        if(filteredList != null){
            return filteredList.size();
        }
        return 0;
    }

    public void setProviderList(List<Provider> providers) {
        providerList = providers;
        filteredList = providers;
        notifyDataSetChanged();
    }

    public void setPosition(int position) {
        lastSelectedPosition = position;
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
                for(Provider p : providerList) {
                    if (p.equals(filteredList.get(getAdapterPosition()))) {
                        p.setChecked(true);
                    }
                    else
                        p.setChecked(false);
                }
                lastSelectedPosition = getAdapterPosition();
                notifyDataSetChanged();
                mResultClickListener.onItemClick(filteredList.get(lastSelectedPosition), lastSelectedPosition);
            });
        }
    }
}
