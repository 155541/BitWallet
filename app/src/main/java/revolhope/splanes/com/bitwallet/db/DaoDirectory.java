package revolhope.splanes.com.bitwallet.db;

import android.content.Context;
import androidx.annotation.NonNull;

import java.sql.SQLException;
import revolhope.splanes.com.bitwallet.model.Directory;

public class DaoDirectory implements AbstractDao<Long, Directory> {

    private AppDatabase appDatabase;
    private static DaoDirectory instance;


    public static synchronized DaoDirectory getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new DaoDirectory(context);
        }
        return instance;
    }

    private DaoDirectory(@NonNull Context context)
    {
        appDatabase = AppDatabase.getInstance(context);
    }

    @Override
    public void findById(@NonNull Long aLong,
                         @NonNull DaoCallbacks.Select<Directory> selectCallback)
            throws SQLException {
        appDatabase.selectDirectory(aLong, selectCallback);
    }

    @Override
    public void findAll(@NonNull DaoCallbacks.Select<Directory> selectCallback)
            throws SQLException {
        appDatabase.selectDirectory(null, selectCallback);
    }

    public void findRoot(@NonNull DaoCallbacks.Select<Directory> selectCallback)
            throws SQLException{
        appDatabase.selectDirectoryRoot(selectCallback);
    }

    public void findInRoot(@NonNull DaoCallbacks.Select<Directory> selectCallback)
        throws SQLException{
        appDatabase.selectDirectoryInRoot(selectCallback);
    }

    public void findChildrenAt(@NonNull Long idParent,
                               @NonNull DaoCallbacks.Select<Directory> selectCallback)
            throws SQLException{
        appDatabase.selectDirectoryAt(idParent, selectCallback);
    }

    @Override
    public void insert(@NonNull DaoCallbacks.Update<Directory> updateCallback,
                       @NonNull Directory... entities)
            throws SQLException {
        appDatabase.insertDirectory(updateCallback, entities);
    }

    @Override
    public void update(@NonNull DaoCallbacks.Update<Directory> updateCallback,
                       @NonNull Directory... entities)
            throws SQLException {
        appDatabase.updateDirectory(updateCallback, entities);
    }

    @Override
    public void delete(@NonNull DaoCallbacks.Delete deleteCallback, @NonNull Long... ids)
            throws SQLException {
        appDatabase.deleteDirectory(deleteCallback, ids);
    }
}
