package tzy.refreshlayout.scroller;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import tzy.refreshlayout.Scroller;

public class RecyclerViewScroller implements Scroller<RecyclerView> {
    final RecyclerView mRecyclerView;

    /**
     * 适配了RecyclerView
     */

    public RecyclerViewScroller(View target) {
        mRecyclerView = target instanceof RecyclerView && ViewCompat.isNestedScrollingEnabled(target) ? (RecyclerView) target : null;
    }

    @Override
    public RecyclerView ensureTargetScrollChild(View target) {
        return mRecyclerView;
    }

    /**
     * RecyclerView滑动相应距离
     */
    @Override
    public void scrollBy(int dy) {
        if (mRecyclerView != null) {
            mRecyclerView.scrollBy(0, dy);
        }
    }

    /**
     * RecyclerView停止滑动动画
     */
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
