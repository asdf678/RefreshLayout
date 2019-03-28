package tzy.refreshlayout;

import android.view.View;

public interface Scroller<T> {
    /**
     * 判定targetView
     * */
    T ensureTargetScrollChild(View target);

    /**
     * targetView滑动相应距离
     * */
    void scrollBy(int dy);

    /**
     * targetView停止嵌套滑动
     * */
    void stopNestedScroll();

    /**
     * targetView是否支持滑动
     * */
    boolean isScrollSupported();
}
