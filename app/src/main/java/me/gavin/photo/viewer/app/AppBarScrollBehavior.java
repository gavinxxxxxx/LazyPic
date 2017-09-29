package me.gavin.photo.viewer.app;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import me.gavin.photo.viewer.app.base.DisplayUtil;
import me.gavin.photo.viewer.app.base.L;

public class AppBarScrollBehavior extends CoordinatorLayout.Behavior<View> {

    private boolean flingFlag;

    public AppBarScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        flingFlag = false;
        float tranY = child.getTranslationY() - dyConsumed;
        child.setTranslationY(dyConsumed < 0 ? Math.min(tranY, 0) : Math.max(tranY, -child.getHeight()));
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY, boolean consumed) {
        if (velocityY > 2000) {
            flingFlag = true;
            child.animate().translationY(-child.getHeight()).setDuration(200).setInterpolator(new DecelerateInterpolator());
        } else if (velocityY < -2000) {
            flingFlag = true;
            child.animate().translationY(0).setDuration(200).setInterpolator(new DecelerateInterpolator());
        }
        return false;
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) ((RecyclerView) target).getLayoutManager();
        if (layoutManager.findFirstVisibleItemPosition() == 0) {
            View firstItem = layoutManager.findViewByPosition(0);
            if (firstItem.getTop() > DisplayUtil.getStatusHeight() + DisplayUtil.dp2px(4)) {
                flingFlag = true;
                child.animate().translationY(0).setDuration(200);
            }
        }
        if (!flingFlag) {
            L.e(flingFlag);
            child.animate().translationY(child.getHeight() / 2 + child.getTranslationY() > 0 ? 0 : -child.getHeight()).setDuration(200);
        }
    }

}