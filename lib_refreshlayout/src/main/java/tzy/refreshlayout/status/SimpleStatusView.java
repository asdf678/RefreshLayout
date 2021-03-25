package tzy.refreshlayout.status;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import tzy.refreshlayout.R;
import tzy.refreshlayout.StatusView;

public class SimpleStatusView extends FrameLayout implements StatusView {
    View mEmptyView;
    View mNetWorkView;

    public SimpleStatusView(Context context) {
        this(context, null);
    }

    public SimpleStatusView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleStatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setEmptyNetWorkView(R.layout.layout_empty_data, R.layout.layout_network_error, Gravity.CENTER);
    }

    public void setEmptyNetWorkView(int emptyLayoutId, int netWorkLayoutId) {
        setEmptyNetWorkView(emptyLayoutId, netWorkLayoutId, Gravity.CENTER);
    }

    @Override
    public void showEmptyView() {
        setViewVisible(this, VISIBLE);
        setViewVisible(mNetWorkView, GONE);
        setViewVisible(mEmptyView, VISIBLE);
    }

    @Override
    public void showNetworkView() {
        setViewVisible(this, VISIBLE);
        setViewVisible(mEmptyView, GONE);
        setViewVisible(mNetWorkView, VISIBLE);
    }

    @Override
    public void hideStatusView() {
        setViewVisible(this, GONE);
        setViewVisible(mEmptyView, GONE);
        setViewVisible(mNetWorkView, GONE);
    }


    public void setEmptyNetWorkView(int emptyLayoutId, int netWorkLayoutId, int gravity) {
        final int oldEmptyVisible = mEmptyView == null ? GONE : mEmptyView.getVisibility();
        final int oldNetworkVisible = mNetWorkView == null ? GONE : mNetWorkView.getVisibility();
        clearChildViews();

        LayoutInflater inflater = LayoutInflater.from(getContext());

        mEmptyView = inflater.inflate(emptyLayoutId, this, false);
        addAndSetView(mEmptyView, oldEmptyVisible, gravity);

        mNetWorkView = inflater.inflate(netWorkLayoutId, this, false);
        addAndSetView(mNetWorkView, oldNetworkVisible, gravity);
    }

    public void setEmptyImageResource(int resId) {
        getEmptyImageView().setImageResource(resId);
    }

    public ImageView getEmptyImageView() {
        return mEmptyView.findViewById(R.id.status_empty_ic);
    }

    public void setEmptyText(String text) {
        getEmptyTextView().setText(text);
    }

    public void setEmptyTextColor(int color) {
        getEmptyTextView().setTextColor(color);
    }

    public void setEmptyTextSize(float size) {
        getEmptyTextView().setTextSize(size);
    }

    public TextView getEmptyTextView() {
        return mEmptyView.findViewById(R.id.status_empty_text);
    }

    public void setNetworkImageResource(int resId) {
        getNetworkImageView().setImageResource(resId);
    }

    public ImageView getNetworkImageView() {
        return mNetWorkView.findViewById(R.id.status_network_ic);
    }

    public void setNetworkText(String text) {
        getNetworkTextView().setText(text);
    }

    public void setNetworkTextColor(int color) {
        getNetworkTextView().setTextColor(color);
    }

    public void setNetworkTextSize(float size) {
        getNetworkTextView().setTextSize(size);
    }

    public TextView getNetworkTextView() {
        return mNetWorkView.findViewById(R.id.status_network_text);
    }

    void addAndSetView(View view, int visible, int gravity) {
        view.setVisibility(visible);
        ((LayoutParams) view.getLayoutParams()).gravity = gravity;
        addView(view);
    }

    private void clearChildViews() {
        removeAllViews();
        mEmptyView = null;
        mNetWorkView = null;
    }

    static void setViewVisible(View view, int visible) {
        if (view.getVisibility() != visible) {
            view.setVisibility(visible);
        }
    }
}
