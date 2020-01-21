package com.luckstro.videooverlay.project;

import android.provider.BaseColumns;

import com.luckstro.videooverlay.entity.Player;
import com.luckstro.videooverlay.entity.Team;
import com.luckstro.videooverlay.entity.VideoOverylayEntity;
import com.luckstro.videooverlay.graphics.DrawingPath;
import com.luckstro.videooverlay.overlays.CommentOverlay;
import com.luckstro.videooverlay.overlays.MiniScoreOverlay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by ejdd5cj on 7/7/2017.
 */

public class Project implements VideoOverylayEntity {
    public enum CommentIconType {
        None, HomeTeam, AwayTeam, Player;
    }
    private static Project currentProject;
    private Long id;
    private String name;
    private byte[] icon;
    private String videoId;
    private Team awayTeam;
    private Team homeTeam;
    private int awayTeamScore;
    private int homeTeamScore;
    private String comment;
    private MiniScoreOverlay miniScoreOverlay;
    private CommentOverlay commentOverlay;
    private CommentIconType commentIconType = CommentIconType.None;
    private Player player;
    private TreeMap<Integer, FrameInfo> frameInfoHolder = new TreeMap<>();
    private int currentVideoFrame;
    private HashMap<Long, List<DrawingPath>> drawingPaths = new HashMap<>();

    public class ProjectColumns implements BaseColumns {
        public static final String TABLE_NAME = "PROJECT";
        public static final String NAME = "NAME";
        public static final String ICON = "ICON";
        public static final String VIDEO_ID = "VIDEO_ID";
    }

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + ProjectColumns.TABLE_NAME + " (" +
            Project.ProjectColumns._ID + " INTEGER PRIMARY KEY," +
            Project.ProjectColumns.NAME + " TEXT," +
            ProjectColumns.ICON + " BLOB" +
            ProjectColumns.VIDEO_ID + " TEXT" + ")";

    public static Project currentProject() {
        return currentProject;
    }

    public static void makeNewCurrentProject() {
        currentProject = new Project();
        currentProject.setAwayTeam(new Team());
        currentProject.setHomeTeam(new Team());
    }

    public void init() {
        if (this.miniScoreOverlay != null)
            miniScoreOverlay.init();
        if (this.commentOverlay != null)
            commentOverlay.init();
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public int getAwayTeamScore() {
        return awayTeamScore;
    }

    public void setAwayTeamScore(int awayTeamScore) {
        this.awayTeamScore = awayTeamScore;
    }

    public int getHomeTeamScore() {
        return homeTeamScore;
    }

    public void setHomeTeamScore(int homeTeamScore) {
        this.homeTeamScore = homeTeamScore;
    }

    public void setMiniScoreOverlay(MiniScoreOverlay miniScoreOverlay) {
        this.miniScoreOverlay = miniScoreOverlay;
    }

    public void setCommentOverlay(CommentOverlay commentOverlay) {
        this.commentOverlay = commentOverlay;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public byte[] getIcon() {
        return this.icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public String getVideoId() {
        return this.videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }

    public void setCommentIconType(CommentIconType commentIconType) {
        this.commentIconType = commentIconType;
    }

    public CommentIconType getCommentIconType() {
        return commentIconType;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void addFrameInfo(int time, FrameInfo frameInfo) {
        frameInfoHolder.put(time, frameInfo);
    }

    /*
    public FrameInfo getFrameInfo(int frameNumber) {
        return frameInfoHolder.get(frameNumber);
    }
    */

    public FrameInfo getCurrentVideoFrame() {
        return getNearestFrame(currentVideoFrame);
    }

    public void setCurrentVideoFrame(int time) {
        this.currentVideoFrame = time;
    }

    public FrameInfo getNearestFrame(int time) {
        if (frameInfoHolder.get(time) != null) {
            return frameInfoHolder.get(time);
        }
        Map.Entry<Integer,FrameInfo> low = frameInfoHolder.floorEntry(time);
        Map.Entry<Integer,FrameInfo> high = frameInfoHolder.ceilingEntry(time);
        FrameInfo result = null;
        if (low != null && high != null) {
            result = Math.abs(time-low.getKey()) < Math.abs(time-high.getKey())
                    ?   low.getValue()
                    :   high.getValue();
        } else if (low != null || high != null) {
            result = low != null ? low.getValue() : high.getValue();
        }
        return result;
    }

    public HashMap<Long, List<DrawingPath>> getDrawingPaths() {
        return drawingPaths;
    }

    public void setDrawingPaths(HashMap<Long, List<DrawingPath>> drawingPaths) {
        this.drawingPaths = drawingPaths;
    }
}
