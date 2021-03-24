package example.refreshlayout.test;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import example.refreshlayout.Navigator;
import example.refreshlayout.R;

public class TestFragment extends Fragment {

    private Navigator mTestNavigator;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mTestNavigator = (Navigator) getActivity();
    }

    @Override
    public void onDetach() {
        mTestNavigator = null;
        super.onDetach();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_test, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_recycler_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTestNavigator.navigateTestRecyclerView();
            }
        });
        view.findViewById(R.id.btn_list_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTestNavigator.navigateTestListView();

            }
        });
        view.findViewById(R.id.btn_nested_scroll_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTestNavigator.navigateTestNestedScrollView();

            }
        });
        view.findViewById(R.id.btn_scroll_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTestNavigator.navigateTestScrollView();

            }
        });
        view.findViewById(R.id.btn_web_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTestNavigator.navigateTestWebView();
            }
        });


    }
}
