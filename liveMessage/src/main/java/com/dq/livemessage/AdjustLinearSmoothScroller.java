package com.dq.livemessage;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class AdjustLinearSmoothScroller extends LinearSmoothScroller {

    public float duration = 300f; //越小越快

    public AdjustLinearSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return duration / displayMetrics.densityDpi;
    }

    @Override
    protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
        final int dy = calculateDyToMakeVisible(targetView, getVerticalSnapPreference());
        final int time = calculateTimeForDeceleration(dy);
        if (time > 0) {
            action.update(0, -dy, time, mDecelerateInterpolator);
        }
    }

    public void setDuration(float milliseconds) {
        duration = milliseconds;
    }

}
