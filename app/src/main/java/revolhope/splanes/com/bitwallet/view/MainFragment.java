package revolhope.splanes.com.bitwallet.view;

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

import revolhope.splanes.com.bitwallet.R;
import revolhope.splanes.com.bitwallet.adapter.RecyclerContentAdapter;
import revolhope.splanes.com.bitwallet.db.DaoAccount;
import revolhope.splanes.com.bitwallet.db.DaoCallbacks;
import revolhope.splanes.com.bitwallet.db.DaoDirectory;
import revolhope.splanes.com.bitwallet.model.Account;
import revolhope.splanes.com.bitwallet.model.Directory;

public class MainFragment extends Fragment
{
    private Directory currentDir;
    private RecyclerContentAdapter contentAdapter;

    //RecyclerPathAdapter pathAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView recyclerViewContent = rootView.findViewById(R.id.recyclerViewContent);
        RecyclerView recyclerViewPath = rootView.findViewById(R.id.recyclerViewPath);

        recyclerViewContent.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewPath.setLayoutManager(new LinearLayoutManager(getContext(),
                                                                  LinearLayoutManager.HORIZONTAL,
                                                                  false));

        contentAdapter = new RecyclerContentAdapter(getContext());
        recyclerViewContent.setAdapter(contentAdapter);

        DaoDirectory daoDirectory = DaoDirectory.getInstance(getContext());
        DaoAccount daoAccount = DaoAccount.getInstance(getContext());
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

}
