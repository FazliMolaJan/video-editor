package com.luckstro.videooverlay.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.luckstro.videooverlay.R;

import java.util.HashMap;

public class ImageBrowserActivity extends AppCompatActivity {
    private static final String TAG = ImageBrowserActivity.class.getName();
    public static final String IMAGE_ID_TAG = "ImageId";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 54321;
    private Cursor imageCursor;
    private GridView imageGridView;
    private ImageBrowserAdapter gridAdapter;
    private static long[] imageIds = null;
    private static String[] imagePaths = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_browser);

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        );

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        } else {
            createImageCursor();
            imageGridView = (GridView) findViewById(R.id.image_grid_view);
            new Handler().post(new Runnable() {

                @Override
                public void run() {
                    gridAdapter = new ImageBrowserAdapter(ImageBrowserActivity.this);
                    imageGridView.setAdapter(gridAdapter);
                }

            });
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    createImageCursor();
                    imageGridView = (GridView) findViewById(R.id.image_grid_view);

                    new Handler().post(new Runnable() {

                        @Override
                        public void run() {
                            gridAdapter = new ImageBrowserAdapter (
                                    ImageBrowserActivity.this);

                            imageGridView.setAdapter(gridAdapter);
                        }

                    });
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private HashMap<Long, String> images = new HashMap<>();

    private void createImageCursor() {
        String[] imageParams = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Thumbnails.DATA};

        imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null);
        if (imageCursor != null) {
            new Thread() {
                public void run() {
                    try {
                        imageCursor.moveToFirst();
                        imageIds = new long[imageCursor.getCount()];
                        imagePaths = new String[imageCursor.getCount()];
                        for (int i = 0; i < imageCursor.getCount(); i++) {
                            imageCursor.moveToPosition(i);
                            images.put(imageCursor.getLong(0), imageCursor.getString(1));
                            imageIds[i] = imageCursor.getLong(0);
                            imagePaths[i] = imageCursor.getString(1);
                            //Log.e("mNames[i]",mNames[i]+":"+cc.getColumnCount()+ " : " +cc.getString(3));
                        }
                    } catch (Exception e) {
                    }
                }
            }.start();
        }
    }

    private class ImageBrowserAdapter extends BaseAdapter {
        private Context context;

        public ImageBrowserAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return imageCursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View v;
            if (convertView != null)
                v = convertView;
            else {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.grid_entity_list_item, null);
            }

            new Thread() {
                public void run() {
                    try {
                        final long imageId = imageIds[position];
                        final Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context
                                        .getApplicationContext().getContentResolver(), imageId,
                                MediaStore.Images.Thumbnails.MINI_KIND, null);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView imageView = (ImageView) v.findViewById(R.id.grid_entity_list_item_image);
                                imageView.setImageBitmap(bitmap);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent resultIntent = new Intent();
                                        resultIntent.putExtra(IMAGE_ID_TAG, imagePaths[position]);
                                        setResult(Activity.RESULT_OK, resultIntent);
                                        finish();
                                    }
                                });
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            return v;
        }
    }
}
