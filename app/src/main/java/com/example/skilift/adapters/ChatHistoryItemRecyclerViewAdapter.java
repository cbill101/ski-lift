package com.example.skilift.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.skilift.R;
import com.example.skilift.fragments.ChatHistoryFragment.OnListFragmentInteractionListener;
import com.example.skilift.models.ChatItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link com.example.skilift.models.ChatItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class ChatHistoryItemRecyclerViewAdapter extends RecyclerView.Adapter<ChatHistoryItemRecyclerViewAdapter.ViewHolder> {

    private List<ChatItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public ChatHistoryItemRecyclerViewAdapter(List<ChatItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chat_hist_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mChatSnipView.setText(mValues.get(position).getChatSnippet());

        Glide.with(holder.mProfPicView.getContext())
                .load(Uri.parse(mValues.get(position).getProfPicUri()))
                .centerCrop()
                .apply(RequestOptions.circleCropTransform())
                .into(holder.mProfPicView);

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setListItems(List<ChatItem> items) {
        mValues = items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mProfPicView;
        final TextView mNameView;
        final TextView mChatSnipView;
        ChatItem mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mProfPicView = view.findViewById(R.id.profPicView);
            mNameView = view.findViewById(R.id.personNameTV);
            mChatSnipView = view.findViewById(R.id.chatSnippetTV);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'" + mChatSnipView.getText() + "'";
        }
    }
}
