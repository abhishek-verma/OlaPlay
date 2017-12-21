package com.abhi.olaplay.mainscreen;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.abhi.olaplay.mainscreen.items.ItemsFragment;
import com.abhi.olaplay.model.api.SongItem;

import java.util.List;

/**
 *
 * Simple Adapter for displaying fragment in main activity
 *
 * Created by Abhishek on 12/17/2017.
 */

public class ItemFragmentAdapter extends FragmentStatePagerAdapter {

    private List<List<SongItem>> mSongItemsList;

    public ItemFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setData(List<List<SongItem>> songItemsList) {
        mSongItemsList = songItemsList;
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Integer.toString(position+1);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    @Override
    public Fragment getItem(int position) {
        return ItemsFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return mSongItemsList==null? 0 : mSongItemsList.size();
    }
}
