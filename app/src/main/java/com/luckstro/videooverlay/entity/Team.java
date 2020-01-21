package com.luckstro.videooverlay.entity;

import android.provider.BaseColumns;

/**
 * Created by ejdd5cj on 7/7/2017.
 */

public class Team implements VideoOverylayEntity {
    public class TeamColumns implements BaseColumns {
        public static final String TABLE_NAME = "TEAM";
        public static final String NAME = "NAME";
        public static final String ABBREVIATION = "ABBREVIATION";
        public static final String MASCOT = "MASCOT";
        public static final String COLOR = "COLOR";
        public static final String ICON = "ICON";
    }

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TeamColumns.TABLE_NAME + " (" +
            TeamColumns._ID + " INTEGER PRIMARY KEY," +
            TeamColumns.NAME + " TEXT," +
            TeamColumns.ABBREVIATION + " TEXT," +
            TeamColumns.MASCOT + " TEXT," +
            TeamColumns.COLOR + " INTEGER," +
            TeamColumns.ICON + " BLOB" + ")";

    public enum TeamType {
        HomeTeam, AwayTeam;
    }

    private Long id;
    private String name;
    private String abbreviation;
    private String mascot;
    private int color;
    private byte[] icon;

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public String getMascot() {
        return mascot;
    }

    public void setMascot(String mascot) {
        this.mascot = mascot;
    }

    public byte[] getIcon() {
        return this.icon;
    }
}
