package tzy.refreshlayout;

public interface RefreshView {
    void setRefreshHeader(MyRefreshHeader2 header);

    void setRefreshFooter(MyRefreshFooter2 footer);

    void setRefreshEnabled(boolean enabled);

    void setLoadEnabled(boolean enabled);

    void stopRefreshing();

    void stopLoading();

    boolean isProgressRefreshing();

    boolean isLoading();

    boolean isRefreshing();

    void setScrollTarget(Scroller scrollTarget);

    void setOnRefreshLoadListener(OnRefreshLoadListener listener);

    void startProgressRefreshing(boolean notify);


}
