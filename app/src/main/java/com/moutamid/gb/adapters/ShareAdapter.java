package com.moutamid.gb.adapters;

import static android.os.Build.VERSION.SDK_INT;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.moutamid.gb.R;
import com.moutamid.gb.model.StatusItem;

import java.util.List;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.StatusVH> {
    private final Context context;
    private final List<StatusItem> items;

    public ShareAdapter(Context context, List<StatusItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public StatusVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.share_item, parent, false);
        return new StatusVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusVH holder, int position) {
        StatusItem item = items.get(holder.getAbsoluteAdapterPosition());

        if (item.isImage()){
            holder.videoIcon.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(item.getFileUri())
                .centerCrop()
                .into(holder.imageView);

        holder.save.setOnClickListener(v -> {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Uri imageUri = null;
            if (item.getFilePath() != null) {
                imageUri = FileProvider.getUriForFile(context, "com.moutamid.gbwhatstool.provider", item.getFile());
            }

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            if (SDK_INT >= Build.VERSION_CODES.R)
                intent.putExtra(Intent.EXTRA_STREAM, item.getFileUri());
            else intent.putExtra(Intent.EXTRA_STREAM, imageUri);

            context.startActivity(Intent.createChooser(intent, "Share with"));
        });

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
