package com.luckstro.videooverlay.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.luckstro.videooverlay.R;

/**
 * Created by ejdd5cj on 9/22/2017.
 */

public class ImageGridViewAdapter extends CursorAdapter {
    private Context context;
    private LayoutInflater cursorInflater;

    // Default constructor
    public ImageGridViewAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        this.cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    public void bindView(View view, final Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.grid_video_list_item_image);
        final String image_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));

        long imageId = cursor.getLong(0);
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context
                        .getApplicationContext().getContentResolver(), imageId,
                MediaStore.Images.Thumbnails.MINI_KIND, null);
        imageView.setImageBitmap(bitmap);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.grid_video_list_item, parent, false);
    }
}
