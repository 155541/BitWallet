package revolhope.splanes.com.bitwallet.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import revolhope.splanes.com.bitwallet.R;

public class MainActivity extends AppCompatActivity {

    private MainFragment mainFragment;
    private OptionsFragment optionsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mainFragment = new MainFragment();
        optionsFragment = new OptionsFragment();

        findViewById(R.id.fabNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogNewAccount dialog = new DialogNewAccount();
                dialog.show(getSupportFragmentManager(), "New");
            }
        });

    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause()
    {
        goAuth();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.home)
        {
            goAuth();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return position == 0 ? mainFragment : optionsFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    /**
     * Method to go to authentication view
     */
    private void goAuth() {

        Intent i = new Intent(this, AuthActivity.class);
        startActivity(i);
        finish();
    }

}
