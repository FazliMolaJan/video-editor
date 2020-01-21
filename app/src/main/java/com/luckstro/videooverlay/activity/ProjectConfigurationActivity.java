package com.luckstro.videooverlay.activity;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.luckstro.videooverlay.entity.Player;
import com.luckstro.videooverlay.view.CustomVideoView;
import com.luckstro.videooverlay.R;
import com.luckstro.videooverlay.fragment.TeamInfoScoreFragment;
import com.luckstro.videooverlay.project.Project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the main activity for project configuration.  It has a video preview of the
 * project as well as buttons to open up editor activities.
 *
 */
public class ProjectConfigurationActivity extends AppCompatActivity
		implements MediaPlayer.OnPreparedListener,
		TeamInfoScoreFragment.OnFragmentInteractionListener,
		View.OnClickListener {
	private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE =001010;
	public static int OFFSET = 250;
	public static int ADD_COMMENT_REQUEST = 200;
	public static int ADD_PLAYER_SUCCESS = 5;
	public static int TELESTRATE_SUCCESS = 10;
	public static int ADD_SCORE_SUCCESS = 20;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private String videoPath;
	private CustomVideoView videoView;
	private RelativeLayout mainLayout;
	private LinearLayout videoPanel;
	private MediaPlayer mediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Check to see if project passed in and use it for the current project, otherwise create a new empty project
		if (Project.currentProject() == null)
			Project.makeNewCurrentProject();

		setContentView(R.layout.activity_project_configuration);
		Intent intent = getIntent();
		videoPath = intent.getStringExtra("video_path");

		createMediaPlayer();
		createVideoView();

//		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.button_panel_label);
//
//		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//				ViewGroup.LayoutParams.WRAP_CONTENT);
//
//		p.addRule(RelativeLayout.BELOW, videoView.getId());
//		linearLayout.setLayoutParams(p);
//

		// The comment button opens up a new activity to add a comment to the video.
		findViewById(R.id.add_comment).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), AddCommentToProjectActivity.class);
				startActivityForResult(intent, ADD_COMMENT_REQUEST);
			}
		});
//
//		findViewById(R.id.add_player).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(v.getContext(), AddPlayerToProjectActivity.class);
//				startActivityForResult(intent, ADD_PLAYER_SUCCESS);
//			}
//		});
//
//		findViewById(R.id.add_score).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(v.getContext(), AddScoreToProjectActivity.class);
//				startActivityForResult(intent, ADD_SCORE_SUCCESS);
//			}
//		});

		// The telestrate button opens up an activity which allows the user to draw directly
		// onto the video.
		findViewById(R.id.add_telestrate).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(v.getContext(), TelestratorActivity.class);
				Intent intent = new Intent(v.getContext(), DrawingActivity.class);
				intent.putExtra("video_path", videoPath);
				startActivityForResult(intent, TELESTRATE_SUCCESS);
			}
		});

		// Add a spot shadow
		findViewById(R.id.add_spotshadow).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), SpotShadowActivity.class);
				intent.putExtra("video_path", videoPath);
				startActivityForResult(intent, TELESTRATE_SUCCESS);
			}
		});

		// Play the video
		findViewById(R.id.play_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				findViewById(R.id.play_button).setVisibility(View.GONE);
				findViewById(R.id.pause_button).setVisibility(View.VISIBLE);
				if (!mediaPlayer.isPlaying())
					mediaPlayer.start();
			}
		});

		// Pause the video
		findViewById(R.id.pause_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				findViewById(R.id.play_button).setVisibility(View.VISIBLE);
				findViewById(R.id.pause_button).setVisibility(View.GONE);
				if (mediaPlayer.isPlaying())
					mediaPlayer.pause();
			}
		});

		// We need to permission to write to the disk so we can create a temporary
		// video of the drawing, which we will add to the main video.
		int permissionCheck = ContextCompat.checkSelfPermission(this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE
		);

		if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
					MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

		} else {

		}
	}

	// TODO We need to handle the permissions better.  This really doesn't do anything
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					// TODO Handle permission request better.
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		layoutTheView();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		layoutTheView();
	}

	private void layoutTheView() {
		ActionBar actionBar = this.getSupportActionBar();
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mainLayout.getLayoutParams();
		int actionBarHeight = actionBar.getHeight();
		params.setMargins(0, 0, 0, 0);
		mainLayout.setLayoutParams(params);
		mainLayout.requestLayout();
	}

	private void createMediaPlayer() {
		mediaPlayer = new MediaPlayer();
		try {
			mediaPlayer.setDataSource(videoPath);
		} catch (IOException e) {
			// TODO Handle exception properly.
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		videoView.onResume();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO instead of seeking to 100 ms, use an imageview which overlaps the surfaceview and
		// show the preview image.  Also change the link from the video on the browser page
		// so that it does an animation when moving to this page.
		//mediaPlayer.seekTo(100);
		//mediaPlayer.start();
	}

	@Override
	public void onFragmentInteraction(Uri uri) {
	}

	@Override
	public void onClick(View v) {
//		if (v.getId() == R.id.save_project_button) {
//			updateProject();
//			DependencyFactory.instance().getVideoOverlayDAO().createProject(Project.currentProject());
//		}

//		Team homeTeam = new Team();
		// TODO fix so fragment sets home and away team
//		View homeTeamView = homeTeamFragment.getView().findViewById(R.id.team_name_short);
//		homeTeam.setAbbreviation(((EditText)homeTeamFragment.getView()
//				.findViewById(R.id.team_name_short)).getText().toString());

//		Team awayTeam = new Team();
//		awayTeam.setAbbreviation(((EditText)awayTeamFragment.getView()
//				.findViewById(R.id.team_name_short)).getText().toString());
//		Project.currentProject().setHomeTeam(homeTeam);
//		Project.currentProject().setAwayTeam(awayTeam);
//		updateProject();

//		if (v.getId() == R.id.preview_video_button) {
//			Project.currentProject().init();
//			videoView.playVideo();
//		}
	}

//	private void updateProject() {
//		if (!((EditText)findViewById(R.id.away_team_score)).getText().toString().equals("")) {
//			Project.currentProject().setAwayTeamScore(Integer.parseInt((
//					(EditText) findViewById(R.id.away_team_score)).getText().toString()));
//		}
//		if (!((EditText)findViewById(R.id.home_team_score)).getText().toString().equals("")) {
//			Project.currentProject().setHomeTeamScore(Integer.parseInt((
//					(EditText)findViewById(R.id.home_team_score)).getText().toString()));
//		}
//		Project.currentProject().setComment(((EditText) findViewById(R.id.comment)).getText().toString());
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		if ((requestCode == ADD_COMMENT_REQUEST) || (requestCode == ADD_SCORE_SUCCESS)) {
			if (resultCode == RESULT_OK) {
				videoPanel.removeView(videoView);
				createMediaPlayer();
				createVideoView();
			}
		}
	}

//	private void createVideoView() {
//		DisplayMetrics displayMetrics = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//
//		// TODO Replace this with actual sizes read from the video file.
//		int width = displayMetrics.widthPixels - OFFSET;
//		int displayHeight = displayMetrics.heightPixels;
//		float videoWidth = 1920;
//		float videoHeight = 1080;
//		float aspectRatio = videoWidth/videoHeight;
//
//		int actionBarHeight = 0;
//
//		// Calculate ActionBar height
//		TypedValue tv = new TypedValue();
//		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
//		{
//			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
//		}
//
//		int height = new Float(width/aspectRatio).intValue() - actionBarHeight;
//		// TODO Instead of passing width and height around, have a helper class where it is stored.
//		videoView = new CustomVideoView(this, mediaPlayer, width, height, false);
//		videoView.setId(View.generateViewId());
//		mainLayout = (LinearLayout) findViewById(R.id.activity_add_score_main);
//		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
//		layoutParams.setMargins(0, 0, 0, 0);
//		mainLayout.addView(videoView, layoutParams);
//	}

	private void createVideoView() {
		Point availableSpace = getDisplayDimensions(this);
		int height = availableSpace.y - OFFSET;
		// TODO Get actual size of video from file.
		float videoWidth = 1920;
		float videoHeight = 1080;
		float aspectRatio = videoWidth/videoHeight;

		int width = new Float(height * aspectRatio).intValue();
		videoView = new CustomVideoView(this, mediaPlayer, width, height, false);
		videoView.setId(View.generateViewId());
		mainLayout = (RelativeLayout) findViewById(R.id.activity_add_score_main);
		videoPanel = (LinearLayout) findViewById(R.id.video_panel);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
		videoPanel.addView(videoView, 0, layoutParams);

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.button_panel);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.RIGHT_OF, videoPanel.getId());
		linearLayout.setLayoutParams(params);
	}

	private static Point getDisplayDimensions(Context context )
	{
		WindowManager wm = ( WindowManager ) context.getSystemService( Context.WINDOW_SERVICE );
		Display display = wm.getDefaultDisplay();

		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics( metrics );
		int screenWidth = metrics.widthPixels;
		int screenHeight = metrics.heightPixels;

		// find out if status bar has already been subtracted from screenHeight
		display.getRealMetrics( metrics );
		int physicalHeight = metrics.heightPixels;
		int statusBarHeight = getStatusBarHeight( context );
		int navigationBarHeight = getNavigationBarHeight( context );
		int heightDelta = physicalHeight - screenHeight;
		if ( heightDelta == 0 || heightDelta == navigationBarHeight )
		{
			screenHeight -= statusBarHeight;
		}

		return new Point( screenWidth, screenHeight );
	}

	private static int getStatusBarHeight( Context context )
	{
		Resources resources = context.getResources();
		int resourceId = resources.getIdentifier( "status_bar_height", "dimen", "android" );
		return ( resourceId > 0 ) ? resources.getDimensionPixelSize( resourceId ) : 0;
	}

	private static int getNavigationBarHeight( Context context )
	{
		Resources resources = context.getResources();
		int resourceId = resources.getIdentifier( "navigation_bar_height", "dimen", "android" );
		return ( resourceId > 0 ) ? resources.getDimensionPixelSize( resourceId ) : 0;
	}
}
