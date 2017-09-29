package me.gavin.photo.viewer.test;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchUIUtil;
import android.view.MotionEvent;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/9/27
 */
public class Test {

    ItemTouchHelper a;
    ItemTouchUIUtil b;
    RecyclerView c;

    void test() {
        c.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }
}
