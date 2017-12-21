package com.abhi.olaplay.model.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Song item class for storing data for individual song
 * Created by Abhishek on 12/17/2017.
 */

public class SongItem {

    @SerializedName("song")
    private String mSongTitle;

    @SerializedName("artists")
    private String mArtist;

    @SerializedName("url")
    private String mSongUrl;

    @SerializedName("cover_image")
    private String mCoverImageUrl;

    public SongItem(String mSongTitle, String mArtist, String mSongUrl, String mCoverImageUrl) {
        this.mSongTitle = mSongTitle;
        this.mArtist = mArtist;
        this.mSongUrl = mSongUrl;
        this.mCoverImageUrl = mCoverImageUrl;
    }

    public String getmSongTitle() {
        return mSongTitle;
    }

    public void setmSongTitle(String mSongTitle) {
        this.mSongTitle = mSongTitle;
    }

    public String getmArtist() {
        return mArtist;
    }

    public void setmArtist(String mArtist) {
        this.mArtist = mArtist;
    }

    public String getmSongUrl() {
        return mSongUrl;
    }

    public void setmSongUrl(String mSongUrl) {
        this.mSongUrl = mSongUrl;
    }

    public String getmCoverImageUrl() {
        return mCoverImageUrl;
    }

    public void setmCoverImageUrl(String mCoverImageUrl) {
        this.mCoverImageUrl = mCoverImageUrl;
    }

    /**
     * two songs equal only id url equal
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != SongItem.class)
            return false;

        SongItem other = (SongItem) obj;
        return getmSongUrl().equals(other.getmSongUrl());
    }
}
