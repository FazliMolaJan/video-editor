package com.luckstro.videooverlay.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.luckstro.videooverlay.R;
import com.luckstro.videooverlay.entity.Team;
import com.luckstro.videooverlay.entity.VideoOverylayEntity;
import com.luckstro.videooverlay.factory.DependencyFactory;
import com.luckstro.videooverlay.graphics.ImageTool;
import com.luckstro.videooverlay.project.Project;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import static android.view.View.GONE;
import static android.view.View.inflate;

public class AddTeamActivity extends AppCompatActivity implements View.OnClickListener, PopupWindow.OnDismissListener {
    public static final int SELECT_LOGO_SUCCESS = 0;
    private Set<Integer> teamColorViewIds;
    public final static String TEAM_ID = "TeamId";
    public final static int SUCCESS = 0;
    public final static int CANCEL = 1;
    private Team.TeamType teamType;
    private View mainView;
    private int currentVisibleColorId;
    private int currentVisibleColorView = R.id.team_color_red;
    private AlertDialog alertColorPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout for this fragment
        setContentView(R.layout.team_details_fragment);
        Intent intent = getIntent();
        Long teamId = intent.getLongExtra(TEAM_ID, -1);
        if (teamId != -1) {
            loadTeam(teamId);
        } else {
            currentVisibleColorId = R.color.blue;
            changeBackground(ContextCompat.getColor(this, R.color.blue),
                    ContextCompat.getColor(this, R.color.white));
        }

        int colorValue = ContextCompat.getColor(this, currentVisibleColorId);
        ColorFilter colorFilter = new PorterDuffColorFilter(colorValue, PorterDuff.Mode.SRC_ATOP);
        View colorButton = findViewById(R.id.color_button);
        colorButton.getBackground().setColorFilter(colorFilter);
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });
        changeBackground(colorValue, ContextCompat.getColor(this, R.color.white));

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

        findViewById(R.id.edit_team_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
        });

        findViewById(R.id.edit_team_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Team team = createTeam();
                DependencyFactory.instance().getVideoOverlayDAO().createTeam(team);
                setResult(RESULT_OK);
                finish();
                return;
            }
        });
    }

    private void startImageBrowserActivity() {
        Intent intent = new Intent(this, ImageBrowserActivity.class);
        startActivityForResult(intent, SELECT_LOGO_SUCCESS);
    }

    private void changeBackground(int topColor, int bottomColor) {
        View layout = findViewById(R.id.team_details_fragment);

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {topColor,bottomColor});
        gd.setCornerRadius(0f);

        layout.setBackground(gd);

    }

    @Override
    public void onClick(View v) {
        if (teamColorViewIds.contains(v.getId())) {
            showPopup();
            return;
        }
        switch (v.getId()) {

            case R.id.add_team_on_time_use_button   :   {
                                                            setResult(RESULT_OK);
                                                            finish();
                                                            break;
                                                        }
        }
    }

    private Team createTeam() {
        Team team = new Team();
        if (!"".equals(((EditText) findViewById(R.id.team_id)).getText().toString())) {
            team.setId(Long.parseLong(
                    ((EditText) findViewById(R.id.team_id)).getText().toString()));
        }
        team.setAbbreviation(
                ((EditText) findViewById(R.id.team_name_short)).getText().toString());
        team.setName(((EditText) findViewById(R.id.team_name_long)).getText().toString());
        team.setMascot(((EditText) findViewById(R.id.team_mascot)).getText().toString());
        team.setColor(currentVisibleColorId);
        team.setIcon(createLogoByteArray());
        if (teamType == Team.TeamType.HomeTeam) {
            Project.currentProject().setHomeTeam(team);
        } else if (teamType == Team.TeamType.AwayTeam) {
            Project.currentProject().setAwayTeam(team);
        }
        return team;
    }

    private byte[] createLogoByteArray() {
        Bitmap teamIcon =
            ((BitmapDrawable)((ImageView)findViewById(R.id.team_icon_image)).getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        teamIcon.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] byteArray = bos.toByteArray();
        return byteArray;
    }

    private void loadTeam(Long teamId) {
        Team team = DependencyFactory.instance().getVideoOverlayDAO().fetchTeam(teamId);
        ((EditText) findViewById(R.id.team_id)).setText(team.getId().toString());
        ((EditText) findViewById(R.id.team_name_short)).setText(team.getAbbreviation());
        ((EditText) findViewById(R.id.team_name_long)).setText(team.getName());
        ((EditText) findViewById(R.id.team_mascot)).setText(team.getMascot());
        currentVisibleColorId = team.getColor();
        ((ImageView)findViewById(R.id.team_icon_image))
                .setImageBitmap(BitmapFactory.decodeByteArray(
                        team.getIcon(), 0, team.getIcon().length));
    }

    private void switchSelectedColor(ColorFilter colorFilter) {
        View colorButton = findViewById(R.id.color_button);
        colorButton.getBackground().setColorFilter(colorFilter);
        alertColorPicker.cancel();
    }

    private void setupColorPickerButton(View colorButton, final int color) {
        final int colorValue = ContextCompat.getColor(this, color);
        final ColorFilter colorFilter = new PorterDuffColorFilter(
                colorValue, PorterDuff.Mode.SRC_ATOP);
        colorButton.getBackground().setColorFilter(colorFilter);
        final int whiteColor = ContextCompat.getColor(this, R.color.white);
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentVisibleColorId = color;
                changeBackground(colorValue, whiteColor);
                switchSelectedColor(colorFilter);
            }
        });
    }

    private void showPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View baseView = inflate(getBaseContext(), R.layout.fragment_color_picker, null);

        setupColorPickerButton(baseView.findViewById(R.id.color_picker_red), R.color.red);
        setupColorPickerButton(baseView.findViewById(R.id.color_picker_purple), R.color.purple);
        setupColorPickerButton(baseView.findViewById(R.id.color_picker_green), R.color.green);
        setupColorPickerButton(baseView.findViewById(R.id.color_picker_yellow), R.color.yellow);
        setupColorPickerButton(baseView.findViewById(R.id.color_picker_orange), R.color.orange);
        setupColorPickerButton(baseView.findViewById(R.id.color_picker_gold), R.color.gold);
        setupColorPickerButton(baseView.findViewById(R.id.color_picker_light_blue), R.color.lightblue);
        setupColorPickerButton(baseView.findViewById(R.id.color_picker_blue), R.color.blue);
        setupColorPickerButton(baseView.findViewById(R.id.color_picker_dark_blue), R.color.darkblue);
        setupColorPickerButton(baseView.findViewById(R.id.color_picker_silver), R.color.silver);
        setupColorPickerButton(baseView.findViewById(R.id.color_picker_white), R.color.white);
        setupColorPickerButton(baseView.findViewById(R.id.color_picker_black), R.color.black);

        builder.setView(baseView);

        alertColorPicker = builder.create();
        alertColorPicker.show();
    }

    @Override
    public void onDismiss() {
        mainView.getForeground().setAlpha(0);
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
