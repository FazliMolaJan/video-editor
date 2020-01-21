package com.luckstro.videooverlay.entity;

import android.provider.BaseColumns;

/**
 * Created by ejdd5cj on 10/7/2017.
 */

public class Player implements VideoOverylayEntity {
    public class PlayerColumns implements BaseColumns {
        public static final String TABLE_NAME = "PLAYER";
        public static final String FIRST_NAME = "FIRST_NAME";
        public static final String LAST_NAME = "LAST_NAME";
        public static final String NICK_NAME = "NICK_NAME";
        public static final String ICON = "ICON";
    }

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + Player.PlayerColumns.TABLE_NAME + " (" +
            Player.PlayerColumns._ID + " INTEGER PRIMARY KEY," +
            Player.PlayerColumns.FIRST_NAME + " TEXT," +
            Player.PlayerColumns.LAST_NAME + " TEXT," +
            Player.PlayerColumns.NICK_NAME + " TEXT," +
            Player.PlayerColumns.ICON + " BLOB" + ")";

    private Long id;
    private String firstName;
    private String lastName;
    private String nickName;
    private byte[] icon;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        StringBuilder fullName = new StringBuilder();
        if (!isEmptyString(firstName))
            fullName.append(firstName);
        if ((!isEmptyString(firstName)) && (!isEmptyString(nickName)))
            fullName.append(" ");
        if (!isEmptyString(nickName))
            fullName.append("\"" + nickName +"\"");
        if ((!isEmptyString(firstName) && !isEmptyString(lastName)) ||
                (!isEmptyString(nickName) && !isEmptyString(lastName)))
            fullName.append(" ");
        if (!isEmptyString(lastName))
            fullName.append(lastName);
        return fullName.toString();
    }

    private boolean isEmptyString(String string) {
        if (string == null) return true;
        if (string.equals("")) return true;
        return false;
    }

    @Override
    public byte[] getIcon() {
        return icon;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }
}
