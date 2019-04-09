package com.example.ufree;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter {

    ArrayList<Event> events;

    public CustomAdapter(ArrayList<Event> e) {
        this.events = e;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView description;

        public MyViewHolder(View view) {
            super(view);
            description = (TextView) view.findViewById(R.id.description);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rowlayout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Event event = events.get(position);
        //this better be okay...
        MyViewHolder mvHolder = (MyViewHolder) holder;
        mvHolder.description.setText(event.description);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
