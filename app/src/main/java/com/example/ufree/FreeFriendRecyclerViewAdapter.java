package com.example.ufree;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ufree.FreeFriendFragment.OnListFragmentInteractionListener;
import com.example.ufree.FreeFriend.FreeFriendContent.FreeFriend;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link FreeFriend} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class FreeFriendRecyclerViewAdapter extends RecyclerView.Adapter<FreeFriendRecyclerViewAdapter.ViewHolder> {

    private final List<FreeFriend> freeFriends;
    private final OnListFragmentInteractionListener mListener;

    public FreeFriendRecyclerViewAdapter(List<FreeFriend> items, OnListFragmentInteractionListener listener) {
        freeFriends = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_freefriend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = freeFriends.get(position);
        holder.mIdView.setText(freeFriends.get(position).id);
        holder.mContentView.setText(freeFriends.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return freeFriends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public FreeFriend mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        // TODO: Figure out how to enable onClick in Recycler View
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mView.getContext(), SingleFriendEventsActivity.class);
            mView.getContext().startActivity(intent);
        }
    }

}
