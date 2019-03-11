package tzy.refreshlayout.scroller;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import tzy.refreshlayout.Scroller;

public class RecyclerViewScroller implements Scroller<RecyclerView> {
    final RecyclerView mRecyclerView;


    public RecyclerViewScroller(View target) {
        mRecyclerView = target instanceof RecyclerView && ViewCompat.isNestedScrollingEnabled(target) ? (RecyclerView) target : null;
    }

    @Override
    public RecyclerView ensureTargetScrollChild(View target) {
        return mRecyclerView;
    }

    @Override
    public void scrollBy(int dy) {
        if (mRecyclerView != null) {
            mRecyclerView.scrollBy(0, dy);
        }
    }

    @Override
    public void stopNestedScroll() {
        if (mRecyclerView != null) {
            mRecyclerView.stopScroll();
        }
    }



    @Override
    public boolean isScrollSupported() {
        return mRecyclerView != null;
    }


}
