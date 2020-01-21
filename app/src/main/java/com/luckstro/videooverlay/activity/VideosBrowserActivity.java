package com.luckstro.videooverlay.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.luckstro.videooverlay.R;
import com.luckstro.videooverlay.adapter.VideoGridViewAdapter;

public class VideosBrowserActivity extends AppCompatActivity {
	private static final String TAG = VideosBrowserActivity.class.getName();
	private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 54321;
	private Cursor videoCursor;
	private GridView videoGridView;
	private VideoGridViewAdapter gridAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_videos_browser);

		int permissionCheck = ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_EXTERNAL_STORAGE
		);

		if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
					MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

		} else {
			createVideoCursor();
			videoGridView = (GridView) findViewById(R.id.video_grid_view);
			new Handler().post(new Runnable() {

				@Override
				public void run() {
					gridAdapter = new VideoGridViewAdapter(
							VideosBrowserActivity.this,
							videoCursor,
							0);

					videoGridView.setAdapter(gridAdapter);
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
					createVideoCursor();
					videoGridView = (GridView) findViewById(R.id.video_grid_view);

					new Handler().post(new Runnable() {

						@Override
						public void run() {
							gridAdapter = new VideoGridViewAdapter(
									VideosBrowserActivity.this,
									videoCursor,
									0);

							videoGridView.setAdapter(gridAdapter);
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

	private void createVideoCursor() {
		String[] videoParams = {MediaStore.Video.Media._ID,
				MediaStore.Video.Media.DATA,
				MediaStore.Video.Media.DATE_TAKEN,
				MediaStore.Video.Thumbnails.DATA};

		videoCursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoParams, null, null, null);
	}
}
