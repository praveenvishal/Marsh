package com.android.marshplay.ui.saved;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.github.siyamed.shapeimageview.RoundedImageView;

import java.util.List;

import com.android.marshplay.R;
import com.android.marshplay.data.local.Image;
import com.android.marshplay.util.GlideUtil;

public class SavedImageListAdapter extends RecyclerView.Adapter<SavedImageListAdapter.ImageModel> {
    private final Context context;
    private List<Image> items;
    private OnImageClickListener onImageClickListener;

    public SavedImageListAdapter(List<Image> items, Context context, OnImageClickListener clickListener) {
        this.items = items;
        this.context = context;
        this.onImageClickListener = clickListener;
    }

    @Override
    public ImageModel onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item_image, parent, false);
        return new ImageModel(v);
    }

    @Override
    public void onBindViewHolder(ImageModel holder, int position) {
        Image item = items.get(position);
        holder.set(item);
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public void setImages(List<Image> images) {
        items.clear();
        items.addAll(images);
        notifyDataSetChanged();
    }

    public interface OnImageClickListener {
        void onImageClicked(Image image);
    }

    public class ImageModel extends RecyclerView.ViewHolder {

        RoundedImageView imageView;
        Image image;

        public ImageModel(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(v -> {
                if (onImageClickListener != null) {
                    onImageClickListener.onImageClicked(image);
                }
            });
        }

        public void set(Image item) {
            image = item;
            GlideUtil.showRemoteImage(imageView, item.getImagePath());
        }
    }
}