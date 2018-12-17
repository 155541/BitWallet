package revolhope.splanes.com.bitwallet.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

import revolhope.splanes.com.bitwallet.R;
import revolhope.splanes.com.bitwallet.db.DaoCallbacks;
import revolhope.splanes.com.bitwallet.db.DaoDirectory;
import revolhope.splanes.com.bitwallet.helper.AppContract;
import revolhope.splanes.com.bitwallet.helper.DialogHelper;
import revolhope.splanes.com.bitwallet.model.Directory;
import revolhope.splanes.com.bitwallet.view.dialogs.DialogFolder;

public class MainActivity extends AppCompatActivity {

    private MainFragment mainFragment;
    private OptionsFragment optionsFragment;
    private OnBackPressedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mainFragment = new MainFragment();
        optionsFragment = new OptionsFragment();

        findViewById(R.id.fabNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AccountActivity.class);
                i.putExtra(AppContract.EXTRA_CURRENT_DIR, mainFragment.getCurrentDir());
                startActivity(i);
            }
        });

        findViewById(R.id.fabFolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFolder dialogFolder = new DialogFolder();
                dialogFolder.isNew(true);
                dialogFolder.show(getSupportFragmentManager(), "DialogFolder");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_backup) {
            return true;
        }
        if (id == R.id.home)
        {
            goAuth();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

    @Override
    public void onBackPressed() {

        if (listener != null) {
            listener.onBackPressed();
        }
        else {
            super.onBackPressed();
        }
    }

    public void newFolder(String folderName) {

        Long parent = mainFragment.getCurrentDir();
        if (parent != null) {
            try {
                Directory directory = new Directory(folderName, parent);
                DaoDirectory daoDirectory = DaoDirectory.getInstance(this);
                daoDirectory.insert(new DaoCallbacks.Update<Directory>() {
                    @Override
                    public void onUpdated(final Directory[] results) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                         "Directory created!", Toast.LENGTH_SHORT)
                                        .show();

                                Objects.requireNonNull(mainFragment.getContentAdapter())
                                        .addDirectories(Arrays.asList(results));
                            }
                        });
                    }
                }, directory);
            }
            catch (SQLException e) {
                DialogHelper.showInfo("SQL Error", e.getMessage(), this);
            }
        }
        else {
            Toast.makeText(this, "Couldn't get parent folder...", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        this.listener = listener;
    }

    public void vibrate(int time) {
        Objects.requireNonNull((Vibrator)getSystemService(Context.VIBRATOR_SERVICE))
                .vibrate(VibrationEffect.createOneShot(time,VibrationEffect.DEFAULT_AMPLITUDE));
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }
}
