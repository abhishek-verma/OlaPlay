package com.abhi.olaplay.customviews;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.abhi.olaplay.R;

/**
 * CustomImage view which retains a certain height width ratio,
 * used in this app to display square image
 */

public class FixedRatioImageView extends AppCompatImageView {
    private static final float HEIGHT_TO_WIDTH_RATIO = 1; // height/width
    private static final int FADING_EDGE_LEN = 20;

    public FixedRatioImageView(Context context) {
        super(context);
        init();
    }

    public FixedRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FixedRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        // Enable horizontal fading
        this.setHorizontalFadingEdgeEnabled(true);
    }

    @Override
    protected float getLeftFadingEdgeStrength() {
        return 1f;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setClipToOutline(true);
        }

        // getting height and setting that as width
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = (int) (height * HEIGHT_TO_WIDTH_RATIO);

        this.setFadingEdgeLength(width/2);

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }
}
