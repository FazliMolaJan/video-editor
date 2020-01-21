package com.luckstro.videooverlay.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.luckstro.videooverlay.entity.Player;
import com.luckstro.videooverlay.entity.Team;
import com.luckstro.videooverlay.entity.VideoOverylayEntity;
import com.luckstro.videooverlay.project.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ejdd5cj on 8/16/2017.
 */

public class VideoOverlayDAO {
    private VideoOverlayDatabaseHelper videoOverlayDatabaseHelper;
    private HashMap<Long, Team> teamCache = new HashMap<>();
    private HashMap<Long, Project> projectCache = new HashMap<>();
    private HashMap<Long, Player> playerCache = new HashMap<>();
    public VideoOverlayDAO(VideoOverlayDatabaseHelper videoOverlayDatabaseHelper) {
        this.videoOverlayDatabaseHelper = videoOverlayDatabaseHelper;
    }

    public void createTeam(Team team) {
        SQLiteDatabase database = videoOverlayDatabaseHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Team.TeamColumns.NAME, team.getName());
        values.put(Team.TeamColumns.ABBREVIATION, team.getAbbreviation());
        values.put(Team.TeamColumns.MASCOT, team.getMascot());
        values.put(Team.TeamColumns.COLOR, team.getColor());
        values.put(Team.TeamColumns.ICON, team.getIcon());
        if (team.getId() != null) {
            database.update(Team.TeamColumns.TABLE_NAME, values, "_id=?",
                    new String[]{Long.toString(team.getId())});
        } else {
            // Insert the new row, returning the primary key value of the new row
            long newRowId = database.insert(Team.TeamColumns.TABLE_NAME, null, values);
            team.setId(newRowId);
        }

        teamCache.put(team.getId(), team);
    }

    public void createProject(Project project) {
        SQLiteDatabase database = videoOverlayDatabaseHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Project.ProjectColumns.NAME, project.getName());
        values.put(Project.ProjectColumns.ICON, project.getIcon());
        if (project.getId() != null) {
            database.update(Project.ProjectColumns.TABLE_NAME, values, "_id=?",
                    new String[]{Long.toString(project.getId())});
        } else {
            // Insert the new row, returning the primary key value of the new row
            long newRowId = database.insert(Project.ProjectColumns.TABLE_NAME, null, values);
            project.setId(newRowId);
        }

        projectCache.put(project.getId(), project);
    }

    public void createPlayer(Player player) {
        SQLiteDatabase database = videoOverlayDatabaseHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Player.PlayerColumns.FIRST_NAME, player.getFirstName());
        values.put(Player.PlayerColumns.LAST_NAME, player.getLastName());
        values.put(Player.PlayerColumns.NICK_NAME, player.getNickName());
        values.put(Player.PlayerColumns.ICON, player.getIcon());
        if (player.getId() != null) {
            database.update(Player.PlayerColumns.TABLE_NAME, values, "_id=?",
                    new String[]{Long.toString(player.getId())});
        } else {
            // Insert the new row, returning the primary key value of the new row
            long newRowId = database.insert(Player.PlayerColumns.TABLE_NAME, null, values);
            player.setId(newRowId);
        }

        playerCache.put(player.getId(), player);
    }

    public void loadAllTeamsToCache() {
        SQLiteDatabase db = videoOverlayDatabaseHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                Team.TeamColumns._ID,
                Team.TeamColumns.NAME,
                Team.TeamColumns.ABBREVIATION,
                Team.TeamColumns.MASCOT,
                Team.TeamColumns.COLOR,
                Team.TeamColumns.ICON
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                Team.TeamColumns.NAME + " DESC";

        Cursor cursor = db.query(
                Team.TeamColumns.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while(cursor.moveToNext()) {
            Team team = new Team();
            team.setId(cursor.getLong(
                    cursor.getColumnIndexOrThrow(Team.TeamColumns._ID)));
            team.setName(cursor.getString(cursor.getColumnIndexOrThrow(Team.TeamColumns.NAME)));
            team.setAbbreviation(cursor.getString(
                    cursor.getColumnIndexOrThrow(Team.TeamColumns.ABBREVIATION)));
            team.setMascot(cursor.getString(cursor.getColumnIndexOrThrow(Team.TeamColumns.MASCOT)));
            team.setColor(cursor.getInt(cursor.getColumnIndexOrThrow(Team.TeamColumns.COLOR)));
            team.setIcon(cursor.getBlob(cursor.getColumnIndexOrThrow(Team.TeamColumns.ICON)));
            teamCache.put(team.getId(), team);
        }
        cursor.close();
    }

    public void loadAllProjectsToCache() {
        SQLiteDatabase db = videoOverlayDatabaseHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                Project.ProjectColumns._ID,
                Project.ProjectColumns.NAME,
                Project.ProjectColumns.ICON
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                Project.ProjectColumns.NAME + " DESC";

        Cursor cursor = db.query(
                Project.ProjectColumns.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while(cursor.moveToNext()) {
            Project project = new Project();
            project.setId(cursor.getLong(
                    cursor.getColumnIndexOrThrow(Project.ProjectColumns._ID)));
            project.setName(cursor.getString(cursor.getColumnIndexOrThrow(Project.ProjectColumns.NAME)));
            project.setIcon(cursor.getBlob(cursor.getColumnIndexOrThrow(Project.ProjectColumns.ICON)));
            projectCache.put(project.getId(), project);
        }
        cursor.close();
    }

    public void loadAllPlayersToCache() {
        SQLiteDatabase db = videoOverlayDatabaseHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                Player.PlayerColumns._ID,
                Player.PlayerColumns.FIRST_NAME,
                Player.PlayerColumns.LAST_NAME,
                Player.PlayerColumns.NICK_NAME,
                Team.TeamColumns.ICON
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                Player.PlayerColumns.LAST_NAME + " DESC";

        Cursor cursor = db.query(
                Player.PlayerColumns.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while(cursor.moveToNext()) {
            Player player = new Player();
            player.setId(cursor.getLong(
                    cursor.getColumnIndexOrThrow(Player.PlayerColumns._ID)));
            player.setFirstName(cursor.getString(
                    cursor.getColumnIndexOrThrow(Player.PlayerColumns.FIRST_NAME)));
            player.setLastName(cursor.getString(
                    cursor.getColumnIndexOrThrow(Player.PlayerColumns.LAST_NAME)));
            player.setNickName(cursor.getString(
                    cursor.getColumnIndexOrThrow(Player.PlayerColumns.NICK_NAME)));
            player.setIcon(cursor.getBlob(cursor.getColumnIndexOrThrow(Player.PlayerColumns.ICON)));
            playerCache.put(player.getId(), player);
        }
        cursor.close();
    }

    public List<VideoOverylayEntity> getAllEntities() {
        ArrayList<VideoOverylayEntity> entities = new ArrayList<>();
        entities.addAll(teamCache.values());
        entities.addAll(projectCache.values());
        entities.addAll(playerCache.values());
        return entities;
    }

    public Map<Long, Team> getTeams() {
        return teamCache;
    }

    public Team fetchTeam(Long teamId) {
        return teamCache.get(teamId);
    }

    public Map<Long, Player> getPlayers() {
        return playerCache;
    }

    public Project fetchProject(Long projectId) {
        return projectCache.get(projectId);
    }

    public Player fetchPlayer(Long playerId) {return playerCache.get(playerId);}
}
