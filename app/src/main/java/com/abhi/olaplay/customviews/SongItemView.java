package com.abhi.olaplay.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abhi.olaplay.R;
import com.abhi.olaplay.model.api.SongItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Custom view for displaying individual song item in main screen
 */
public class SongItemView extends FrameLayout {
    public boolean isStarred;
    @BindView(R.id.songTitle)
    public TextView mSongTitleTxtV;
    @BindView(R.id.songArtist)
    public TextView mSongArtistTxtV;
    @BindView(R.id.coverImgV)
    public FixedRatioImageView mCoverImgV;
    @BindView(R.id.songInfoParent)
    public RelativeLayout mSongInfoParent;
    @BindView(R.id.itemStarBtn)
    public ImageButton mItemStarBtn;
    @BindView(R.id.itemMenuBtn)
    public ImageButton mItemMenuBtn;
    private SongItem mSongItem;

    public SongItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.song_item_layout, this);

        ButterKnife.bind(this);
    }

    /**
     * Set songItem for this view,
     * updates the view according to the songItem
     *
     * @param songItem supplied song item
     * @param songStarred is song starred or not
     */
    public void setItem(SongItem songItem, boolean songStarred) {
        mSongItem = songItem;
        mSongTitleTxtV.setText(songItem.getmSongTitle());
        mSongArtistTxtV.setText(songItem.getmArtist());

        // Asynchronously loading cover image
        Glide.with(getContext())
                .load(songItem.getmCoverImageUrl())
                .asBitmap()
                .placeholder(R.drawable.cover_icon)
                .fallback(R.drawable.cover_icon)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        assert resource != null;
                        mCoverImgV.setImageBitmap(resource);

                        // Setting color of views according to color of the cover image
                        Palette.from(resource)
                                .generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(Palette palette) {
                                        Palette.Swatch textSwatch = palette.getLightMutedSwatch();
                                        if (textSwatch == null) {
                                            textSwatch = palette.getVibrantSwatch();
                                        }
                                        if (textSwatch == null) return;

                                        mSongInfoParent.setBackgroundColor(textSwatch.getRgb());
                                        mSongTitleTxtV.setTextColor(textSwatch.getTitleTextColor());
                                        mSongArtistTxtV.setTextColor(textSwatch.getBodyTextColor());
                                    }
                                });
                    }

                });


        setSongStarred(songStarred);
    }

    /**
     * stars/unstars the song displayed
     * @param songStarred
     */
    public void setSongStarred(boolean songStarred) {

        isStarred = songStarred;

        if (songStarred)
            mItemStarBtn.setImageDrawable(getContext()
                    .getResources()
                    .getDrawable(R.drawable.ic_stared_24dp));
        else
            mItemStarBtn.setImageDrawable(getContext()
                    .getResources()
                    .getDrawable(R.drawable.ic_unstarred_24dp));
    }

    /**
     * Method to change color of vector drawables programmatically
     *
     * @param imgV
     * @param color
     */
    public void setImageButtonVectorColors(ImageView imgV, int color) {
        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(color,
                PorterDuff.Mode.CLEAR);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawableCompat drawable = (VectorDrawableCompat) imgV.getDrawable();
            drawable.setColorFilter(porterDuffColorFilter);
            imgV.setImageDrawable(drawable);
        } else {
            VectorDrawable drawable = (VectorDrawable) imgV.getDrawable();
            drawable.setColorFilter(porterDuffColorFilter);
            imgV.setImageDrawable(drawable);
        }
    }
}
