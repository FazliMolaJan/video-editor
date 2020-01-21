package com.luckstro.videooverlay.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.luckstro.videooverlay.R;
import com.luckstro.videooverlay.entity.Player;
import com.luckstro.videooverlay.entity.Team;
import com.luckstro.videooverlay.entity.VideoOverylayEntity;
import com.luckstro.videooverlay.factory.DependencyFactory;
import com.luckstro.videooverlay.graphics.ImageTool;
import com.luckstro.videooverlay.project.Project;

import java.io.ByteArrayOutputStream;

public class EditPlayerActivity extends AppCompatActivity {
    public static final String PLAYER_ID = "PlayerId";
    public static final int SELECT_LOGO_SUCCESS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_player);

        Intent intent = getIntent();
        Long playerId = intent.getLongExtra(PLAYER_ID, -1);
        if (playerId != -1) {
            loadPlayer(playerId);
        }

        View photoHeader = findViewById(R.id.photoHeader);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        /* For devices equal or higher than lollipop set the translation above everything else */
            photoHeader.setTranslationZ(6);
        /* Redraw the view to show the translation */
            photoHeader.invalidate();
        }

        findViewById(R.id.team_icon_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImageBrowserActivity();
            }
        });

        findViewById(R.id.team_icon_edit_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImageBrowserActivity();
            }
        });

        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
        });

        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Player player = createPlayer();
                DependencyFactory.instance().getVideoOverlayDAO().createPlayer(player);
                setResult(RESULT_OK);
                finish();
                return;
            }
        });
    }

    private void loadPlayer(Long playerId) {
        Player player = DependencyFactory.instance().getVideoOverlayDAO().fetchPlayer(playerId);
        ((EditText) findViewById(R.id.player_id)).setText(player.getId().toString());
        ((EditText) findViewById(R.id.player_first_name)).setText(player.getFirstName());
        ((EditText) findViewById(R.id.player_last_name)).setText(player.getLastName());
        ((EditText) findViewById(R.id.player_nick_name)).setText(player.getNickName());
        ((ImageView)findViewById(R.id.team_icon_image))
                .setImageBitmap(BitmapFactory.decodeByteArray(
                        player.getIcon(), 0, player.getIcon().length));
    }

    private void startImageBrowserActivity() {
        Intent intent = new Intent(this, ImageBrowserActivity.class);
        startActivityForResult(intent, SELECT_LOGO_SUCCESS);
    }

    private Player createPlayer() {
        Player player = new Player();
        if (!"".equals(((EditText) findViewById(R.id.player_id)).getText().toString())) {
            player.setId(Long.parseLong(
                    ((EditText) findViewById(R.id.player_id)).getText().toString()));
        }
        player.setFirstName(((EditText) findViewById(R.id.player_first_name)).getText().toString());
        player.setLastName(((EditText) findViewById(R.id.player_last_name)).getText().toString());
        player.setNickName(((EditText) findViewById(R.id.player_nick_name)).getText().toString());
        player.setIcon(createLogoByteArray());

        return player;
    }

    private byte[] createLogoByteArray() {
        Bitmap teamIcon =
                ((BitmapDrawable)((ImageView)findViewById(R.id.team_icon_image)).getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        teamIcon.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] byteArray = bos.toByteArray();
        return byteArray;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (SELECT_LOGO_SUCCESS) : {
                if (resultCode == Activity.RESULT_OK) {
                    String imagePath = data.getStringExtra(ImageBrowserActivity.IMAGE_ID_TAG);
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

                    Bitmap scaledDownBitmap = ImageTool.scaleDown(bitmap, VideoOverylayEntity.MAX_LOGO_SIZE, true);
                    de.hdodenhof.circleimageview.CircleImageView imageView =
                            (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.team_icon_image);
                    imageView.setImageBitmap(scaledDownBitmap);
                }
                break;
            }
        }
    }
}
