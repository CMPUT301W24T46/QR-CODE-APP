package com.example.eventapp.Image;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eventapp.R;

import java.util.List;

public class ImageGridAdapter extends BaseAdapter {
    private Context context;
    private List<Image> imageItems;
    private int imageSize;

    public ImageGridAdapter(Context context, List<Image> imageItems, int imageSize) {
        this.context = context;
        this.imageItems = imageItems;
        this.imageSize = imageSize; // New parameter to set image size
    }

    @Override
    public int getCount() {
        return imageItems.size();
    }

    @Override
    public Object getItem(int position) {
        return imageItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.content_image_grid, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.gridImageItem);
        TextView imageText  = convertView.findViewById(R.id.gridImageId);

        // Set the ImageView size
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height = imageSize;
        layoutParams.width = imageSize;
        imageView.setLayoutParams(layoutParams);

        Image item = imageItems.get(position);

        Glide.with(context).load(item.getURL()).centerCrop().into(imageView);
        imageText.setText(item.getId());

        return convertView;
    }

    public void setFilter(List<Image> newImages) {
        imageItems.clear();
        imageItems.addAll(newImages);
    }
}
