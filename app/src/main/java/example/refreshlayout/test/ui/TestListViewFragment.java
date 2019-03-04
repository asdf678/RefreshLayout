package example.refreshlayout.test.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import example.refreshlayout.R;
import tzy.refreshlayout.MyRefreshLayout;

public class TestListViewFragment extends Fragment implements Handler.Callback {
    Handler mHandler;
    private static final int MSG_REFRESH = 0x01;
    private static final int MSG_LOAD = 0x02;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(this);
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    ListView mListView;
    MyRefreshLayout mMyRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_test_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = view.findViewById(R.id.list_view);
        mListView.setAdapter(new Adapter());
        mMyRefreshLayout = view.findViewById(R.id.refresh_layout);
        mMyRefreshLayout.setOnRefreshLoadListener(new MyRefreshLayout.OnRefreshLoadListener() {
            @Override
            public void onProgressRefresh(MyRefreshLayout view) {
                mHandler.sendEmptyMessageDelayed(MSG_REFRESH, 1000);
            }

            @Override
            public void onRefresh(MyRefreshLayout view) {
                mHandler.sendEmptyMessageDelayed(MSG_REFRESH, 1000);

            }

            @Override
            public void onLoading(MyRefreshLayout view) {
                mHandler.sendEmptyMessageDelayed(MSG_LOAD, 1000);

            }
        });
        mMyRefreshLayout.startProgressRefreshing(true);

    }

    @Override
    public boolean handleMessage(Message msg) {
        List<String> data = new ArrayList<>(10);

        for (int i = 0; i < 50; ++i) {
            data.add(String.valueOf(i));
        }

        final Adapter adapter = (Adapter) mListView.getAdapter();

        switch (msg.what) {
            case MSG_REFRESH:
                adapter.refreshData(data);
                mMyRefreshLayout.stopRefreshing();
                break;
            case MSG_LOAD:
                adapter.insertData(data);
                mMyRefreshLayout.stopLoading();

                break;

        }
        return false;
    }

    private static final class Adapter extends BaseAdapter {
        final List<String> mData = new ArrayList<>();

        void refreshData(List<String> data) {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }

        void insertData(List<String> data) {
            mData.addAll(data);
            notifyDataSetChanged();

        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) convertView;
            if (textView == null) {
                textView = new TextView(parent.getContext());
            }
            textView.setText(mData.get(position));
            return textView;
        }
    }
}
