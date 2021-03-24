package example.refreshlayout.style;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import example.refreshlayout.Navigator;
import example.refreshlayout.R;

public class StyleFragment extends Fragment {

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
        return inflater.inflate(R.layout.frag_style, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_base).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTestNavigator.navigateStyleBase();
            }
        });
        view.findViewById(R.id.btn_tj).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTestNavigator.navigateStyleTJ();

            }
        });


    }
}
