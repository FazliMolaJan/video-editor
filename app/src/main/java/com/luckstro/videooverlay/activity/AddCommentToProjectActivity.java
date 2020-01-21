package com.luckstro.videooverlay.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.luckstro.videooverlay.R;
import com.luckstro.videooverlay.entity.Team;
import com.luckstro.videooverlay.factory.DependencyFactory;
import com.luckstro.videooverlay.project.Project;

import java.util.ArrayList;
import java.util.Map;

public class AddCommentToProjectActivity extends AppCompatActivity {
    private ArrayList<String> commentIconTypes;
    private ArrayAdapter<String> commentIconTypesAdapter;
    private Project.CommentIconType selectCommentIconType = Project.CommentIconType.None;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment_to_project);

        commentIconTypes = new ArrayList<>();
        commentIconTypes.add(Project.CommentIconType.None.toString());
        commentIconTypes.add(Project.CommentIconType.AwayTeam.toString());
        commentIconTypes.add(Project.CommentIconType.HomeTeam.toString());
        commentIconTypes.add(Project.CommentIconType.Player.toString());

        commentIconTypesAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, commentIconTypes);
        Spinner iconSpinner = (Spinner) findViewById(R.id.use_icon);
        iconSpinner.setAdapter(commentIconTypesAdapter);

        ((Spinner) findViewById(R.id.use_icon)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectCommentIconType =
                        Project.CommentIconType.valueOf(commentIconTypes.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectCommentIconType = Project.CommentIconType.None;
            }
        });

        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Project.currentProject().setComment(
                        ((EditText) findViewById(R.id.comment)).getText().toString());
                Project.currentProject().setCommentIconType(selectCommentIconType);
                setResult(RESULT_OK);
                finish();
            }
        });

        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
