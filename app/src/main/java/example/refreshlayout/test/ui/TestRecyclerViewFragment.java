package example.refreshlayout.test.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import example.refreshlayout.R;
import tzy.refreshlayout.MyRefreshLayout;
import tzy.refreshlayout.OnRefreshLoadListener;

public class TestRecyclerViewFragment extends Fragment implements Handler.Callback {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_test_recyclerview, container, false);
    }

    protected RecyclerView mRecyclerView;
     protected MyRefreshLayout mMyRefreshLayout;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMyRefreshLayout = view.findViewById(R.id.refresh_layout);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new Adapter());
        mMyRefreshLayout.setOnRefreshLoadListener(new OnRefreshLoadListener() {
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

        final Adapter adapter = (Adapter) mRecyclerView.getAdapter();

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

    private static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        final List<String> mData = new ArrayList<>();

        void refreshData(List<String> data) {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }

        void insertData(List<String> data) {
            final int start = mData.size();
            mData.addAll(data);
            notifyItemRangeInserted(start, data.size());
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(new TextView(parent.getContext())) {
                @Override
                public String toString() {
                    return super.toString();
                }
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((TextView) holder.itemView).setText(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

}
