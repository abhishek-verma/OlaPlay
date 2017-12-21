package com.abhi.olaplay.model.db;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.abhi.olaplay.model.db.PlaylistContract.*;

public class PlaylistProvider extends ContentProvider {

    public static final String PLAYLIST_SORT_ORDER =
            PlaylistContract.PlaylistsEntry.COLUMN_PLAYLIST_NAME + " ASC";


    private static final int PLAYLISTS = 10;
    private static final int SONGS_BY_PLAYLIST = 20;

    // The URI Matcher used by this content provider
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private PlaylistDBHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PlaylistContract.CONTENT_AUTHORITY;

        matcher.addURI(authority,
                PlaylistContract.PATH_PLAYLISTS + "/*",
                SONGS_BY_PLAYLIST);
        matcher.addURI(authority,
                PlaylistContract.PATH_PLAYLISTS,
                PLAYLISTS);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new PlaylistDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case SONGS_BY_PLAYLIST:
                return PlaylistsEntry.CONTENT_TYPE;
            case PLAYLISTS:
                return PlaylistsEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor = null;

        switch (sUriMatcher.match(uri)) {

            case SONGS_BY_PLAYLIST:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        true,
                        PlaylistsEntry.TABLE_NAME,
                        projection,
                        PlaylistsEntry.COLUMN_PLAYLIST_NAME + "=?",
                        new String[]{PlaylistsEntry.getPlaylistNameFromUri(uri)},
                        null, null,
                        sortOrder, null
                );
                break;

            case PLAYLISTS:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        true,
                        PlaylistsEntry.TABLE_NAME,
                        projection,
                        null, null,
                        null, null,
                        sortOrder, null
                );

                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (retCursor != null) retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public synchronized Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {

            case PLAYLISTS: {
                long _id = db.insertWithOnConflict(PlaylistsEntry.TABLE_NAME,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (_id > 0)
                    returnUri = PlaylistsEntry.CONTENT_URI;
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case PLAYLISTS:
                rowsDeleted = db.delete(
                        PlaylistsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public synchronized int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;

        switch (match) {

            case PLAYLISTS: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(PlaylistsEntry.TABLE_NAME,
                                null,
                                value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1)
                            returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }

            default: {
                return super.bulkInsert(uri, values);
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }


    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
