package com.luckstro.videooverlay.entity;

import android.provider.BaseColumns;

/**
 * Created by ejdd5cj on 8/28/2017.
 */

public interface VideoOverylayEntity {
    public static final int MAX_LOGO_SIZE = 256;
    public Long getId();
    public String getName();
    public byte[] getIcon();
}
