package com.abhi.olaplay.mainscreen.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.abhi.olaplay.R;
import com.abhi.olaplay.customviews.SongItemView;
import com.abhi.olaplay.model.api.SongItem;
import com.abhi.olaplay.model.repositories.PlaylistRepository;

import java.util.List;

/**
 * song item adapter for displaying song items inside list fragment
 *
 * Created by Abhishek on 12/17/2017.
 */

public class SongItemAdapter extends RecyclerView.Adapter<SongItemAdapter.ViewHolder> {

    private List<SongItem> mSongItemList;
    private SongItemEventListener mListener;

    public void setData(List<SongItem> songItemList) {
        mSongItemList = songItemList;
        notifyDataSetChanged();
    }

    public void setEventListener(SongItemEventListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new SongItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mSongItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return mSongItemList!=null ? mSongItemList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        SongItemView mSongItemView;

        ViewHolder(View itemView) {
            super(itemView);
            mSongItemView = (SongItemView) itemView;
        }

        void bind(final SongItem item) {
            mSongItemView.setItem(item, PlaylistRepository.getInstance(mSongItemView.getContext()).isSongStarred(item.getmSongUrl()));
            mSongItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onClicked(getAdapterPosition());
                }
            });
            mSongItemView.mItemStarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSongItemView.setSongStarred(!mSongItemView.isStarred);
                    mListener.onStarClicked(getAdapterPosition(), mSongItemView.isStarred);
                }
            });
            mSongItemView.mItemMenuBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onMenuClicked(getAdapterPosition());
                }
            });
        }

    }

    interface SongItemEventListener {

        void onClicked(int position);

        void onMenuClicked(int position);

        void onStarClicked(int position, boolean starred);

    }
}
