package com.abhi.olaplay.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.abhi.olaplay.model.api.SongItem;

/**
 * Created by Abhishek on 12/20/2017.
 */

public class DownloadUtil {

    /**
     * Method to download a song
     * @param songItem song
     * @param context
     */
    public static void downloadSong(SongItem songItem, Context context) {

        Uri music_uri = Uri.parse(songItem.getmSongUrl());

        DownloadManager.Request request = new DownloadManager.Request(music_uri);
        request.setTitle("Downloading " + songItem.getmSongTitle());
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, songItem.getmSongTitle()+".mp3");

        // getting download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        assert manager != null;
        manager.enqueue(request);
    }

    /**
     * method to show downloads
     * @param context
     */
    public static void showDownloads(Context context) {
        Intent dm = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        dm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dm);
    }
}
