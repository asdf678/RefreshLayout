package example.refreshlayout.usage.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import example.refreshlayout.R;
import example.refreshlayout.test.ui.TestRecyclerViewFragment;

public class UsageMDFragment extends Fragment {
    ViewPager mViewPager;
    TabLayout mTabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_usage_md, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager = view.findViewById(R.id.view_pager);
        mViewPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), TestRecyclerViewFragment.class));
        mTabLayout = view.findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private static final class MyFragmentPagerAdapter extends FragmentPagerAdapter {


        final Class<? extends Fragment> mFragmentClass;

        public MyFragmentPagerAdapter(FragmentManager fm, Class<? extends Fragment> aFragmentClass) {
            super(fm);
            this.mFragmentClass = aFragmentClass;
        }


        @Override
        public Fragment getItem(int position) {
            try {
                return mFragmentClass.newInstance();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return String.valueOf(position);
        }

        @Override
        public int getCount() {
            return 10;
        }
    }
}
