package example.refreshlayout;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import example.refreshlayout.test.TestFragment;
import example.refreshlayout.test.ui.TestListViewFragment;
import example.refreshlayout.test.ui.TestNestedViewFragment;
import example.refreshlayout.test.ui.TestRecyclerViewFragment;
import example.refreshlayout.test.ui.TestScrollViewFragment;
import example.refreshlayout.test.ui.TestWebViewFragment;
import example.refreshlayout.util.Utils;


public class MainActivity extends AppCompatActivity implements Navigator {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_main);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Utils.addFragment(getSupportFragmentManager(), MainFragment.class, R.id.container);
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return true;
        }

        finish();
        return true;
    }


    @Override
    public void navigateTest() {
        Utils.replaceFragment(getSupportFragmentManager(), TestFragment.class, R.id.container, null);

    }

    @Override
    public void navigateTestRecyclerView() {
        Utils.replaceFragment(getSupportFragmentManager(), TestRecyclerViewFragment.class, R.id.container, null);

    }

    @Override
    public void navigateTestListView() {
        Utils.replaceFragment(getSupportFragmentManager(), TestListViewFragment.class, R.id.container, null);

    }

    @Override
    public void navigateTestNestedScrollView() {
        Utils.replaceFragment(getSupportFragmentManager(), TestNestedViewFragment.class, R.id.container, null);

    }

    @Override
    public void navigateTestScrollView() {
        Utils.replaceFragment(getSupportFragmentManager(), TestScrollViewFragment.class, R.id.container, null);

    }

    @Override
    public void navigateTestWebView() {
        Utils.replaceFragment(getSupportFragmentManager(), TestWebViewFragment.class, R.id.container, null);

    }

    @Override
    public void navigateStyle() {

    }

    @Override
    public void navigateUsage() {

    }
}
