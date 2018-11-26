package revolhope.splanes.com.bitwallet.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
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
import revolhope.splanes.com.bitwallet.db.DaoAccount;
import revolhope.splanes.com.bitwallet.db.DaoCallbacks;
import revolhope.splanes.com.bitwallet.db.DaoDirectory;
import revolhope.splanes.com.bitwallet.db.DaoK;
import revolhope.splanes.com.bitwallet.helper.AppContract;
import revolhope.splanes.com.bitwallet.helper.AppUtils;
import revolhope.splanes.com.bitwallet.helper.DialogHelper;
import revolhope.splanes.com.bitwallet.model.Account;
import revolhope.splanes.com.bitwallet.model.Directory;
import revolhope.splanes.com.bitwallet.model.K;

public class MainFragment extends Fragment
{
    private Directory currentDir;
    private RecyclerContentAdapter contentAdapter;

    //RecyclerPathAdapter pathAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final DaoDirectory daoDirectory = DaoDirectory.getInstance(getContext());
        final DaoAccount daoAccount = DaoAccount.getInstance(getContext());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView recyclerViewContent = rootView.findViewById(R.id.recyclerViewContent);
        RecyclerView recyclerViewPath = rootView.findViewById(R.id.recyclerViewPath);

        recyclerViewContent.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewPath.setLayoutManager(new LinearLayoutManager(getContext(),
                                                                  LinearLayoutManager.HORIZONTAL,
                                                                  false));

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

                Vibrator v = (Vibrator) Objects.requireNonNull(getActivity()).getSystemService(Context.VIBRATOR_SERVICE);

                if (getFragmentManager() != null) {
                    DialogHolderOptions dialogHolderOptions = new DialogHolderOptions();
                    dialogHolderOptions.isDirectory(false);
                    dialogHolderOptions.setCallback(new DialogHolderOptions.OnOptionPicked() {
                        @Override
                        public void optionPicked(int option) {

                            switch (option){
                                case AppContract.ITEM_MOVE:
                                    break;

                                case AppContract.ITEM_DROP:
                                    break;
                            }
                        }
                    });
                    dialogHolderOptions.show(getFragmentManager(), "OptionDialog");
                }
            }
        });

        contentAdapter.setOnClickDir(new OnDirClick() {
            @Override
            public void onClick(final Directory directory) {
                try {
                    daoDirectory.findChildrenAt(directory.get_id(),
                            new DaoCallbacks.Select<Directory>() {
                                @Override
                                public void onSelected(final Directory[] selection) {

                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                contentAdapter.setDirectories(Arrays.asList(selection));
                                                currentDir = directory;
                                            }
                                        });
                                    }
                                }
                            });
                    daoAccount.findAllAt(directory.get_id(), new DaoCallbacks.Select<Account>() {
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
                    if (getContext() != null) {
                        DialogHelper.showInfo("SQL Error", e.getMessage(), getContext());
                    }
                }
            }

            @Override
            public void onLongClick(Directory directory) {

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
                public void onSelected(Directory[] selection) {
                    contentAdapter.setDirectories(Arrays.asList(selection));
                }
            });

            daoAccount.findInRoot(new DaoCallbacks.Select<Account>() {
                @Override
                public void onSelected(Account[] selection) {
                    contentAdapter.setAccounts(Arrays.asList(selection));
                }
            });
        }
        catch(SQLException exc)
        {
            exc.printStackTrace();
        }

        //RecyclerPathAdapter pathAdapter = new RecyclerPathAdapter(getContext());



        return rootView;
    }

    @Nullable
    public Long getCurrentDir() {

        return this.currentDir != null ? currentDir.get_id() : null;

    }

    public interface OnAccClick {
        void onClick(Account account);
        void onLongClick(Account account);
    }

    public interface OnDirClick {
        void onClick(Directory directory);
        void onLongClick(Directory directory);
    }
}
