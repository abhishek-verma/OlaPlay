package com.abhi.olaplay.mainscreen.items;

import android.arch.lifecycle.ViewModelProviders;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.abhi.bottomslidingdialog.BottomSlidingDialog;
import com.abhi.olaplay.R;
import com.abhi.olaplay.mainscreen.player.PlayerInterface;
import com.abhi.olaplay.model.api.SongItem;
import com.abhi.olaplay.model.repositories.PlaylistRepository;
import com.abhi.olaplay.model.repositories.SongsViewModel;
import com.abhi.olaplay.playlist.AddToPlaylistDialogFragment;
import com.abhi.olaplay.playlist.RemoveFromPlaylistDialogFragment;
import com.abhi.olaplay.utils.DownloadUtil;

import java.util.List;

/**
 * Created by Abhishek on 12/17/2017.
 */

public class ItemsPresenter implements ItemsContract.ItemsListener {

    private ItemsContract.ItemsView mItemsView;
    private FragmentActivity mActivity;
    private int mDataPosition;
    private PlayerInterface mPlayerInterface;

    public ItemsPresenter(ItemsContract.ItemsView mItemsView, FragmentActivity mActivity) {
        this.mItemsView = mItemsView;
        this.mActivity = mActivity;
    }

    /**
     * initializing Presenter
     * getting required data
     * calling method to show the data
     *
     * @param dataPosition the data position to retrieve data required by this fragment
     */
    @Override
    public void init(int dataPosition) {

        mDataPosition = dataPosition;

        // getting instance of the SongsViewModel
        SongsViewModel songsViewModel =
                ViewModelProviders
                        .of(mActivity)
                        .get(SongsViewModel.class);

        // getting instance of playerInterface, to play song when required
        mPlayerInterface = PlayerInterface.getInstance(mActivity);

        // retrieving data
        List<List<SongItem>> songItemsList = songsViewModel.getmSongItemsObservable().getValue();

        // if data is present, update view to show it
        if (songItemsList != null && songItemsList.size() > dataPosition)
            mItemsView.updateView(songItemsList.get(dataPosition));
    }

    /**
     * listener for event of individual items inside SongItem RecyclerView
     *
     * @return
     */
    @Override
    public SongItemAdapter.SongItemEventListener getSongItemEventListener() {
        return new SongItemAdapter.SongItemEventListener() {
            /**
             * song clicked
             * play song
             * @param position position of clicked song
             */
            @Override
            public void onClicked(int position) {

                SongsViewModel songsViewModel =
                        ViewModelProviders
                                .of(mActivity)
                                .get(SongsViewModel.class);

                List<List<SongItem>> songItemsList = songsViewModel.getmSongItemsObservable().getValue();

                if (songItemsList != null && songItemsList.size() > mDataPosition) {

                    final SongItem songItem = songItemsList
                            .get(mDataPosition)
                            .get(position);

                    mPlayerInterface.playSong(songItem);
                }
            }

            /**
             * Menu option clicked for song,
             * show options menu
             * @param position position of clicked song
             */
            @Override
            public void onMenuClicked(int position) {

                // getting model instance
                SongsViewModel songsViewModel =
                        ViewModelProviders
                                .of(mActivity)
                                .get(SongsViewModel.class);

                // getting data
                List<List<SongItem>> songItemsList = songsViewModel.getmSongItemsObservable().getValue();

                // if data is present
                if (songItemsList != null && songItemsList.size() > mDataPosition) {

                    // get required song item
                    final SongItem songItem = songItemsList
                            .get(mDataPosition)
                            .get(position);

                    // showing options menu
                    BottomSlidingDialog.build(mActivity)
                            .addAction(R.string.download, R.drawable.ic_arrow_downward, 1)
                            .addAction(R.string.add_to_playlist, R.drawable.ic_add_24dp, 2)
                            .addAction(R.string.remove_from_playlist, R.drawable.ic_remove_24dp, 3)
                            .setActionListener(new BottomSlidingDialog.ActionListener() {
                                @Override
                                public void onActionSelected(int actionId) {
                                    switch (actionId) {
                                        case 1: // download option selected:
                                            DownloadUtil.downloadSong(songItem, mActivity);

                                            Snackbar snackbar = Snackbar
                                                    .make(mActivity.findViewById(R.id.coordinatorLayout),
                                                            R.string.download_started,
                                                            Snackbar.LENGTH_INDEFINITE);
                                            snackbar.setAction(R.string.show_downloads,
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            DownloadUtil.showDownloads(mActivity);
                                                        }
                                                    });
                                            snackbar.show();

                                            break;
                                        case 2: // add to playlist option selected

                                            // show a dialog with list of playlist's with add button
                                            // add the song to the returned playlist by calling
                                            // songsRepo.addToPlaylist(songMetadata, playlistName)
                                            new AddToPlaylistDialogFragment
                                                    .Builder(songItem.getmSongUrl())
                                                    .show(mActivity.getFragmentManager());
                                            break;
                                        case 3: // remove song from playlist

                                            // show a dialog with list of playlist's
                                            // remove the song from the returned playlist by calling
                                            // songsRepo.removeFromPlaylist(songMetadata, playlistName)
                                            new RemoveFromPlaylistDialogFragment
                                                    .Builder(songItem.getmSongUrl())
                                                    .show(mActivity.getFragmentManager());
                                            break;
                                    }
                                }
                            })
                            .show();
                }
            }

            /**
             * star button clicked for song
             * @param position positon of song
             * @param starred is Star enabled or disabled
             */
            @Override
            public void onStarClicked(int position, boolean starred) {

                SongsViewModel songsViewModel =
                        ViewModelProviders
                                .of(mActivity)
                                .get(SongsViewModel.class);

                List<List<SongItem>> songItemsList = songsViewModel.getmSongItemsObservable().getValue();

                if (songItemsList != null && songItemsList.size() > mDataPosition) {

                    // update database with new value
                    PlaylistRepository
                            .getInstance(mActivity.getApplicationContext())
                            .toggleSongStar(
                                    songItemsList
                                            .get(mDataPosition)
                                            .get(position)
                                            .getmSongUrl(),
                                    starred);
                }
            }
        };
    }
}
