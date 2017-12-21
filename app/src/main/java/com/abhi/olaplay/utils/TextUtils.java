package com.abhi.olaplay.utils;

import android.util.Log;

import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * contains several text utility functions
 * Created by Abhishek on 12/20/2017.
 */

public class TextUtils {

    public static final String PLAYLIST_IDENTIFIER = "PLAYLIST: ";

    /**
     * gets display string for time in seconds
     * @param timeSeconds time in seconds
     * @return display string
     */
    public static String stringForTime(int timeSeconds) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        int seconds = timeSeconds % 60;
        int minutes = (timeSeconds / 60) % 60;
        int hours   = timeSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * is search term a playlist search
     * @param s term
     * @return is playlist search
     */
    public static boolean isPlaylistSearch(String s) {
        return s.startsWith(PLAYLIST_IDENTIFIER);
    }

    /**
     * extract playlist name from a playlist search
     * @param s
     * @return
     */
    public static String getPlayListNameFromSearch(String s) {
        return s.replaceFirst("^"+PLAYLIST_IDENTIFIER, "");
    }
}
