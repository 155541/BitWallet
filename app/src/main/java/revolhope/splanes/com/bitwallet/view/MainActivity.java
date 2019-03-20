package revolhope.splanes.com.bitwallet.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import revolhope.splanes.com.bitwallet.R;
import revolhope.splanes.com.bitwallet.adapter.RecyclerContentAdapter;
import revolhope.splanes.com.bitwallet.adapter.RecyclerPathAdapter;
import revolhope.splanes.com.bitwallet.db.DaoAccount;
import revolhope.splanes.com.bitwallet.db.DaoCallbacks;
import revolhope.splanes.com.bitwallet.db.DaoDirectory;
import revolhope.splanes.com.bitwallet.db.DaoK;
import revolhope.splanes.com.bitwallet.helper.AppContract;
import revolhope.splanes.com.bitwallet.helper.DialogHelper;
import revolhope.splanes.com.bitwallet.helper.HttpConn;
import revolhope.splanes.com.bitwallet.helper.RandomGenerator;
import revolhope.splanes.com.bitwallet.model.Account;
import revolhope.splanes.com.bitwallet.model.Directory;
import revolhope.splanes.com.bitwallet.model.K;
import revolhope.splanes.com.bitwallet.view.dialogs.DialogConfirmation;
import revolhope.splanes.com.bitwallet.view.dialogs.DialogFolder;
import revolhope.splanes.com.bitwallet.view.dialogs.DialogHolderOptions;
import revolhope.splanes.com.bitwallet.view.dialogs.DialogMove;
import revolhope.splanes.com.bitwallet.view.dialogs.DialogToken;
import revolhope.splanes.com.bitwallet.view.modals.ModalHolderOptions;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private Directory currentDir;
    private RecyclerContentAdapter contentAdapter;
    private RecyclerPathAdapter pathAdapter;
    private DaoDirectory daoDirectory;
    private DaoAccount daoAccount;

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//                                      Override methods
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            //getSupportActionBar().setLogo(R.drawable.ic_app_brand);
            //getSupportActionBar().setDisplayUseLogoEnabled(true);
            //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_USE_LOGO);
            getSupportActionBar().setTitle(R.string.app_name);
        }
        daoDirectory = DaoDirectory.getInstance(this);
        daoAccount = DaoAccount.getInstance(this);

        final RecyclerView recyclerViewContent = findViewById(R.id.recyclerViewContent);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3,
                LinearLayoutManager.VERTICAL, false);
        contentAdapter = new RecyclerContentAdapter(this);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (contentAdapter.getItemViewType(position) !=
                    RecyclerContentAdapter.TYPE_DIR)
                     return 1; //  TODO: CHANGE!
                else return 1;
            }
        });

        RecyclerView recyclerViewPath = findViewById(R.id.recyclerViewPath);
        recyclerViewContent.setLayoutManager(gridLayoutManager);
        recyclerViewPath.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,false));

        pathAdapter = new RecyclerPathAdapter(this);
        pathAdapter.setOnClickListener(new RecyclerPathAdapter.OnClickListener() {
            @Override
            public void onClick(Directory directory) {
                setPath(directory);
            }
        });
        recyclerViewPath.setAdapter(pathAdapter);

        contentAdapter.setOnClickAcc(new RecyclerContentAdapter.OnAccClick() {
            @Override
            public void onClick(final Account account) {

                try {
                    DaoK daoK = DaoK.getInstance(context);
                    daoK.find(account.get_id(), new DaoCallbacks.Select<K>() {
                        @Override
                        public void onSelected(K[] selection) {

                            if (selection != null && selection.length == 1) {

                                Intent i = new Intent(context, AccountActivity.class);
                                i.putExtra(AppContract.EXTRA_CURRENT_DIR, currentDir.get_id());
                                i.putExtra(AppContract.EXTRA_EDIT_ACC, account);
                                AccountActivity.setK(selection[0]);
                                startActivity(i);
                            }
                        }
                    });
                }
                catch (final SQLException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogHelper.showInfo(context,"SQL Error", e.getMessage(),
                                    null);
                        }
                    });
                }
            }

            @Override
            public void onLongClick(final Account account) {

                vibrate();
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (fragmentManager != null) {

                    final ModalHolderOptions modalHolderOptions = new ModalHolderOptions();

                    ModalHolderOptions.OnModalOptionPicked onModalOptionPicked =
                            new ModalHolderOptions.OnModalOptionPicked() {
                        @Override
                        public void optionPicked(int option) {
                            switch (option){

                                case AppContract.ITEM_WEB:
                                    modalHolderOptions.dismiss();
                                    DialogToken dialogToken = new DialogToken();
                                    Bundle args = new Bundle();
                                    args.putSerializable(DialogToken.ARG0,
                                            new DialogToken.OnSendAuthSucceeded() {
                                                @Override
                                                public void onSendAuthSucceeded(int uses, long deadLine) {
                                                    retrieveAndPost(account.get_id(), uses, deadLine);
                                                }
                                            });
                                    dialogToken.setArguments(args);
                                    dialogToken.show(getSupportFragmentManager(), "DialogToken");
                                    break;
                                case AppContract.ITEM_MOVE:
                                    modalHolderOptions.dismiss();
                                    DialogMove dialogMove = new DialogMove();
                                    dialogMove.setName(account.getAccount());
                                    dialogMove.setListener(new DialogMove.OnMoveListener() {
                                        @Override
                                        public void onMove(long newParent) {
                                            account.setParent(newParent);
                                            try {
                                                daoAccount.update(new DaoCallbacks.Update<Account>() {
                                                    @Override
                                                    public void onUpdated(Account[] results) {
                                                        refreshContentRecyclerView();
                                                    }
                                                }, new Account[] {account});
                                            }
                                            catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    dialogMove.show(getSupportFragmentManager(), "DialogMove");
                                    break;

                                case AppContract.ITEM_DROP:
                                    modalHolderOptions.dismiss();
                                    DialogConfirmation dialogConfirmation = new DialogConfirmation();
                                    dialogConfirmation.isDirectory(false);
                                    dialogConfirmation.setListener(
                                            new DialogConfirmation.OnConfirmListener() {
                                                @Override
                                                public void onConfirm() {
                                                    dropData(false, account.get_id());
                                                }
                                            });
                                    dialogConfirmation.show(getSupportFragmentManager(),
                                            "Confirm");
                                    break;
                            }
                        }
                    };

                    try {
                        Bundle arguments = new Bundle();
                        arguments.putBoolean(ModalHolderOptions.ARG0, true);
                        arguments.putSerializable(ModalHolderOptions.ARG1, onModalOptionPicked);
                        modalHolderOptions.setArguments(arguments);
                        modalHolderOptions.show(fragmentManager, ModalHolderOptions.TAG);
                    }
                    catch (Exception e) {
                        // TODO: Change!
                        e.printStackTrace();
                    }


                    /*final DialogHolderOptions dialogHolderOptions = new DialogHolderOptions();
                    dialogHolderOptions.isDirectory(false);
                    dialogHolderOptions.setCallback(new DialogHolderOptions.OnOptionPicked() {
                        @Override
                        public void optionPicked(int option) {

                            switch (option){

                                case AppContract.ITEM_WEB:

                                    dialogHolderOptions.dismiss();
                                    DialogToken dialogToken = new DialogToken();
                                    dialogToken.show(getSupportFragmentManager(), "DialogToken");

                                    *//*try {
                                        DaoK daoK = DaoK.getInstance(context);
                                        daoK.find(account.get_id(), new DaoCallbacks.Select<K>() {
                                            @Override
                                            public void onSelected(K[] selection) {

                                                if (selection != null && selection.length == 1) {
                                                    try {
                                                        HttpConn httpConn = new HttpConn(context);
                                                        httpConn.post(RandomGenerator.createToken(),
                                                                selection[0].getPwdBase64());
                                                    }
                                                    catch (final IOException e) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                DialogHelper.showInfo(
                                                                        "IO Error",
                                                                         e.getMessage(), context);
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    catch (final SQLException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                DialogHelper.showInfo("SQL Error",
                                                        e.getMessage(),
                                                        context);
                                            }
                                        });
                                    }*//*
                                    break;
                                case AppContract.ITEM_MOVE:

                                    DialogMove dialogMove = new DialogMove();
                                    dialogMove.setName(account.getAccount());
                                    dialogMove.setListener(new DialogMove.OnMoveListener() {
                                        @Override
                                        public void onMove(long newParent) {
                                            account.setParent(newParent);
                                            try {
                                                daoAccount.update(new DaoCallbacks.Update<Account>() {
                                                    @Override
                                                    public void onUpdated(Account[] results) {
                                                        refreshContentRecyclerView();
                                                    }
                                                }, new Account[] {account});
                                            }
                                            catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    dialogMove.show(getSupportFragmentManager(), "DialogMove");
                                    break;

                                case AppContract.ITEM_DROP:

                                    DialogConfirmation dialogConfirmation = new DialogConfirmation();
                                    dialogConfirmation.isDirectory(false);
                                    dialogConfirmation.setListener(
                                            new DialogConfirmation.OnConfirmListener() {
                                                @Override
                                                public void onConfirm() {
                                                    dropData(false, account.get_id());
                                                }
                                            });
                                    dialogConfirmation.show(getSupportFragmentManager(),
                                            "Confirm");
                                    break;
                            }
                        }
                    });
                    if (getSupportFragmentManager() != null) {
                        dialogHolderOptions.show(getSupportFragmentManager(), "OptionDialog");
                    }*/
                }
            }
        });
        contentAdapter.setOnClickDir(new RecyclerContentAdapter.OnDirClick() {
            @Override
            public void onClick(final Directory directory) {
                currentDir = directory;
                pathAdapter.addDirectory(currentDir);
                refreshContentRecyclerView();
            }

            @Override
            public void onLongClick(final Directory directory) {

                vibrate();
                if (getSupportFragmentManager() != null) {
                    DialogHolderOptions dialogHolderOptions = new DialogHolderOptions();
                    dialogHolderOptions.isDirectory(true);
                    dialogHolderOptions.setCallback(new DialogHolderOptions.OnOptionPicked() {
                        @Override
                        public void optionPicked(int option) {

                            switch (option){
                                case AppContract.ITEM_MOVE:

                                    DialogMove dialogMove = new DialogMove();
                                    dialogMove.setName(directory.getName());
                                    dialogMove.setListener(new DialogMove.OnMoveListener() {
                                        @Override
                                        public void onMove(long newParent) {
                                            try {
                                                directory.setParentId(newParent);
                                                daoDirectory.update(new DaoCallbacks.Update<Directory>() {
                                                    @Override
                                                    public void onUpdated(Directory[] results) {
                                                        refreshContentRecyclerView();
                                                    }
                                                }, directory);
                                            }
                                            catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    dialogMove.show(getSupportFragmentManager(), "DialogMove");
                                    break;
                                case AppContract.ITEM_UPDATE:

                                    DialogFolder dialogFolder = new DialogFolder();
                                    dialogFolder.isNew(false);
                                    dialogFolder.setCallback(new DialogFolder.OnUpdateFolder() {
                                        @Override
                                        public void onUpdate(String newName) {
                                            try {
                                                directory.setName(newName);
                                                daoDirectory.update(new DaoCallbacks.Update<Directory>() {
                                                    @Override
                                                    public void onUpdated(Directory[] results) {

                                                        if (results != null && results.length == 1){
                                                            try {
                                                                daoDirectory.findChildrenAt(currentDir.get_id(), new DaoCallbacks.Select<Directory>() {
                                                                    @Override
                                                                    public void onSelected(final Directory[] selection) {
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                contentAdapter.setDirectories(Arrays.asList(selection));
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                            catch (final SQLException e){
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        DialogHelper.
                                                                                showInfo(context,"SQL Error",e.getMessage(), null);
                                                                    }
                                                                });
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }, directory);
                                            }
                                            catch (final SQLException e) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        DialogHelper.showInfo(context,"SQL Error",
                                                                e.getMessage(), null);
                                                    }
                                                });
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    dialogFolder.show(getSupportFragmentManager(),
                                            "FolderDialog");
                                    break;
                                case AppContract.ITEM_DROP:

                                    DialogConfirmation dialogConfirmation = new DialogConfirmation();
                                    dialogConfirmation.isDirectory(true);
                                    dialogConfirmation.setListener(
                                            new DialogConfirmation.OnConfirmListener() {
                                                @Override
                                                public void onConfirm() {
                                                    dropData(true, directory.get_id());
                                                }
                                            });
                                    dialogConfirmation.show(getSupportFragmentManager(),
                                            "ConfirmDialog");
                                    break;
                            }
                        }
                    });
                    if (getFragmentManager() == null) {
                        DialogHelper.showInfo(context,"Fragment Manager Error",
                                "Couldn't get fragment manager", null);
                    }
                    else {
                        dialogHolderOptions.show(getSupportFragmentManager(), "OptionDialog");
                    }
                }
            }
        });
        recyclerViewContent.setAdapter(contentAdapter);

        try
        {
            daoDirectory.findRoot(new DaoCallbacks.Select<Directory>() {
                @Override
                public void onSelected(Directory[] selection) {
                    if (selection != null && selection.length == 1) {
                        currentDir = selection[0];
                    }
                }
            });

            daoDirectory.findInRoot(new DaoCallbacks.Select<Directory>() {
                @Override
                public void onSelected(final Directory[] selection) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            contentAdapter.setDirectories(Arrays.asList(selection));
                        }
                    });
                }
            });

            daoAccount.findInRoot(new DaoCallbacks.Select<Account>() {
                @Override
                public void onSelected(final Account[] selection) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            contentAdapter.setAccounts(Arrays.asList(selection));
                        }
                    });
                }
            });
        }
        catch(SQLException exc) {
            exc.printStackTrace();
        }


        findViewById(R.id.fabNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AccountActivity.class);
                i.putExtra(AppContract.EXTRA_CURRENT_DIR, currentDir.get_id());
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
    public void onResume() {
        try
        {
            if (currentDir == null) {
                daoDirectory.findRoot(new DaoCallbacks.Select<Directory>() {
                    @Override
                    public void onSelected(Directory[] selection) {
                        if (selection != null && selection.length == 1) {
                            currentDir = selection[0];
                            refreshContentRecyclerView();
                        }
                    }
                });
            }
            else {
                refreshContentRecyclerView();
            }
        }
        catch(SQLException exc) {
            exc.printStackTrace();
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (currentDir != null && !currentDir.getName().equals("Root")) {
            try {
                pathAdapter.removeDirectory(currentDir);

                daoDirectory.findChildrenAt(currentDir.getParentId(),
                        new DaoCallbacks.Select<Directory>() {
                            @Override
                            public void onSelected(final Directory[] selection) {
                                if (selection != null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            contentAdapter.setDirectories(Arrays.asList(selection));
                                        }
                                    });
                                }
                            }
                        });
                daoAccount.findAllAt(currentDir.getParentId(),
                        new DaoCallbacks.Select<Account>() {
                            @Override
                            public void onSelected(final Account[] selection) {
                                if (selection != null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            contentAdapter.setAccounts(Arrays.asList(selection));
                                        }
                                    });
                                }
                            }
                        });
                daoDirectory.findById(currentDir.getParentId(),
                        new DaoCallbacks.Select<Directory>() {
                            @Override
                            public void onSelected(Directory[] selection) {
                                if (selection != null && selection.length == 1) {
                                    currentDir = selection[0];
                                }
                            }
                        });
            }
            catch (SQLException e) {
                DialogHelper.showInfo(context, "SQL Error", e.getMessage(), null);
                e.printStackTrace();
            }
        }
        else if (currentDir != null){
            DialogHelper.showInfo(context, "Confirmation", "Do you really want to exit?",
                    new DialogHelper.DialogHelperListener() {
                        @Override
                        public void onDialogClick() {
                            Intent i = new Intent(getApplicationContext(), AuthActivity.class);
                            startActivity(i);
                            finish();
                        }
                    });
        }
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

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//                                       Private methods
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    private void retrieveAndPost(final String accId, final int uses, final long deadDate) {
        try {
            DaoK daoK = DaoK.getInstance(context);
            daoK.find(accId, new DaoCallbacks.Select<K>() {
                @Override
                public void onSelected(K[] selection) {
                    if (selection != null && selection.length == 1) {
                        try {
                            HttpConn client = new HttpConn(context);
                            client.post(uses, deadDate, RandomGenerator.createToken(),
                                    selection[0].getPwdBase64());
                        }
                        catch (final IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogHelper.showInfo(context,"IO Error",
                                            e.getMessage(), null);
                                }
                            });
                        }
                    }
                }
            });

        } catch (final SQLException excSQL) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogHelper.showInfo(context,"SQL Error",
                            excSQL.getMessage(), null);
                }
            });
        }
    }

    private void refreshContentRecyclerView() {
        try {
            daoDirectory.findChildrenAt(currentDir.get_id(), new DaoCallbacks.Select<Directory>() {
                        @Override
                        public void onSelected(final Directory[] selection) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    contentAdapter.setDirectories(Arrays.asList(selection));
                                }
                            });
                        }
                    });
            daoAccount.findAllAt(currentDir.get_id(), new DaoCallbacks.Select<Account>() {
                @Override
                public void onSelected(final Account[] selection) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            contentAdapter.setAccounts(Arrays.asList(selection));
                        }
                    });
                }
            });
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void dropData(boolean isDirectory, Object id) {
        try {

            DaoCallbacks.Delete delete = new DaoCallbacks.Delete() {
                @Override
                public void onDelete(int deleteCode) {
                    if (deleteCode == DaoCallbacks.DELETE_OK) {
                        refreshContentRecyclerView();
                    }
                }
            };

            if (isDirectory) {
                daoDirectory.delete(delete, (Long)id);
            }
            else {
                daoAccount.delete(delete, new String[]{id.toString()});
            }
        }
        catch (final SQLException e)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogHelper.showInfo(context, "SQL Error", e.getMessage(), null);
                    e.printStackTrace();
                }
            });
        }
    }

    private void setPath(Directory directory) {

        if (directory == null) {
            try {
                daoDirectory.findRoot(new DaoCallbacks.Select<Directory>() {
                    @Override
                    public void onSelected(Directory[] selection) {
                        if (selection != null && selection.length == 1) {
                            currentDir = selection[0];
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pathAdapter.setDirectories(new ArrayList<Directory>());
                                }
                            });
                            refreshContentRecyclerView();
                        }
                    }
                });
            }
            catch (final SQLException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogHelper.showInfo(context,"SQL Error", e.getMessage(), null);
                    }
                });
                e.printStackTrace();
            }
        }
        else {
            currentDir = directory;
            refreshContentRecyclerView();
        }
    }

    private void goAuth() {

        Intent i = new Intent(this, AuthActivity.class);
        startActivity(i);
        finish();
    }

    private void vibrate() {
        Vibrator v = Objects.requireNonNull((Vibrator)getSystemService(Context.VIBRATOR_SERVICE));
        v.vibrate(VibrationEffect.createOneShot(100,VibrationEffect.DEFAULT_AMPLITUDE));
    }

    public void newFolder(String folderName) {

        Long parent = currentDir.get_id();
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

                                Objects.requireNonNull(contentAdapter)
                                        .addDirectories(Arrays.asList(results));
                            }
                        });
                    }
                }, directory);
            }
            catch (SQLException e) {
                DialogHelper.showInfo(this,"SQL Error", e.getMessage(), null);
            }
        }
        else {
            Toast.makeText(this, "Couldn't get parent folder...", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
