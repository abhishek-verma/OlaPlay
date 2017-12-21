package com.abhi.olaplay.model.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.io.File;

/**
 * Databaase contract for saving Playlists
 */

public class PlaylistContract {

    public static final String CONTENT_AUTHORITY = "com.abhi.olaplay";
    public static final String PATH_PLAYLISTS = "playlists";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class PlaylistsEntry implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLAYLISTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_PLAYLISTS;

        // Table Name
        public static final String TABLE_NAME = "playlists";

        // Column names
        public static final String COLUMN_PLAYLIST_NAME = "playlist_name";
        public static final String COLUMN_SONG_ID = "playlist_song_id";

        public static String getPlaylistNameFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }

        public static Uri buildSongsByPlaylistUri(String playlist) {
            return CONTENT_URI.buildUpon()
                    .appendPath(playlist)
                    .build();
        }
    }
}
