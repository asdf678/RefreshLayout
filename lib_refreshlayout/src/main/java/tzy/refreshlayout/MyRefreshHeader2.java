package tzy.refreshlayout;

/**
 * Created by Administrator on 2018/3/5.
 */

public interface MyRefreshHeader2 {

    boolean onMeasure(MyRefreshLayout parent, int parentWidthMeasureSpec, int parentHeightMeasureSpec);

    boolean onLayout(MyRefreshLayout parent, int scrollY, int targetLeft, int targetTop, int targetRight, int targetBottom, int targetWidget, int targetHeight);

    void onScrolling(MyRefreshLayout parent, int distance, int deltaY, boolean visible, boolean backScrolling);
    
    boolean onStartRefreshing(ScrollTarget scrollTarget, int distance, int overScrollRange, int type);

    int getOverScrollDistance();

    void onRefreshingAccepted(int distance, int overScrollRange, int type);

    void onRefreshingNotAccepted(int distance, int overScrollRange, int type);

    boolean onFinishRefreshing(ScrollTarget scrollTarget, int distance);
}
