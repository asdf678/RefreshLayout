package tzy.refreshlayout;

/**
 * Created by Administrator on 2018/3/5.
 */

public interface MyRefreshFooter2 {
    boolean onMeasure(MyRefreshLayout parent, int parentWidthMeasureSpec, int parentHeightMeasureSpec);

    boolean onLayout(MyRefreshLayout parent, int scrollY, int targetLeft, int targetTop, int targetRight, int targetBottom, int targetWidget, int targetHeight);

    void onScrolling(MyRefreshLayout parent, int distance, int deltaY, boolean visible, boolean backScrolling);
    
    void onLoadingAccepted(int distance, int overScrollRange, int type);

    void onLoadingNotAccepted(int distance, int overScrollRange, int type);


    boolean onStartLoading(ScrollTarget scrollTarget, int distance, int overScrollRange, int type);

    int getOverScrollDistance();


    boolean onFinishLoading(ScrollTarget scrollTarget, int distance);
}
