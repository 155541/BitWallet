package revolhope.splanes.com.bitwallet.db;

import java.util.List;

public abstract class DaoCallbacks {

    public static final int DELETE_FAIL = 0;
    public static final int DELETE_OK = 1;
    public static final int DELETE_PARTIAL = 2;

    public interface Update<T>{
        void onUpdated(T[] results);
    }

    public interface Select<T>{
        void onSelected(T[] selection);
    }

    public interface Delete{
        void onDelete(int deleteCode);
    }
}
