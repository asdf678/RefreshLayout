package tzy.refreshlayout;

import android.view.View;

public interface ScrollTarget<T> {
    T ensureTargetScrollChild(View target);

    void scrollBy(int dy);

    void stopNestedScroll();

    boolean isScrollSupported();
}
