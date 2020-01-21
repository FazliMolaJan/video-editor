package com.luckstro.videooverlay.factory;

import android.app.Activity;

import com.luckstro.videooverlay.db.VideoOverlayDAO;
import com.luckstro.videooverlay.db.VideoOverlayDatabaseHelper;

/**
 * Created by ejdd5cj on 8/17/2017.
 */

public class DependencyFactory {
    private static DependencyFactory dependencyFactory;
    private VideoOverlayDAO videoOverlayDAO;

    private DependencyFactory() {
    }

    public static DependencyFactory instance() {
        if (dependencyFactory == null) {
            dependencyFactory = new DependencyFactory();
        }
        return dependencyFactory;
    }

    public void init(Activity mainActivity) {
        // TODO This is only for testing purposes.  Remove before
        mainActivity.deleteDatabase(VideoOverlayDatabaseHelper.DATABASE_NAME);
        VideoOverlayDatabaseHelper videoOverlayDatabaseHelper =
                new VideoOverlayDatabaseHelper(mainActivity);
        videoOverlayDAO = new VideoOverlayDAO(videoOverlayDatabaseHelper);
        videoOverlayDAO.loadAllTeamsToCache();
        videoOverlayDAO.loadAllPlayersToCache();
    }

    public VideoOverlayDAO getVideoOverlayDAO() {
        return this.videoOverlayDAO;
    }
}
