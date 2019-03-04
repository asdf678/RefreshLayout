package tzy.refreshlayout.scrolltarget;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import tzy.refreshlayout.ScrollTarget;

public class RecyclerViewScrollTarget implements ScrollTarget<RecyclerView> {
    final RecyclerView mRecyclerView;


    public RecyclerViewScrollTarget(View target) {
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
