package revolhope.splanes.com.bitwallet.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.sql.SQLException;
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
import revolhope.splanes.com.bitwallet.model.Account;
import revolhope.splanes.com.bitwallet.model.Directory;
import revolhope.splanes.com.bitwallet.model.K;

public class MainFragment extends Fragment
{
    private Directory currentDir;
    private RecyclerContentAdapter contentAdapter;
    private RecyclerPathAdapter pathAdapter;
    private DaoDirectory daoDirectory;
    private DaoAccount daoAccount;
    //RecyclerPathAdapter pathAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getContext() == null) return null;

        if (getActivity() != null) {

            ((MainActivity)getActivity())
                    .setOnBackPressedListener(new MainActivity.OnBackPressedListener() {
                        @Override
                        public void onBackPressed() {
                            goBack();
                        }
                    });

        }

        daoDirectory = DaoDirectory.getInstance(getContext());
        daoAccount = DaoAccount.getInstance(getContext());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final RecyclerView recyclerViewContent = rootView.findViewById(R.id.recyclerViewContent);
        RecyclerView recyclerViewPath = rootView.findViewById(R.id.recyclerViewPath);

        recyclerViewContent.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewPath.setLayoutManager(new LinearLayoutManager(getContext(),
                                                                  LinearLayoutManager.HORIZONTAL,
                                                                  false));

        pathAdapter = new RecyclerPathAdapter(getContext());
        pathAdapter.setOnClickListener(new RecyclerPathAdapter.OnClickListener() {
            @Override
            public void onClick(Directory directory) {
                currentDir = directory;
                refreshContentRecyclerView();
            }
        });
        recyclerViewPath.setAdapter(pathAdapter);

        contentAdapter = new RecyclerContentAdapter(getContext());

        contentAdapter.setOnClickAcc(new OnAccClick() {
            @Override
            public void onClick(final Account account) {

                try {
                    DaoK daoK = DaoK.getInstance(getContext());
                    daoK.find(account.get_id(), new DaoCallbacks.Select<K>() {
                        @Override
                        public void onSelected(K[] selection) {

                            if (selection != null && selection.length == 1) {

                                Intent i = new Intent(getContext(), AccountActivity.class);
                                i.putExtra(AppContract.EXTRA_CURRENT_DIR, getCurrentDir());
                                i.putExtra(AppContract.EXTRA_EDIT_ACC, account);
                                AccountActivity.setK(selection[0]);
                                startActivity(i);
                            }
                        }
                    });
                }
                catch (SQLException e) {
                    if (getContext() != null) {
                        DialogHelper.showInfo("SQL Error", e.getMessage(), getContext());
                    }
                }
            }

            @Override
            public void onLongClick(final Account account) {

                if (getActivity() != null) {
                    ((MainActivity)getActivity()).vibrate(100);
                }

                if (getFragmentManager() != null) {
                    DialogHolderOptions dialogHolderOptions = new DialogHolderOptions();
                    dialogHolderOptions.isDirectory(false);
                    dialogHolderOptions.setCallback(new DialogHolderOptions.OnOptionPicked() {
                        @Override
                        public void optionPicked(int option) {

                            switch (option){
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
                                    dialogMove.show(getFragmentManager(), "DialogMove");
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
                                    dialogConfirmation.show(getFragmentManager(), "Confirm");
                                    break;
                            }
                        }
                    });
                    if (getFragmentManager() != null)
                        dialogHolderOptions.show(getFragmentManager(), "OptionDialog");
                }
            }
        });

        contentAdapter.setOnClickDir(new OnDirClick() {
            @Override
            public void onClick(final Directory directory) {
                currentDir = directory;
                pathAdapter.addDirectory(currentDir);
                refreshContentRecyclerView();
            }

            @Override
            public void onLongClick(final Directory directory) {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).vibrate(100);
                }
                if (getFragmentManager() != null) {
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
                                    dialogMove.show(getFragmentManager(), "DialogMove");
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

                                                                        if (getActivity() != null) {
                                                                            getActivity().runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    contentAdapter.setDirectories(Arrays.asList(selection));
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                            catch (SQLException e){
                                                                if (getContext() != null) {
                                                                    DialogHelper.showInfo("SQL Error",
                                                                            e.getMessage(),
                                                                            Objects.requireNonNull(getContext()));
                                                                }
                                                                else e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }, directory);
                                            }
                                            catch (SQLException e) {

                                                if (getContext() != null) {
                                                    DialogHelper.showInfo("SQL Error",
                                                            e.getMessage(),
                                                            Objects.requireNonNull(getContext()));
                                                }
                                                else e.printStackTrace();
                                            }
                                        }
                                    });
                                    dialogFolder.show(getFragmentManager(), "FolderDialog");
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
                                    dialogConfirmation.show(getFragmentManager(),
                                            "ConfirmDialog");
                                    break;
                            }
                        }
                    });
                    if (getFragmentManager() == null) {
                        DialogHelper.showInfo("Fragment Manager Error",
                                          "Couldn't get fragment manager",
                                                    Objects.requireNonNull(getContext()));
                    }
                    else {
                        dialogHolderOptions.show(getFragmentManager(), "OptionDialog");
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
                    if (getActivity() != null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                contentAdapter.setDirectories(Arrays.asList(selection));
                            }
                        });
                    }
                }
            });

            daoAccount.findInRoot(new DaoCallbacks.Select<Account>() {
                @Override
                public void onSelected(final Account[] selection) {
                    if (getActivity() != null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                contentAdapter.setAccounts(Arrays.asList(selection));
                            }
                        });
                    }
                }
            });
        }
        catch(SQLException exc)
        {
            exc.printStackTrace();
        }

        return rootView;
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
        catch(SQLException exc)
        {
            exc.printStackTrace();
        }
        super.onResume();
    }

    public void goBack() {

        if (currentDir != null && !currentDir.getName().equals("Root")) {

            try {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pathAdapter.removeDirectory(currentDir);
                        }
                    });
                }
                daoDirectory.findChildrenAt(currentDir.getParentId(),
                        new DaoCallbacks.Select<Directory>() {
                            @Override
                            public void onSelected(final Directory[] selection) {
                                if (selection != null) {
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                contentAdapter.setDirectories(Arrays.asList(selection));
                                            }
                                        });
                                    }
                                }
                            }
                        });
                daoAccount.findAllAt(currentDir.getParentId(),
                        new DaoCallbacks.Select<Account>() {
                            @Override
                            public void onSelected(final Account[] selection) {
                                if (selection != null) {
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                contentAdapter.setAccounts(Arrays.asList(selection));
                                            }
                                        });
                                    }
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
                if (getContext() != null)
                    DialogHelper.showInfo("SQL Error", e.getMessage(), getContext());
                e.printStackTrace();
            }
        }
        else if (currentDir != null){
            if (getContext() != null && getActivity() != null) {
                DialogHelper.showInfo("Confirm",
                        "Are you sure you want to exit?",
                        getContext());
                // TODO -------------------------
                    /*Intent i = new Intent(getContext(), AuthActivity.class);
                    startActivity(i);
                    getActivity().finish();*/
            }
        }
    }

    RecyclerContentAdapter getContentAdapter() { return contentAdapter; }

    public void dropData(boolean isDirectory, Object id) {
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
        catch (SQLException e)
        {
            if (getContext() != null)
                DialogHelper.showInfo("SQL Error", e.getMessage(), getContext());
            e.printStackTrace();
        }
    }

    @Nullable
    public Long getCurrentDir() {

        return this.currentDir != null ? currentDir.get_id() : null;

    }

    private void refreshContentRecyclerView() {
        try {
            daoDirectory.findChildrenAt(currentDir.get_id(),
                    new DaoCallbacks.Select<Directory>() {
                        @Override
                        public void onSelected(final Directory[] selection) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        contentAdapter.setDirectories(Arrays.asList(selection));
                                    }
                                });
                            }
                        }
                    });
            daoAccount.findAllAt(currentDir.get_id(), new DaoCallbacks.Select<Account>() {
                @Override
                public void onSelected(final Account[] selection) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                contentAdapter.setAccounts(Arrays.asList(selection));
                            }
                        });
                    }
                }
            });
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===== LISTENERS =====

    public interface OnAccClick {
        void onClick(Account account);
        void onLongClick(Account account);
    }

    public interface OnDirClick {
        void onClick(Directory directory);
        void onLongClick(Directory directory);
    }
}
