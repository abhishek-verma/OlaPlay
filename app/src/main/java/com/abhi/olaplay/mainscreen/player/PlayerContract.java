package com.abhi.olaplay.mainscreen.player;

import com.abhi.olaplay.model.api.SongItem;

/**
 * Created by Abhishek on 12/18/2017.
 */

interface PlayerContract {

    interface PlayerView {

        void showSong(SongItem songItem);

        void initMediaControls();

        void setTrackDuration(int currentPosition, int duration);

        void setPlayPause(boolean playing);

        void startSeekBarAutoProgress();

        void stopSeekBarAutoProgress();
    }

    interface PlayerListener {

        void closeClicked();

        void progressChanged(int progress);

        void playSong(SongItem songItem);

        boolean isPlaying();

        void playPauseClicked();
    }
}
