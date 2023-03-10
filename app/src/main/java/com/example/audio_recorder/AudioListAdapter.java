package com.example.audio_recorder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {
    private File[] allFiles;
    private timeAgo timeago;
    private onItemListClick onItemListClick;
    public AudioListAdapter(File[] allFiles, onItemListClick onItemListClick) {
        this.allFiles= allFiles;
        this.onItemListClick= onItemListClick;
    }
    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item, parent, false);
        timeago= new timeAgo();
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        holder.list_title.setText(allFiles[position].getName());
        holder.list_date.setText(timeago.getTimeAgo(allFiles[position].lastModified()));
    }

    @Override
    public int getItemCount() {

        return allFiles.length;
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView list_image;
        private TextView list_title;
        private TextView list_date;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            list_image= itemView.findViewById(R.id.imageView);
            list_title= itemView.findViewById(R.id.listTitle);
            list_date= itemView.findViewById(R.id.listDate);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
      onItemListClick.setOnClickListener(allFiles[getAdapterPosition()],getAdapterPosition());
        }
    }

    public interface onItemListClick{
        void setOnClickListener(File file, int position);
    }
}
