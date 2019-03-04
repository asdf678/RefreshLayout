package example.refreshlayout;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {
    private Navigator mNavigator;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mNavigator = (Navigator) getActivity();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavigator.navigateTest();
            }
        });
        view.findViewById(R.id.style).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavigator.navigateStyle();
            }
        });
        view.findViewById(R.id.usage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavigator.navigateUsage();
            }
        });
    }

    @Override
    public void onDetach() {
        mNavigator = null;
        super.onDetach();
    }
}
