package com.luckstro.videooverlay.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.luckstro.videooverlay.R;
import com.luckstro.videooverlay.activity.AddTeamActivity;
import com.luckstro.videooverlay.activity.EditPlayerActivity;
import com.luckstro.videooverlay.entity.Player;
import com.luckstro.videooverlay.entity.Team;
import com.luckstro.videooverlay.entity.VideoOverylayEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ejdd5cj on 9/7/2017.
 */

public class EntityGridViewArrayAdapter<T> extends ArrayAdapter {
    private Context context;
    public EntityGridViewArrayAdapter(Context context, List<? extends VideoOverylayEntity> items) {
        super(context, 0, items);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.grid_entity_list_item, parent, false);
        }

        final VideoOverylayEntity item = (VideoOverylayEntity) getItem(position);
        if (item != null) {
            TextView textView = (TextView) convertView.findViewById(R.id.grid_entity_list_item_text);
            textView.setText(item.getName());

            ImageView imageView = (ImageView) convertView.findViewById(R.id.grid_entity_list_item_image);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(item.getIcon(), 0, item.getIcon().length));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item instanceof Team) {
                        Intent intent = new Intent(v.getContext(), AddTeamActivity.class);
                        intent.putExtra(AddTeamActivity.TEAM_ID, item.getId());
                        context.startActivity(intent);
                    } else if (item instanceof Player) {
                        Intent intent = new Intent(v.getContext(), EditPlayerActivity.class);
                        intent.putExtra(EditPlayerActivity.PLAYER_ID, item.getId());
                        context.startActivity(intent);
                    }
                }
            });
        }

        return convertView;
    }
}
