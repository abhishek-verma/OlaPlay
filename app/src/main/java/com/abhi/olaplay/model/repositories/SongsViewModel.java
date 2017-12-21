package com.abhi.olaplay.model.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.abhi.olaplay.model.api.ApiClient;
import com.abhi.olaplay.model.api.OlaPlayApiService;
import com.abhi.olaplay.model.api.SongItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class provides a modern, flexible, and testable solution
 * for Lifecycle Aware Data Loading
 * <p>
 * Created by Abhishek on 12/17/2017.
 */
public class SongsViewModel extends ViewModel {

    private static final String LOG_TAG = SongsViewModel.class.getSimpleName();
    private static final int SONGS_PER_PAGE = 4;

    private MutableLiveData<List<List<SongItem>>> mSongItemsObservable;
    private List<SongItem> mSongItemList;

    /**
     * returns an observable, on which we can observe data changes
     *
     * @return an observable on mSongItemsObservable
     */
    public MutableLiveData<List<List<SongItem>>> getmSongItemsObservable() {
        if (mSongItemsObservable == null) {
            mSongItemsObservable = new MutableLiveData<>();
            init();
        }

        return mSongItemsObservable;
    }

    /**
     * Method initializes data from Ola Api asynchronously
     */
    public void init() {
        OlaPlayApiService apiService = ApiClient.getClient().create(OlaPlayApiService.class);
        Call<List<SongItem>> call = apiService.getSongList();
        call.enqueue(new Callback<List<SongItem>>() {
            @Override
            public void onResponse(Call<List<SongItem>> call, Response<List<SongItem>> response) {
                if (response.isSuccessful()) {
                    List<SongItem> songItems = response.body();
                    mSongItemList = songItems;

                    List<List<SongItem>> partitions = new ArrayList<>();
                    for (int i = 0; i < songItems.size(); i += SONGS_PER_PAGE) {
                        partitions.add(songItems.subList(i,
                                Math.min(i + SONGS_PER_PAGE, songItems.size())));
                    }

                    mSongItemsObservable.setValue(partitions);

                } else {
                    List<List<SongItem>> emptyList = new ArrayList<>();
                    mSongItemsObservable.setValue(emptyList);
                    Log.e(LOG_TAG, response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<List<SongItem>> call, Throwable t) {
                t.printStackTrace();

                List<List<SongItem>> emptyList = new ArrayList<>();
                mSongItemsObservable.setValue(emptyList);
            }
        });
    }

    public void filterBySearchString(final String searchTerm) {

        Runnable task = new Runnable() {
            @Override
            public void run() {

                List<SongItem> filteredSongList = new ArrayList<>(mSongItemList);

                // filter only if search term is non empty
                if (searchTerm != null && !searchTerm.equals("")) {

                    // removing terms which do not contain the term
                    for (SongItem item : mSongItemList) {
                        if (!item.getmSongTitle().toLowerCase().contains(searchTerm.toLowerCase()))
                            filteredSongList.remove(item);
                    }
                }

                int partitionSize = 4;
                final List<List<SongItem>> partitions = new ArrayList<>();
                for (int i = 0; i < filteredSongList.size(); i += partitionSize) {
                    partitions.add(filteredSongList.subList(i,
                            Math.min(i + partitionSize, filteredSongList.size())));
                }

                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mSongItemsObservable.setValue(partitions);
                    }
                });
            }
        };

        Thread t = new Thread(task);
        t.start();
    }


    public void filterByPlaylist(final String playlist, final Context context) {

        Runnable task = new Runnable() {
            @Override
            public void run() {

                List<String> songsInPlaylist = PlaylistRepository
                        .getInstance(context)
                        .getSongsInPlaylist(playlist);

                List<SongItem> filteredSongList;

                if(songsInPlaylist.size() > 0) {
                    filteredSongList = new ArrayList<>(mSongItemList);

                    // removing items which do not contain the term
                    for (SongItem item : mSongItemList) {
                        if (!songsInPlaylist.contains(item.getmSongUrl()))
                            filteredSongList.remove(item);
                    }
                } else {
                    filteredSongList = new ArrayList<>();
                }

                int partitionSize = 4;
                final List<List<SongItem>> partitions = new ArrayList<>();
                for (int i = 0; i < filteredSongList.size(); i += partitionSize) {
                    partitions.add(filteredSongList.subList(i,
                            Math.min(i + partitionSize, filteredSongList.size())));
                }

                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mSongItemsObservable.setValue(partitions);
                    }
                });
            }

        };

        Thread t = new Thread(task);
        t.start();
    }

    public void clearFilters() {

        if (mSongItemList == null)
            return;

        Runnable task = new Runnable() {
            @Override
            public void run() {

                int partitionSize = 4;
                final List<List<SongItem>> partitions = new ArrayList<>();
                for (int i = 0; i < mSongItemList.size(); i += partitionSize) {
                    partitions.add(mSongItemList.subList(i,
                            Math.min(i + partitionSize, mSongItemList.size())));
                }

                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mSongItemsObservable.setValue(partitions);
                    }
                });
            }

        };

        Thread t = new Thread(task);
        t.start();
    }
}
