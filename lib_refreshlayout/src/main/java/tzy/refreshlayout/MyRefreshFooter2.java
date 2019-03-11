package tzy.refreshlayout;

/**
 * Created by Administrator on 2018/3/5.
 */

public interface MyRefreshFooter2 {
    boolean onMeasure(RefreshLayout parent, int parentWidthMeasureSpec, int parentHeightMeasureSpec);

    boolean onLayout(RefreshLayout parent, int scrollY, int targetLeft, int targetTop, int targetRight, int targetBottom, int targetWidget, int targetHeight);

    void onScrolling(RefreshLayout parent, int distance, int deltaY, boolean visible, boolean backScrolling);
    
    void onLoadingAccepted(int distance, int overScrollRange, int type);

    void onLoadingNotAccepted(int distance, int overScrollRange, int type);


    boolean onStartLoading(Scroller scrollTarget, int distance, int overScrollRange, int type);

    int getOverScrollDistance();


    boolean onFinishLoading(Scroller scrollTarget, int distance);
}
