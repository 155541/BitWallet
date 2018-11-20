package revolhope.splanes.com.bitwallet.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.sql.SQLException;

interface AbstractDao<Id, T> {

    void findById(@NonNull Id id, @NonNull DaoCallbacks.Select<T> selectCallback) throws SQLException;

    void findAll(@NonNull DaoCallbacks.Select<T> selectCallback) throws SQLException;

    void insert(@NonNull DaoCallbacks.Update<T> updateCallback, @NonNull T[] entities) throws SQLException;

    void update(@NonNull DaoCallbacks.Update<T> updateCallback, @NonNull T[] entities) throws SQLException;

    void delete(@NonNull DaoCallbacks.Delete deleteCallback, @NonNull Id[] id) throws SQLException;
}
