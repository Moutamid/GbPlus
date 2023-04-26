package com.moutamid.gb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.moutamid.gb.R;
import com.moutamid.gb.model.StatusItem;
import com.moutamid.gb.utilis.Constants;

import java.io.File;
import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusVH> {
    private final Context context;
    private final List<StatusItem> items;

    public StatusAdapter(Context context, List<StatusItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public StatusVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.status_item, parent, false);
        return new StatusVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusVH holder, int position) {
        StatusItem item = items.get(holder.getAbsoluteAdapterPosition());

        if (item.isDownloaded()){
            holder.save.setEnabled(false);
            holder.save.setClickable(false);
            holder.text.setText("Already Downloaded");
            holder.icon.setImageResource(R.drawable.round_download_done_24);
        }

        if (item.isImage()){
            holder.videoIcon.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(item.getFileUri())
                .centerCrop()
                .into(holder.imageView);

        holder.save.setOnClickListener(v -> {
            if (download(item.getFileUri().toString(), item.getFileName())){
                Toast.makeText(context, "Status Saved!", Toast.LENGTH_SHORT).show();
                notifyItemChanged(holder.getAbsoluteAdapterPosition());
            } else {
                Toast.makeText(context, "Status is not saved try again!", Toast.LENGTH_SHORT).show();
                notifyItemChanged(holder.getAbsoluteAdapterPosition());
            }
        });

    }

    private boolean download(String videoPath, String fileName) {
        boolean isDownload = false;

        if (Constants.checkFolder()) {
            File source = new File(videoPath);
            File target = new File(Constants.SAVED_FOLDER + fileName);

            if (target.exists())
                Toast.makeText(context, "Already Saved", Toast.LENGTH_SHORT).show();
            else {
                isDownload = Constants.copyFileInSavedDir(context, videoPath, fileName);
            }
        } else Toast.makeText(context, "File not save please try again!", Toast.LENGTH_SHORT).show();

        return isDownload;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class StatusVH extends RecyclerView.ViewHolder{
        ImageView icon, videoIcon, imageView;
        MaterialCardView save;
        TextView text;
        public StatusVH(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            videoIcon = itemView.findViewById(R.id.videoIcon);
            imageView = itemView.findViewById(R.id.image);
            save = itemView.findViewById(R.id.save);
            text = itemView.findViewById(R.id.text);
        }
    }

}
