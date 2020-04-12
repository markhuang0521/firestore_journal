package com.ming.journalapp.ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ming.journalapp.R;
import com.ming.journalapp.model.Journal;
import com.squareup.picasso.Picasso;

import java.util.List;

public class JournalRecyclerAdapter extends RecyclerView.Adapter<JournalRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Journal> journalList;

    public JournalRecyclerAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Journal journal=journalList.get(position);
        holder.title.setText(journal.getTitle());
        holder.desc.setText(journal.getDesc());
        holder.date.setText(journal.getTimeAdded());
        Picasso.get().load(journal.getImageUrl()).fit().centerCrop()
                .placeholder(R.drawable.cannon_bg)
                .into(holder.image);


    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title,desc,date;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.tv_row_title);
            desc=itemView.findViewById(R.id.tv_row_desc);
            date=itemView.findViewById(R.id.tv_row_date);
            image=itemView.findViewById(R.id.iv_row_image);

        }
    }
}
