package com.abhi.olaplay.mainscreen.player;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.abhi.olaplay.R;
import com.abhi.olaplay.model.api.SongItem;
import com.abhi.olaplay.utils.TextUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Abhishek on 12/18/2017.
 */

public class PlayerFragment extends Fragment implements PlayerContract.PlayerView {

    @BindView(R.id.playerParent)
    RelativeLayout mPlayerParent;
    @BindView(R.id.songTitle)
    TextView mTitleView;
    @BindView(R.id.playPauseBtn)
    ImageButton mPlayPauseBtn;
    @BindView(R.id.coverImgV)
    ImageView mImgV;
    @BindView(R.id.songArtist)
    TextView mArtistView;
    @BindView(R.id.currentTime)
    TextView mCurrentTimeView;
    @BindView(R.id.trackLength)
    TextView mTrackTimeView;
    @BindView(R.id.seekbar)
    AppCompatSeekBar mSeekBar;

    View mRootView;

    private PlayerContract.PlayerListener mListener;

    private Handler mSeekBarUpdateHandler;
    private Runnable mSeekBarUpdateRunnable;
    private int mCurrentTimeSecs;

    public static PlayerFragment newInstance() {

        Bundle args = new Bundle();

        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.player_fragment, container, false);
        ButterKnife.bind(this, mRootView);

        mListener = new PlayerPresenter(getActivity(), this);

        return mRootView;
    }

    @OnClick(R.id.closeBtn)
    void onClosePlayerClicked() {
        mListener.closeClicked();
    }

    @OnClick(R.id.playPauseBtn)
    void onPlayPauseBtnClicked() {
        mListener.playPauseClicked();
    }

    /**
     * show song on player view
     * @param songItem song item to show
     */
    @Override
    public void showSong(SongItem songItem) {
        mTitleView.setText(songItem.getmSongTitle());
        mArtistView.setText(songItem.getmArtist());
        mPlayPauseBtn.setImageResource(R.drawable.loading_animation);
        setTrackDuration(0, 0);

        // getting cover image
        if (getActivity() != null)
            Glide.with(getActivity())
                    .load(songItem.getmCoverImageUrl())
                    .asBitmap()
                    .placeholder(R.drawable.cover_icon)
                    .fallback(R.drawable.cover_icon)
                    .centerCrop()
                    .into(new BitmapImageViewTarget(mImgV) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory
                                            .create(getActivity().getResources(),
                                                    resource);
                            circularBitmapDrawable.setCircular(true);
                            mImgV.setImageDrawable(circularBitmapDrawable);


                            // Setting color of views according to color of the cover image
                            Palette.from(resource)
                                    .generate(new Palette.PaletteAsyncListener() {
                                        @Override
                                        public void onGenerated(Palette palette) {
                                            Palette.Swatch textSwatch = palette.getLightMutedSwatch();
                                            if (textSwatch == null) {
                                                textSwatch = palette.getVibrantSwatch();
                                            }
                                            if (textSwatch == null) return;

                                            mPlayerParent.setBackgroundColor(textSwatch.getRgb());
                                            mTitleView.setTextColor(textSwatch.getBodyTextColor());
                                            mArtistView.setTextColor(textSwatch.getBodyTextColor());
                                            mCurrentTimeView.setTextColor(textSwatch.getBodyTextColor());
                                            mTrackTimeView.setTextColor(textSwatch.getBodyTextColor());

                                            setSeekBarColor(textSwatch.getTitleTextColor());
                                        }
                                    });
                        }
                    });

    }

    /**
     * this method sets seekbar color
     * @param color to color
     */
    private void setSeekBarColor(int color) {

        mSeekBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        mSeekBar.getThumb().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    /**
     * method to be called from PlayerInterface
     * notifies listener about the song to be played
     * @param songItem song to be played
     */
    public void playSongFromInterface(SongItem songItem) {
        mListener.playSong(songItem);
    }

    /**
     * method to be called from PlayerInterface
     * notifies listener about for the song to be stopped
     */
    public void stopSongFromInterface() {
        mListener.closeClicked();
    }

    /**
     * initializes media player controls
     */
    @Override
    public void initMediaControls() {
        initPlayButton();
        initSeekBar();
    }

    private void initPlayButton() {
        mPlayPauseBtn.requestFocus();
    }

    /**
     * Starts or stops playback. Also takes care of the Play/Pause button toggling
     *
     * @param play True if playback should be started
     */
    @Override
    public void setPlayPause(boolean play) {
        if (!play) {
            mPlayPauseBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        } else {
            mPlayPauseBtn.setImageResource(R.drawable.ic_pause_black_24dp);
        }
    }

    /**
     * Sets the track duration for seekbar
     * @param currentPosition the current positoin of seekbar
     * @param duration total duration of song
     */
    @Override
    public void setTrackDuration(int currentPosition, final int duration) {
        mCurrentTimeSecs = currentPosition / 1000;
        mSeekBar.setProgress(mCurrentTimeSecs);
        mSeekBar.setMax(duration / 1000);
        mCurrentTimeView.setText(TextUtils.stringForTime(mCurrentTimeSecs));
        mTrackTimeView.setText(TextUtils.stringForTime(duration / 1000));
    }

    /**
     * Sahedules seekbar auto update
     */
    @Override
    public void startSeekBarAutoProgress() {
        if (mSeekBarUpdateHandler == null)
            mSeekBarUpdateHandler = new Handler();

        if (mSeekBarUpdateRunnable == null)
            mSeekBarUpdateRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mListener.isPlaying()) {
                        mCurrentTimeSecs++;
                        mSeekBar.setProgress(mCurrentTimeSecs);
                        mCurrentTimeView.setText(TextUtils.stringForTime(mCurrentTimeSecs));

                        mSeekBarUpdateHandler.postDelayed(this, 1000);
                    }
                }
            };

        //Make sure you update Seekbar on UI thread
        mSeekBarUpdateHandler.removeCallbacks(mSeekBarUpdateRunnable);
        mSeekBarUpdateHandler.post(mSeekBarUpdateRunnable);
    }

    /**
     * stops seekbar auto update
     */
    @Override
    public void stopSeekBarAutoProgress() {
        if (mSeekBarUpdateHandler != null)
            mSeekBarUpdateHandler.removeCallbacks(mSeekBarUpdateRunnable);
    }

    /**
     * initializes seekbar
     */
    private void initSeekBar() {
        mSeekBar.requestFocus();

        // seekbar event listener
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            /**
             * user is dragging seekbar
             * show to new progress position
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressSecs, boolean fromUser) {
                if (!fromUser) {
                    // We're not interested in programmatically generated changes to
                    // the progress bar's position.
                    return;
                }

                mCurrentTimeSecs = progressSecs;
                mCurrentTimeView.setText(TextUtils.stringForTime(progressSecs));
            }

            /**
             * user has started dragging seekbar
             * stop seekbar auto update
             * @param seekBar
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopSeekBarAutoProgress();
            }

            /**
             * user has finished dragging seekbar
             * notify listener
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mListener.progressChanged(mCurrentTimeSecs);
            }
        });

    }
}

