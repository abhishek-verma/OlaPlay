package com.abhi.olaplay.mainscreen;

import android.support.v4.app.Fragment;

import com.abhi.olaplay.model.api.SongItem;

import java.util.List;

/**
 * Created by Abhishek on 12/17/2017.
 */

public interface MainContract {

    interface MainView {

        void updateView(List<List<SongItem>> lists);
    }

    interface MainListener {

        void init();

        Fragment getPlayerFragment();

        void playerClosed();

        void filterByPlaylistSelected();
    }
}
