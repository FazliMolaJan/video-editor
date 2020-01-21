package com.luckstro.videooverlay;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.GridView;
import android.widget.ImageView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.luckstro.videooverlay.activity.AddTeamActivity;
import com.luckstro.videooverlay.activity.EditPlayerActivity;
import com.luckstro.videooverlay.activity.VideosBrowserActivity;
import com.luckstro.videooverlay.adapter.EntityGridViewArrayAdapter;
import com.luckstro.videooverlay.adapter.VideoGridViewAdapter;
import com.luckstro.videooverlay.entity.VideoOverylayEntity;
import com.luckstro.videooverlay.factory.DependencyFactory;

import java.util.List;

public class MainActivity extends AppCompatActivity {
	private Cursor entityCursor;
	private GridView entityGridView;
	private VideoGridViewAdapter gridAdapter;
	private boolean hideMenu = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DependencyFactory.instance().init(this);

		loadEntites();

		View newTeamButton = findViewById(R.id.multiple_action_menu_new_team);
		newTeamButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), AddTeamActivity.class);
				startActivity(intent);
			}
		});

		View newPlayerButton = findViewById(R.id.multiple_action_menu_new_player);
		newPlayerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), EditPlayerActivity.class);
				startActivity(intent);
			}
		});

		View newProjectButton = findViewById(R.id.multiple_action_menu_new_project);
		newProjectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), VideosBrowserActivity.class);
				startActivity(intent);
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		FloatingActionsMenu menu = (FloatingActionsMenu) findViewById(R.id.multiple_action_menu);
		menu.collapse();
		loadEntites();
	}

	private void loadEntites() {
		View gridViewEmptyMessage = findViewById(R.id.entity_grid_view_empty_text_view);
		entityGridView = (GridView) findViewById(R.id.entity_grid_view);
		List<VideoOverylayEntity> videoOverlayEntities =
				DependencyFactory.instance().getVideoOverlayDAO().getAllEntities();
		if (videoOverlayEntities.size() > 0) {
			EntityGridViewArrayAdapter<? extends VideoOverylayEntity> videoEntityAdapter =
					new EntityGridViewArrayAdapter<>(this, videoOverlayEntities);
			entityGridView.setAdapter(videoEntityAdapter);
			gridViewEmptyMessage.setVisibility(View.GONE);
			entityGridView.setVisibility(View.VISIBLE);
		} else {
			gridViewEmptyMessage.setVisibility(View.VISIBLE);
			entityGridView.setVisibility(View.GONE);
		}
	}
}
