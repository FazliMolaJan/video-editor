package com.luckstro.videooverlay.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.luckstro.videooverlay.entity.Player;
import com.luckstro.videooverlay.entity.Team;
import com.luckstro.videooverlay.project.Project;

/**
 * Created by ejdd5cj on 8/16/2017.
 */

public class VideoOverlayDatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "VideoOverlay.db";

    public VideoOverlayDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Team.SQL_CREATE_TABLE);
        db.execSQL(Player.SQL_CREATE_TABLE);
        db.execSQL(Project.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
