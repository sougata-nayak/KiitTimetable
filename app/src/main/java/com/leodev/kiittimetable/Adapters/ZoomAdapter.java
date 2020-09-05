package com.leodev.kiittimetable.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leodev.kiittimetable.R;

import java.util.List;

public class ZoomAdapter extends RecyclerView.Adapter<ZoomAdapter.MyViewHolder> {
    private List<String> list;

    public ZoomAdapter(List<String> list){
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View subject_text= LayoutInflater.from(parent.getContext()).inflate
               (R.layout.subject_zoomlink,parent,false);
        return new MyViewHolder(subject_text);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       holder.SubjectsName.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView SubjectsName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            SubjectsName=itemView.findViewById(R.id.subject_id);
        }
    }
}
