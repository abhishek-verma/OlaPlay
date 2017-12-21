package com.abhi.olaplay.mainscreen;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.abhi.olaplay.R;
import com.abhi.olaplay.mainscreen.player.PlayerInterface;
import com.abhi.olaplay.mainscreen.search.SearchFragment;
import com.abhi.olaplay.model.api.SongItem;
import com.bumptech.glide.Glide;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements MainContract.MainView {

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    @BindView(R.id.pager)
    ViewPager mPager;
    @BindView(R.id.viewpagertab)
    SmartTabLayout mViewPagerTab;

    MainContract.MainListener mListener;

    //The pager adapter, which provides the pages to the view pager widget.
    private ItemFragmentAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //setting up appbar
        setupAppBar();

        // Instantiate a ViewPager and a PagerAdapter.
        setupViewPager();

        // Adding search
        setupSearchView();

    }

    @Override
    protected void onResume() {
        super.onResume();

        mListener = new MainPresenter(this, this);
        mListener.init();

        // Adding Player
        setupPlayer();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        getSupportFragmentManager()
                .beginTransaction()
                .remove(mListener.getPlayerFragment())
                .commit();

        // leaving activity, destroy player
        PlayerInterface.destroyInstance();
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.filter_by_playlist:
                mListener.filterByPlaylistSelected();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void setupAppBar() {
        ImageView olaImgV = findViewById(R.id.ola_icon);
        Glide.with(this)
                .load(R.drawable.ola_icon)
                .into(olaImgV);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setupViewPager() {
        mPagerAdapter = new ItemFragmentAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    private void setupSearchView() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.searchFragmentContainer, SearchFragment.newInstance())
                .commit();
    }

    private void setupPlayer() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.player_frag_container, mListener.getPlayerFragment())
                .commit();

        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.player_frag_container));

        // player hidden initially
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN)
                        mListener.playerClosed();
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        }
    }

    /**
     * The method updates the view when data is received
     *
     * @param lists the songs data
     */
    @Override
    public void updateView(List<List<SongItem>> lists) {

        mPagerAdapter.setData(lists);
        mPager.setCurrentItem(0, false);

        // to update data of tab layout
        mViewPagerTab.setViewPager(mPager);
        ;
    }
}
