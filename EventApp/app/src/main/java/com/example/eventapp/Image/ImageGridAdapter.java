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

/**
 * Adapter class for displaying a grid of images. This adapter manages a collection of {@link Image} objects
 * and binds them to views to be displayed in a grid layout.
 */

public class ImageGridAdapter extends BaseAdapter {
    private Context context;
    private List<Image> imageItems;
    private int imageSize;

    /**
     * Constructs a new {@link ImageGridAdapter}.
     *
     * @param context The current context.
     * @param imageItems The list of {@link Image} objects to be displayed.
     * @param imageSize The desired size of each image in the grid.
     */

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

    /**
     * Creates a new {@link View} for an item referenced by the Adapter.
     *
     * @param position The position of the item within the adapter's data set of the item whose view we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return A {@link View} corresponding to the data at the specified position.
     */

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

    /**
     * Updates the data set used by the adapter and refreshes the grid view.
     *
     * @param newImages The new list of {@link Image} objects to be displayed.
     */

    public void setFilter(List<Image> newImages) {
        imageItems.clear();
        imageItems.addAll(newImages);
    }
}
