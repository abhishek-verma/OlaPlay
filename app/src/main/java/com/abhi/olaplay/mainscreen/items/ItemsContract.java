package com.abhi.olaplay.mainscreen.items;

import com.abhi.olaplay.model.api.SongItem;

import java.util.List;

/**
 * Created by Abhishek on 12/17/2017.
 */

public interface ItemsContract {

    interface ItemsView {

        void updateView(List<SongItem> lists);
    }

    interface ItemsListener {

        void init(int dataPosition);

        SongItemAdapter.SongItemEventListener getSongItemEventListener();
    }
}
