package example.refreshlayout.test.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import example.refreshlayout.R;
import tzy.refreshlayout.RefreshLayout;
import tzy.refreshlayout.OnRefreshLoadListener;

public class TestWebViewFragment extends Fragment {
    RefreshLayout mMyRefreshLayout;
    WebView mWebView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_test_webview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMyRefreshLayout = view.findViewById(R.id.refresh_layout);
        mMyRefreshLayout.setLoadEnabled(false);
        mWebView = view.findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
//        mWebView.loadUrl("https//:www.baidu.com/");


        mMyRefreshLayout.setOnRefreshLoadListener(new OnRefreshLoadListener() {
            @Override
            public void onProgressRefresh(RefreshLayout view) {
                mWebView.loadUrl("http://baidu.com");
                view.stopRefreshing();
            }

            @Override
            public void onRefresh(RefreshLayout view) {
                mWebView.reload();
                view.stopRefreshing();

            }

            @Override
            public void onLoading(RefreshLayout view) {

            }
        });
        mMyRefreshLayout.startProgressRefreshing(true);

    }
}
