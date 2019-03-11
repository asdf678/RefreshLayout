package tzy.refreshlayout;

public interface OnRefreshLoadListener {
    void onProgressRefresh(RefreshLayout view);

    void onRefresh(RefreshLayout view);

    void onLoading(RefreshLayout view);
}
