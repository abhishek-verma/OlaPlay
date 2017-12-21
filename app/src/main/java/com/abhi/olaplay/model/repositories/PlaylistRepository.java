package com.abhi.olaplay.model.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;
import android.widget.Toast;

import com.abhi.olaplay.R;
import com.abhi.olaplay.model.db.PlaylistContract.PlaylistsEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * this class is an interface to access playlist stored in db
 *
 * functions are self explanatory
 *
 * Created by Abhishek on 12/18/2017.
 */
public class PlaylistRepository {

    private static final String LOG_TAG = PlaylistRepository.class.getSimpleName();

    private final static int COL_INDEX_PLAYLIST_NAME = 0;
    private final static int COL_INDEX_SONG_ID = 1;

    private static final String[] projection = {
            PlaylistsEntry.COLUMN_PLAYLIST_NAME,
            PlaylistsEntry.COLUMN_SONG_ID
    };

    private static final String STARRED_PLAYLIST = "Starred";


    public static PlaylistRepository mPlaylistRepositoryInstance;
    Context mApplicationContext;

    private PlaylistRepository(Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    public static PlaylistRepository getInstance(Context applicationContext) {
        if (mPlaylistRepositoryInstance == null)
            mPlaylistRepositoryInstance = new PlaylistRepository(applicationContext);

        return mPlaylistRepositoryInstance;
    }

    public List<String> getPlaylists() {

        Cursor playlistsDataCursor = mApplicationContext
                .getContentResolver()
                .query(PlaylistsEntry.CONTENT_URI,
                        new String[]{PlaylistsEntry.COLUMN_PLAYLIST_NAME},
                        null, null, null);

        List<String> playlists = new ArrayList<>();

        if (playlistsDataCursor != null && playlistsDataCursor.moveToFirst()) {
            do {
                String curSong = playlistsDataCursor.getString(0);
                playlists.add(curSong);
            } while (playlistsDataCursor.moveToNext());
        }

        if (playlistsDataCursor != null) {
            playlistsDataCursor.close();
        }

        return playlists;
    }

    public List<String> getSongsInPlaylist(String playlist) {

        Cursor songsDataCursor = mApplicationContext
                .getContentResolver()
                .query(PlaylistsEntry.buildSongsByPlaylistUri(playlist),
                        new String[]{PlaylistsEntry.COLUMN_SONG_ID},
                        null, null,
                        null, null);

        List<String> songIds = new ArrayList<>();

        if (songsDataCursor != null && songsDataCursor.moveToFirst()) {
            do {

                songIds.add(songsDataCursor.getString(0));
            } while (songsDataCursor.moveToNext());
        }

        if (songsDataCursor != null) {
            songsDataCursor.close();
        }

        return songIds;
    }

    public void removeSongFromPlaylist(String songId, String playlist) {

        // removing from playlist
        mApplicationContext.getContentResolver().delete(PlaylistsEntry.CONTENT_URI,
                PlaylistsEntry.COLUMN_SONG_ID + " = ? " +
                        "AND " + PlaylistsEntry.COLUMN_PLAYLIST_NAME +
                        " = ? ",
                new String[]{songId, playlist});
    }

    public void toggleSongStar(String songId, boolean star) {

        // unstar
        if (!star) {
            // removing from starred
            mApplicationContext.getContentResolver().delete(PlaylistsEntry.CONTENT_URI,
                    PlaylistsEntry.COLUMN_SONG_ID + " = ? " +
                            "AND " + PlaylistsEntry.COLUMN_PLAYLIST_NAME +
                            " = ? ",
                    new String[]{songId, STARRED_PLAYLIST});
        } else {
            ContentValues cv = new ContentValues();
            cv.put(PlaylistsEntry.COLUMN_SONG_ID, songId);
            cv.put(PlaylistsEntry.COLUMN_PLAYLIST_NAME, STARRED_PLAYLIST);

            mApplicationContext.getContentResolver().insert(PlaylistsEntry.CONTENT_URI, cv);
        }
    }

    public boolean isSongStarred(String songId) {
        List<String> starredSongs = getSongsInPlaylist(STARRED_PLAYLIST);
        return starredSongs.contains(songId);
    }

    /**
     * @param songIds  song ids
     * @param playlist playlist name to which the songs need to be added to
     */
    public void addSongsToPlaylist(List<String> songIds, String playlist) {

        Vector<ContentValues> cvVector = new Vector<>(songIds.size());
        ContentValues cv;
        //adding new Last Played
        for (String songId : songIds) {
            cv = new ContentValues();
            cv.put(PlaylistsEntry.COLUMN_PLAYLIST_NAME, playlist);
            cv.put(PlaylistsEntry.COLUMN_SONG_ID, songId);

            cvVector.add(cv);
        }

        int insertedCnt;

        // adding to db
        if (cvVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cvVector.size()];
            cvVector.toArray(cvArray);
            try {
                insertedCnt = mApplicationContext
                        .getContentResolver()
                        .bulkInsert(PlaylistsEntry.CONTENT_URI, cvArray);

                Toast
                        .makeText(mApplicationContext,
                                mApplicationContext.getString(R.string.song_added_to_playlist),
                                Toast.LENGTH_SHORT)
                        .show();
                Log.d(LOG_TAG, insertedCnt + " items inserted in playlist " + playlist);
            } catch (SQLiteConstraintException exception) {
                // try removing this try-catch to find the error
                Log.e(LOG_TAG, "Cannot insert into database! ERROR: " + exception);
            }
        }
    }

}
