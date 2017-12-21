package com.abhi.olaplay.mainscreen.player;

import android.net.Uri;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.abhi.olaplay.R;
import com.abhi.olaplay.model.api.SongItem;
import com.abhi.olaplay.utils.TextUtils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

/**
 * Presenter listens to player events
 * and manages playback
 *
 * Uses Exoplayer for playback
 *
 * Created by Abhishek on 12/18/2017.
 */

public class PlayerPresenter implements PlayerContract.PlayerListener {

    private final static String LOG_TAG = PlayerPresenter.class.getSimpleName();

    FragmentActivity mFragmentActivity;
    PlayerContract.PlayerView mPlayerView;
    Fragment mFragment;

    private SimpleExoPlayer mExoPlayer;
    private boolean mIsPlaying = false;

    private Player.DefaultEventListener eventListener = new Player.DefaultEventListener() {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            super.onPlayerStateChanged(playWhenReady, playbackState);

            Log.i(LOG_TAG, "onPlayerStateChanged: playWhenReady = " + String.valueOf(playWhenReady)
                    + " playbackState = " + playbackState);
            switch (playbackState) {
                case Player.STATE_ENDED:
                    Log.i(LOG_TAG, "Playback ended!");
                    //Stop playback and return to start position
                    closeClicked();
                    if (mExoPlayer != null)
                        mExoPlayer.seekTo(0);
                    break;
                case Player.STATE_READY:
                    // update seekbar duration for the fetched song
                    mPlayerView.setTrackDuration((int) mExoPlayer.getCurrentPosition(), (int) mExoPlayer.getDuration());
                    // is not paused
                    if (playWhenReady) {
                        mIsPlaying = true;
                        mPlayerView.setPlayPause(true);
                        mPlayerView.startSeekBarAutoProgress();
                    }
                    break;
                case Player.STATE_BUFFERING:
                    Log.i(LOG_TAG, "Playback buffering!");
                    break;
                case Player.STATE_IDLE:
                    Log.i(LOG_TAG, "ExoPlayer idle!");
                    break;
            }
        }
    };


    public PlayerPresenter(FragmentActivity activity, PlayerFragment playerFragment) {
        mFragmentActivity = activity;
        mPlayerView = playerFragment;
        mFragment = playerFragment;
    }

    /**
     * initialize exoPlayer
     */
    private void initPlayer() {
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(mFragmentActivity),
                new DefaultTrackSelector(),
                new DefaultLoadControl());

        mExoPlayer.setPlayWhenReady(true);
    }

    /**
     * Prepares exoplayer for audio playback from a remote URL audiofile. Should work with most
     * popular audiofile types (.mp3, .m4a,...)
     *
     * @param url Provide a Url in a form of "http://xxx/xxx.mp3"
     */
    private void prepareExoPlayerFromURL(String url) {

        mIsPlaying = false;

        Uri uri = Uri.parse(url);

        if (mExoPlayer == null)
            initPlayer();

        // used this particular constructor to
        // enable allowCrossProtocolRedirects
        // To handle shortened url's provided by the API
        DefaultHttpDataSourceFactory dataSourceFactory
                = new DefaultHttpDataSourceFactory("ua",
                null,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true);

        MediaSource audioSource = new ExtractorMediaSource(uri,
                dataSourceFactory,
                new DefaultExtractorsFactory(),
                null, null);


        mExoPlayer.addListener(eventListener);

        // start loading song and start playing when ready
        mExoPlayer.prepare(audioSource);

        mPlayerView.initMediaControls();
    }

    /**
     * close button from player clicked
     */
    @Override
    public void closeClicked() {
        mPlayerView.stopSeekBarAutoProgress();

        // Stop song
        if (mExoPlayer != null)
            mExoPlayer.stop();

        // Hide player
        BottomSheetBehavior<View> bottomSheetBehavior
                = BottomSheetBehavior
                .from(mFragmentActivity.findViewById(R.id.player_frag_container));
        if (BottomSheetBehavior.STATE_HIDDEN != bottomSheetBehavior.getState())
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        releasePlayer();
    }

    /**
     * release the player,
     * to prevent leaks
     */
    private void releasePlayer() {
        if (mExoPlayer != null) {
            mIsPlaying = false;
            mPlayerView.setTrackDuration(0, 0);
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    /**
     * user has changed seekbar value
     * seek song to new value
     * @param progressSecs
     */
    @Override
    public void progressChanged(int progressSecs) {
        mExoPlayer.seekTo(progressSecs * 1000);
    }

    /**
     * start playback for the supplied song
     * @param songItem
     */
    @Override
    public void playSong(SongItem songItem) {
        // play song
        prepareExoPlayerFromURL(songItem.getmSongUrl());

        // update UI
        mPlayerView.showSong(songItem);
    }

    /**
     * play/pause button clicked
     */
    @Override
    public void playPauseClicked() {
        mIsPlaying = !mIsPlaying;
        mPlayerView.setPlayPause(mIsPlaying);
        mExoPlayer.setPlayWhenReady(mIsPlaying);
        if (mIsPlaying)
            mPlayerView.startSeekBarAutoProgress();
    }

    /**
     * returns if currently playing
     * @return
     */
    @Override
    public boolean isPlaying() {
        return mExoPlayer != null && mIsPlaying;
    }
}
