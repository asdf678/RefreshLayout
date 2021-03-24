package example.refreshlayout.style.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import example.refreshlayout.test.ui.TestRecyclerViewFragment;
import tzy.refreshlayout.header.MyRefreshHeaderView2;

public class StyleTJFragment extends TestRecyclerViewFragment {
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMyRefreshLayout.setRefreshHeader(new MyRefreshHeaderView2(getContext()));
    }
}
