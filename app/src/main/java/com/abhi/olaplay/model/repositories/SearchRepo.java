package com.abhi.olaplay.model.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

/**
 * Repository to store search term
 *
 * Created by Abhishek on 12/17/2017.
 */

public class SearchRepo {

    private static SearchRepo mSearchRepoInstance;

    public static SearchRepo getInstance() {
        if (mSearchRepoInstance == null)
            mSearchRepoInstance = new SearchRepo();

        return mSearchRepoInstance;
    }

    private MutableLiveData<String> mSearchTermObservable;

    public SearchRepo() {
        mSearchTermObservable = new MutableLiveData<>();
        mSearchTermObservable.setValue("");
    }

    public MutableLiveData<String> getSearchTermObservable() {
        return mSearchTermObservable;
    }

    public void setSearchTerm(String searchTerm) {
        mSearchTermObservable.setValue(searchTerm);
    }
}
