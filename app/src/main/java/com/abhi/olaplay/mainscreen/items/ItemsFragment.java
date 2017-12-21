package com.abhi.olaplay.mainscreen.items;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abhi.olaplay.R;
import com.abhi.olaplay.model.api.SongItem;
import com.abhi.olaplay.mainscreen.items.ItemsContract.*;

import java.util.List;

/**
 * Fragment for displaying
 * song items
 *
 * Created by Abhishek on 12/17/2017.
 */

public class ItemsFragment extends Fragment
        implements ItemsContract.ItemsView {

    // key used to store data at which index from the list from SongViewModel is used for this fragment
    private static final String EXTRA_SONG_DATA_POSITION = "song_items_position";

    /**
     * used to create new instance for the fragment,
     *
     * @param dataPosition the index at which data for this fragment
     * @return
     */
    public static ItemsFragment newInstance(int dataPosition) {

        Bundle args = new Bundle();
        args.putInt(EXTRA_SONG_DATA_POSITION, dataPosition);

        ItemsFragment fragment = new ItemsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // stores the data position used for this fragment
    private int mDataPosition;
    private ItemsListener mListener;
    private SongItemAdapter mSongItemAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_list_fragment, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.itemRecyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        mSongItemAdapter = new SongItemAdapter();
        recyclerView.setAdapter(mSongItemAdapter);

        // getting the data position for this fragment
        // i.e. the position of data array from songViewModel
        // required for this fragment
        mDataPosition = getArguments().getInt(EXTRA_SONG_DATA_POSITION);

        // initializing the listener
        mListener = new ItemsPresenter(this, getActivity());
        mListener.init(mDataPosition);

        // setting listener for adapter item events
        mSongItemAdapter.setEventListener(mListener.getSongItemEventListener());
        return rootView;
    }

    /**
     * called to update views
     *
     * @param songItems the song data set
     */
    @Override
    public void updateView(List<SongItem> songItems) {
        mSongItemAdapter.setData(songItems);
    }
}
