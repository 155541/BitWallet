package revolhope.splanes.com.bitwallet.db;

import android.content.Context;
import android.support.annotation.NonNull;

import java.sql.SQLException;

import revolhope.splanes.com.bitwallet.model.Account;

public class DaoAccount implements AbstractDao<String, Account> {

    private AppDatabase appDatabase;
    private static DaoAccount instance;

    public static synchronized DaoAccount getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new DaoAccount(context);
        }
        return instance;
    }

    private DaoAccount(Context context)
    {
        appDatabase = AppDatabase.getInstance(context);
    }

    @Override
    public void findById(@NonNull String s, @NonNull DaoCallbacks.Select<Account> selectCallback)
            throws SQLException {
        appDatabase.selectAccount(s, selectCallback);
    }

    @Override
    public void findAll(@NonNull DaoCallbacks.Select<Account> selectCallback) throws SQLException {
        appDatabase.selectAccount(null, selectCallback);
    }

    public void findInRoot(@NonNull DaoCallbacks.Select<Account> selectCallback) throws SQLException {
        appDatabase.selectAccountRoot(selectCallback);
    }

    @Override
    public void insert(@NonNull DaoCallbacks.Update<Account> updateCallback,
                       @NonNull Account[] entity) throws SQLException {
        appDatabase.insertAccount(updateCallback, entity);
    }

    @Override
    public void update(@NonNull DaoCallbacks.Update<Account> updateCallback,
                       @NonNull Account[] entities) throws SQLException {
        appDatabase.updateAccount(updateCallback, entities);
    }

    @Override
    public void delete(@NonNull DaoCallbacks.Delete deleteCallback, @NonNull String[] id)
            throws SQLException {
        appDatabase.deleteAccount(deleteCallback, id);
    }
}
