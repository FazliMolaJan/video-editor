package com.luckstro.videooverlay.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.luckstro.videooverlay.activity.ProjectConfigurationActivity;
import com.luckstro.videooverlay.R;

/**
 * Created by ejdd5cj on 7/12/2017.
 */

public class VideoGridViewAdapter extends CursorAdapter {
    private Context context;
    private LayoutInflater cursorInflater;

    // Default constructor
    public VideoGridViewAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        this.cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    public void bindView(View view, final Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.grid_video_list_item_image);
        final String video_path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));

        long videoId = cursor.getLong(0);
        Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(context
                        .getApplicationContext().getContentResolver(), videoId,
                MediaStore.Images.Thumbnails.MINI_KIND, null);
        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(v.getContext(), ProjectConfigurationActivity.class);
					intent.putExtra("video_path", video_path);
					context.startActivity(intent);
				}
			});
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.grid_video_list_item, parent, false);
    }
}

