package com.luckstro.videooverlay.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.luckstro.videooverlay.R;
import com.luckstro.videooverlay.entity.Player;
import com.luckstro.videooverlay.factory.DependencyFactory;
import com.luckstro.videooverlay.project.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPlayerToProjectActivity extends AppCompatActivity {
    private ArrayAdapter<String> playerDataAdapter;
    private List<String> playerList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player_to_project);
        playerDataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, playerList);
        loadPlayers();
        Spinner playerSpinner = (Spinner) findViewById(R.id.player_spinner);
        playerSpinner.setAdapter(playerDataAdapter);

        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPlayerName = playerDataAdapter.getItem(
                        ((Spinner)findViewById(R.id.player_spinner)).getSelectedItemPosition());
                // TODO Correct this so that it queries players based off of id.
                Map<Long, Player> players =
                        DependencyFactory.instance().getVideoOverlayDAO().getPlayers();
                for (Map.Entry<Long, Player> player : players.entrySet()) {
                    if (player.getValue().getName().equals(currentPlayerName)) {
                        Project.currentProject().setPlayer(player.getValue());
                    }
                }

                finish();
            }
        });
    }

    private void loadPlayers() {
        Map<Long, Player> players = DependencyFactory.instance().getVideoOverlayDAO().getPlayers();
        for (Map.Entry<Long, Player> player : players.entrySet()) {
            playerList.add(player.getValue().getName());
        }
        playerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }
}
