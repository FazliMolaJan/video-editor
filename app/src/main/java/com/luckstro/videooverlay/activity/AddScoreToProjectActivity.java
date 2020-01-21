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
import java.util.List;
import java.util.Map;

public class AddScoreToProjectActivity extends AppCompatActivity {
    private ArrayAdapter<String> teamDataAdapter;
    private List<String> teamList = new ArrayList<>();
    private Team awayTeam;
    private Team homeTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_score);

        refreshSpinners();

        createSpinner((Spinner) findViewById(R.id.away_team_spinner), true);

        createSpinner((Spinner) findViewById(R.id.home_team_spinner), false);

        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Project.currentProject().setHomeTeam(homeTeam);
                if (((EditText) findViewById(R.id.home_team_score)).getText() != null
                        && !"".equals(((EditText) findViewById(R.id.home_team_score)).getText()))
                    Project.currentProject().setHomeTeamScore(
                            Integer.parseInt(((EditText) findViewById(R.id.home_team_score)).getText().toString()));
                Project.currentProject().setAwayTeam(awayTeam);
                if (((EditText) findViewById(R.id.away_team_score)).getText() != null
                        && !"".equals(((EditText) findViewById(R.id.away_team_score)).getText()))
                    Project.currentProject().setHomeTeamScore(
                            Integer.parseInt(((EditText) findViewById(R.id.away_team_score)).getText().toString()));
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

    private void createSpinner(Spinner spinner, final boolean useAwayTeam) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Map<Long, Team> teams = DependencyFactory.instance().getVideoOverlayDAO().getTeams();
                String currentName = teamDataAdapter.getItem(position);
                for (Map.Entry<Long, Team> team : teams.entrySet()) {
                    if (team.getValue().getName().equals(currentName)) {
                        if (useAwayTeam)
                            awayTeam = team.getValue();
                        else
                            homeTeam = team.getValue();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (useAwayTeam)
                    awayTeam = null;
                else
                    homeTeam = null;
            }
        });
    }

    private void refreshSpinners() {
        teamDataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, teamList);
        loadTeams();
        Spinner homeTeamSpinner = (Spinner) findViewById(R.id.home_team_spinner);
        homeTeamSpinner.setAdapter(teamDataAdapter);
        Spinner awayTeamSpinner = (Spinner) findViewById(R.id.away_team_spinner);
        awayTeamSpinner.setAdapter(teamDataAdapter);
    }

    private void loadTeams() {
        Map<Long, Team> teams = DependencyFactory.instance().getVideoOverlayDAO().getTeams();
        for (Map.Entry<Long, Team> team : teams.entrySet()) {
            teamList.add(team.getValue().getName());
        }
        teamDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }
}
