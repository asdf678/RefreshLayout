package tzy.refreshlayout;

public interface OnRefreshLoadListener {
    void onProgressRefresh(MyRefreshLayout view);

    void onRefresh(MyRefreshLayout view);

    void onLoading(MyRefreshLayout view);
}
