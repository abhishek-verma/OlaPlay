package com.abhi.olaplay.mainscreen;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.abhi.olaplay.R;
import com.abhi.olaplay.mainscreen.player.PlayerInterface;
import com.abhi.olaplay.model.api.SongItem;
import com.abhi.olaplay.model.repositories.SearchRepo;
import com.abhi.olaplay.model.repositories.SongsViewModel;
import com.abhi.olaplay.playlist.SelectPlaylistDialogFragment;
import com.abhi.olaplay.utils.PermissionUtils;
import com.abhi.olaplay.utils.TextUtils;

import java.util.List;

/**
 * Created by Abhishek on 12/17/2017.
 */

public class MainPresenter implements MainContract.MainListener {

    private AppCompatActivity mActivity;
    private MainContract.MainView mMainView;
    private PlayerInterface mPlayerInterface;

    public MainPresenter(AppCompatActivity mActivity, MainContract.MainView mMainView) {
        this.mActivity = mActivity;
        this.mMainView = mMainView;
    }

    /**
     * initializes tasks of MainPresenter
     */
    @Override
    public void init() {

        PermissionUtils.getPermissions(mActivity);

        // creating instance of songs view model
        final SongsViewModel songsViewModel =
                ViewModelProviders
                        .of(mActivity)
                        .get(SongsViewModel.class);

        // subscrible to searchrepo, for if the searhc term changes, update the view
        SearchRepo searchRepo = SearchRepo.getInstance();
        searchRepo.getSearchTermObservable().observe(mActivity,
                new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        if (s == null || s.equals(""))
                            songsViewModel.clearFilters();
                        else if (TextUtils.isPlaylistSearch(s)) {
                            songsViewModel
                                    .filterByPlaylist(TextUtils.getPlayListNameFromSearch(s),
                                            mActivity);
                        } else
                            songsViewModel.filterBySearchString(s);
                    }
                });

        // initializing songs view model to retrieve data
        songsViewModel.init();

        mPlayerInterface = PlayerInterface.getInstance(mActivity);

        // subscribe to changes in song list received through API
        songsViewModel
                .getmSongItemsObservable()
                .observe(mActivity,
                        new Observer<List<List<SongItem>>>() {
                            @Override
                            public void onChanged(@Nullable List<List<SongItem>> lists) {

                                if (lists == null || lists.size() == 0)
                                    Snackbar
                                            .make(mActivity.findViewById(R.id.coordinatorLayout),
                                                    R.string.cannot_connect_to_internet,
                                                    Snackbar.LENGTH_INDEFINITE)
                                            .setAction(R.string.retry, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    songsViewModel.init();
                                                }
                                            })
                                            .show();

                                // Update the view when data received
                                mMainView.updateView(lists);
                            }
                        }
                );
    }

    /**
     * filter by playlist option selected
     * <p>
     * shows a dialog to choose a playlist
     * then show only the songs in that playlist
     */
    @Override
    public void filterByPlaylistSelected() {
        new SelectPlaylistDialogFragment
                .Builder()
                .show(mActivity.getFragmentManager());
    }

    @Override
    public Fragment getPlayerFragment() {
        return mPlayerInterface.getPlayerFragment();
    }

    @Override
    public void playerClosed() {
        mPlayerInterface.stopSong();
    }
}
