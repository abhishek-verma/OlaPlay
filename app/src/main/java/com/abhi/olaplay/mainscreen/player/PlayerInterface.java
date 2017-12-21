package com.abhi.olaplay.mainscreen.player;

import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.abhi.olaplay.R;
import com.abhi.olaplay.model.api.SongItem;
import com.google.android.exoplayer2.Player;

/**
 * PlayerInterface is used to access PlayerFragment
 * to play a song and stop playback
 * Created by Abhishek on 12/20/2017.
 */

public class PlayerInterface {

    private static PlayerInterface mPlayerInterfaceInstance;
    FragmentActivity mActivity;
    private PlayerFragment mPlayerFragment;

    private PlayerInterface(FragmentActivity mActivity) {
        this.mActivity = mActivity;

        mPlayerFragment = PlayerFragment.newInstance();
    }

    public static PlayerInterface getInstance(FragmentActivity activity) {
        if (mPlayerInterfaceInstance == null)
            mPlayerInterfaceInstance = new PlayerInterface(activity);

        return mPlayerInterfaceInstance;
    }

    /**
     * stops playback and destroys instance
     */
    public static void destroyInstance() {
        if (mPlayerInterfaceInstance == null) return;

        mPlayerInterfaceInstance.stopSong();
        mPlayerInterfaceInstance = null;
    }

    /**
     * plays supplied song
     * @param item song
     */
    public void playSong(SongItem item) {

        BottomSheetBehavior<View> bottomSheetBehavior
                = BottomSheetBehavior
                .from(mActivity.findViewById(R.id.player_frag_container));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        mPlayerFragment.playSongFromInterface(item);
    }

    /**
     * stops playback
     */
    public void stopSong() {
        mPlayerFragment.stopSongFromInterface();
    }

    /**
     * @return the player fragment
     */
    public Fragment getPlayerFragment() {
        return mPlayerFragment;
    }


}
