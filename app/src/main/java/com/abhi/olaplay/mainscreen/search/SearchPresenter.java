package com.abhi.olaplay.mainscreen.search;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.abhi.olaplay.model.repositories.SearchRepo;
import com.abhi.olaplay.utils.TextUtils;

/**
 * Created by Abhishek on 12/17/2017.
 */

public class SearchPresenter implements SearchContract.SearchListener {

    SearchContract.SearchView mView;
    SearchRepo mSearchRepo;
    boolean mSelfChanged = false;

    public SearchPresenter(final SearchContract.SearchView mView, FragmentActivity activity) {
        this.mView = mView;
        mSearchRepo = SearchRepo.getInstance();
        /**
         * observe value of the search repo
         */
        mSearchRepo.getSearchTermObservable().observe(activity, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (!mSelfChanged) { // changed by app
                    mView.setText(s);
                } else { // changed by user
                    mSelfChanged = false;
                }
            }
        });
    }

    /**
     * search term changed
     * @param s term
     */
    @Override
    public void onTextChanged(String s) {
        mView.highlightText(TextUtils.isPlaylistSearch(s));

        // if same as before
        if (mSearchRepo.getSearchTermObservable().getValue().equals(s)) {
            return;
        }

        mSelfChanged = true;
        mSearchRepo.setSearchTerm(s);


    }

    /**
     * clar bt clicked
     */
    @Override
    public void onClearClicked() {
        if (mSearchRepo.getSearchTermObservable().getValue().equals(""))
            return;

        mSearchRepo.setSearchTerm("");
        mView.clear();
    }
}
