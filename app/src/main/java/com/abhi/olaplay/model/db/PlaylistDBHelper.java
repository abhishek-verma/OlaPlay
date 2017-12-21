package com.abhi.olaplay.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.abhi.olaplay.model.db.PlaylistContract.*;

/**
 * Db Helper to create table for storing pkaylist
 */
public class PlaylistDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "media.db";
    private static final String LOG_TAG = PlaylistDBHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;

    public PlaylistDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String SQL_CREATE_PLAYLISTS_TABLE = "CREATE TABLE " + PlaylistsEntry.TABLE_NAME + " ( " +
                PlaylistsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PlaylistsEntry.COLUMN_PLAYLIST_NAME + " TEXT NOT NULL, " +
                PlaylistsEntry.COLUMN_SONG_ID + " TEXT NOT NULL" +
                " )";

        Log.i(LOG_TAG, "onCreate: SQL Query" + SQL_CREATE_PLAYLISTS_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_PLAYLISTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // TODO implement when required
        // although not required in DATABASE_VERSION = 1
    }

}
