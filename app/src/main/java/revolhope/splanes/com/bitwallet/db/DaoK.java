package revolhope.splanes.com.bitwallet.db;

import android.content.Context;
import androidx.annotation.NonNull;

import java.sql.SQLException;

import revolhope.splanes.com.bitwallet.model.K;

public class DaoK implements AbstractDao<Long, K> {

    private AppDatabase appDatabase;
    private static DaoK instance;

    public static synchronized DaoK getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new DaoK(context);
        }
        return instance;
    }

    private DaoK(Context context)
    {
        appDatabase = AppDatabase.getInstance(context);
    }

    @Override
    public void findById(@NonNull Long aLong, @NonNull DaoCallbacks.Select<K> selectCallback) throws SQLException {
        appDatabase.selectKById(aLong, selectCallback);
    }

    public void find(@NonNull String accId, @NonNull DaoCallbacks.Select<K> selectCallback) throws SQLException {
        appDatabase.selectK(accId, selectCallback);
    }

    @Override
    public void findAll(@NonNull DaoCallbacks.Select<K> selectCallback) throws SQLException {
        appDatabase.selectKById(null, selectCallback);
    }

    @Override
    public void insert(@NonNull DaoCallbacks.Update<K> updateCallback, @NonNull K[] entities) throws SQLException {
        appDatabase.insertK(updateCallback, entities);
    }

    @Override
    public void update(@NonNull DaoCallbacks.Update<K> updateCallback, @NonNull K[] entities) throws SQLException {
        appDatabase.updateK(updateCallback, entities);
    }

    @Override
    public void delete(@NonNull DaoCallbacks.Delete deleteCallback, @NonNull Long[] id) throws SQLException {
        appDatabase.deleteK(deleteCallback, id);
    }

}
