package com.abhi.olaplay.customviews;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * This is a custom BottomSheetBehaviour class
 * to implement the behaviour of a bottom sheet
 * that collapses when clicked outside of it
 *
 * Taken from
 * https://stackoverflow.com/questions/38185902/
 * android-bottomsheet-how-to-collapse-when-clicked-outside
 * Created by Abhishek on 12/20/2017.
 */

public class AutoCloseBottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {

    public AutoCloseBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN &&
                getState() == BottomSheetBehavior.STATE_EXPANDED) {

            Rect outRect = new Rect();
            child.getGlobalVisibleRect(outRect);

            if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
        return super.onInterceptTouchEvent(parent, child, event);
    }
}
