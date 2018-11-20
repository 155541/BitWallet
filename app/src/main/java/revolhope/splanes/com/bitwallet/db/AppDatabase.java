package revolhope.splanes.com.bitwallet.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import revolhope.splanes.com.bitwallet.db.contracts.AccKContract;
import revolhope.splanes.com.bitwallet.db.contracts.AccountContract;
import revolhope.splanes.com.bitwallet.db.contracts.DirectoryContract;
import revolhope.splanes.com.bitwallet.db.contracts.KContract;
import revolhope.splanes.com.bitwallet.model.Account;
import revolhope.splanes.com.bitwallet.model.Directory;

public class AppDatabase extends SQLiteOpenHelper {


    private static final String DB_NAME = "BitWallet-db";
    private static int DB_VERSION = 1;

    private static AppDatabase instance;

    static synchronized AppDatabase getInstance(@NonNull Context context)
    {
        if (instance == null)
        {
            instance = new AppDatabase(context.getApplicationContext());
        }
        return instance;
    }

    private AppDatabase(@NonNull Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

// ===============================================================================================//
//                                         SET UP
// ===============================================================================================//

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(DirectoryContract.STATEMENT_CREATE);
        sqLiteDatabase.execSQL(DirectoryContract.STATEMENT_INSERT_ROOT);
        sqLiteDatabase.execSQL(AccountContract.STATEMENT_CREATE);
        sqLiteDatabase.execSQL(KContract.STATEMENT_CREATE);
        sqLiteDatabase.execSQL(AccKContract.STATEMENT_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int currentVersion, int newVersion)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DirectoryContract.TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AccountContract.TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + KContract.TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AccKContract.TABLE);

        DB_VERSION = newVersion;
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onConfigure(SQLiteDatabase db)
    {
        db.setForeignKeyConstraintsEnabled(true);
    }



// ===============================================================================================//
//                                         DIRECTORY
// ===============================================================================================//

    /**
     * Method to retrieve directories from database
     * @param id Long identifier of the Directory to select, if it is null, then all dirs will be
     *          selected
     * @param selectCallback Callback of the method
     */
    void selectDirectory(@Nullable Long id, @NonNull DaoCallbacks.Select<Directory> selectCallback)
    {
        new SelectDirAsyncTask(getWritableDatabase(), id, selectCallback).execute();
    }

    /**
     * Method to retrieve directories from database located at directory root
     * @param selectCallback Callback of the method
     */
    void selectDirectoryRoot(@NonNull DaoCallbacks.Select<Directory> selectCallback)
    {
        new SelectDirRootAsyncTask(getWritableDatabase(), selectCallback).execute();
    }

    /**
     * Method to insert new Directories to database
     * @param insertCallback Callback of the method
     * @param directories Directory array containing all directories to insert
     */
    void insertDirectory(@NonNull DaoCallbacks.Update<Directory> insertCallback,
                         Directory... directories)
    {
        new InsertDirAsyncTask(getWritableDatabase(), insertCallback, directories).execute();
    }

    /**
     * Method to update directories from the database
     * @param updateCallback Callback of the method
     * @param directories Directory array containing all directories to be updated
     */
    void updateDirectory(DaoCallbacks.Update<Directory> updateCallback, Directory... directories)
    {
        new UpdateDirAsyncTask(getWritableDatabase(), updateCallback, directories).execute();
    }

    /**
     * Method to delete directories from database
     * @param deleteCallback Callback of the method
     * @param ids Long array containing all the id's from the directories to be removed
     */
    void deleteDirectory(DaoCallbacks.Delete deleteCallback, Long... ids)
    {
        new DeleteDirAsyncTask(getWritableDatabase(), deleteCallback, ids).execute();
    }

    // ======================================================================== //
    //                             Dir : ASYNC TASKS
    // ======================================================================== //

    private static class SelectDirAsyncTask extends AsyncTask<Void,Void ,Void>
    {
        private SQLiteDatabase db;
        private Long id;
        private DaoCallbacks.Select<Directory> callback;

        private SelectDirAsyncTask(@NonNull SQLiteDatabase db,
                                        @Nullable Long id,
                                        @NonNull DaoCallbacks.Select<Directory> callback) {

            this.db = db;
            this.id = id;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (!db.isOpen())
            {
                throw new SQLException("Database instance is closed or is locked by other thread");
            }
            else
            {
                try(Cursor c = db.query(
                        DirectoryContract.TABLE,
                        DirectoryContract.COLUMNS,
                        id == null ? null : "_ID = ?",
                        id == null ? null : new String[]{id.toString()},
                        null,
                        null,
                        null))
                {
                    if (c != null && c.moveToFirst())
                    {
                        List<Directory> list = new ArrayList<>();
                        do {

                            Long _id = c.getLong(c.getColumnIndex(DirectoryContract.COLUMN_ID));
                            String name = c.getString(c.getColumnIndex(DirectoryContract.COLUMN_NAME));
                            Long parent = c.getLong(c.getColumnIndex(DirectoryContract.COLUMN_PARENT));
                            list.add(new Directory(_id, name, parent));

                        } while(c.moveToNext());

                        callback.onSelected(list.toArray(new Directory[0]));
                    }
                    else
                    {
                        callback.onSelected(new Directory[0]);
                    }
                }
            }
            return null;
        }
    }

    private static class SelectDirRootAsyncTask extends AsyncTask<Void,Void ,Void>
    {
        private SQLiteDatabase db;
        private DaoCallbacks.Select<Directory> callback;

        private SelectDirRootAsyncTask(@NonNull SQLiteDatabase db,
                                       @NonNull DaoCallbacks.Select<Directory> callback) {

            this.db = db;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (!db.isOpen())
            {
                throw new SQLException("Database instance is closed or is locked by other thread");
            }
            else
            {
                try(Cursor c = db.query(
                        DirectoryContract.TABLE,
                        DirectoryContract.COLUMNS,
                        DirectoryContract.COLUMN_PARENT +
                                " = SELECT " + DirectoryContract.COLUMN_ID + " FROM " +
                                DirectoryContract.TABLE +
                                " WHERE NAME = ?",
                        new String[] {"Root"},
                        null,
                        null,
                        null))
                {
                    if (c != null && c.moveToFirst())
                    {
                        List<Directory> list = new ArrayList<>();
                        do {

                            Long _id = c.getLong(c.getColumnIndex(DirectoryContract.COLUMN_ID));
                            String name = c.getString(c.getColumnIndex(DirectoryContract.COLUMN_NAME));
                            Long parent = c.getLong(c.getColumnIndex(DirectoryContract.COLUMN_PARENT));
                            list.add(new Directory(_id, name, parent));

                        } while(c.moveToNext());

                        callback.onSelected(list.toArray(new Directory[0]));
                    }
                    else
                    {
                        callback.onSelected(new Directory[0]);
                    }
                }
            }
            return null;
        }
    }

    private static class InsertDirAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private SQLiteDatabase db;
        private Directory[] directories;
        private DaoCallbacks.Update<Directory> callback;

        private InsertDirAsyncTask(SQLiteDatabase db, DaoCallbacks.Update<Directory> callback,
                                   Directory... directories)
        {
            this.db = db;
            this.directories = directories;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (!db.isOpen())
            {
                throw new SQLException("Database instance is closed or is locked by other thread");
            }
            else
            {
                ContentValues values = new ContentValues();
                List<Directory> result = new ArrayList<>();

                for (Directory dir : directories)
                {
                    if (dir.getName() != null && dir.getName().equals("Root"))
                    {
                        throw new SQLException("The directory name can't be 'Root', this name is" +
                                "required by the system");
                    }
                    values.clear();
                    values.put(DirectoryContract.COLUMN_NAME, dir.getName());
                    values.put(DirectoryContract.COLUMN_PARENT, dir.getParentId());

                    long id = db.insert(DirectoryContract.TABLE, null, values);
                    if (id != -1)
                    {
                        result.add(new Directory(id, dir.getName(), dir.getParentId()));
                    }
                }
                callback.onUpdated(result.toArray(new Directory[0]));
            }
            return null;
        }
    }

    private static class UpdateDirAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private SQLiteDatabase db;
        private DaoCallbacks.Update<Directory> callback;
        private Directory[] directories;

        private UpdateDirAsyncTask(SQLiteDatabase db, DaoCallbacks.Update<Directory> updateCallback,
                                   Directory... directories) {
            this.db = db;
            this.callback = updateCallback;
            this.directories = directories;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (!db.isOpen())
            {
                throw new SQLException("Database instance is closed or is locked by other thread");
            }
            else
            {
                ContentValues values = new ContentValues();
                List<Directory> result = new ArrayList<>();

                for (Directory dir : directories)
                {
                    values.clear();
                    values.put(DirectoryContract.COLUMN_ID, dir.get_id());
                    values.put(DirectoryContract.COLUMN_NAME, dir.getName());
                    values.put(DirectoryContract.COLUMN_PARENT, dir.getParentId());

                    db.update(DirectoryContract.TABLE,
                                             values,
                                 DirectoryContract.COLUMN_ID + " = ?",
                                             new String[]{String.valueOf(dir.get_id())});
                    result.add(dir);
                }
                callback.onUpdated(result.toArray(new Directory[0]));
            }
            return null;
        }
    }

    private static class DeleteDirAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private SQLiteDatabase db;
        private DaoCallbacks.Delete callback;
        private Long[] ids;

        private DeleteDirAsyncTask(SQLiteDatabase db, DaoCallbacks.Delete callback, Long... ids) {

            this.db = db;
            this.callback = callback;
            this.ids = ids;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (!db.isOpen())
            {
                throw new SQLException("");
            }
            else
            {
                int count = 0;
                for (Long id : ids)
                {
                    int affected = db.delete(DirectoryContract.TABLE,
                                  DirectoryContract.COLUMN_ID + " = ?",
                                   new String[]{ id.toString()} );
                    if (affected == 1)
                    {
                        count++;
                    }
                }
                if (count == ids.length)
                {
                    callback.onDelete(DaoCallbacks.DELETE_OK);
                }
                else if (count != 0)
                {
                    callback.onDelete(DaoCallbacks.DELETE_PARTIAL);
                }
                else
                {
                    callback.onDelete(DaoCallbacks.DELETE_FAIL);
                }
            }
            return null;
        }
    }

// ===============================================================================================//
//                                         ACCOUNT
// ===============================================================================================//

    /**
     * Method to retrieve accounts from database
     * @param id Long identifier of the accounts to select, if it is null, then all accounts
     *          will be selected
     * @param selectCallback Callback of the method
     */
    void selectAccount(@Nullable String id, @NonNull DaoCallbacks.Select<Account> selectCallback)
    {
        new SelectAccAsyncTask(getWritableDatabase(), id, selectCallback).execute();
    }

    /**
     * Method to retrieve accounts from database located at Root directory
     * @param selectCallback Callback of the method
     */
    void selectAccountRoot(@NonNull DaoCallbacks.Select<Account> selectCallback)
    {
        new SelectAccRootAsyncTask(getWritableDatabase(), selectCallback).execute();
    }

    /**
     * Method to insert new Accounts to database
     * @param insertCallback Callback of the method
     * @param accounts Account array containing all accounts to insert
     */
    void insertAccount(@NonNull DaoCallbacks.Update<Account> insertCallback,
                         Account... accounts)
    {
        new InsertAccAsyncTask(getWritableDatabase(), insertCallback, accounts).execute();
    }

    /**
     * Method to update accounts from the database
     * @param updateCallback Callback of the method
     * @param accounts Accounts array containing all accounts to be updated
     */
    void updateAccount(DaoCallbacks.Update<Account> updateCallback, Account... accounts)
    {
        new UpdateAccAsyncTask(getWritableDatabase(), updateCallback, accounts).execute();
    }

    /**
     * Method to delete accounts from database
     * @param deleteCallback Callback of the method
     * @param ids Long array containing all the id's from the accounts to be removed
     */
    void deleteAccount(DaoCallbacks.Delete deleteCallback, String... ids)
    {
        new DeleteAccAsyncTask(getWritableDatabase(), deleteCallback, ids).execute();
    }

    // ======================================================================== //
    //                             Acc : ASYNC TASKS
    // ======================================================================== //

    private static class SelectAccAsyncTask extends AsyncTask<Void,Void ,Void>
    {
        private SQLiteDatabase db;
        private String id;
        private DaoCallbacks.Select<Account> callback;

        private SelectAccAsyncTask(@NonNull SQLiteDatabase db,
                                   @Nullable String id,
                                   @NonNull DaoCallbacks.Select<Account> callback) {

            this.db = db;
            this.id = id;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (!db.isOpen())
            {
                throw new SQLException("Database instance is closed or is locked by other thread");
            }
            else
            {
                try(Cursor c = db.query(
                        AccountContract.TABLE,
                        AccountContract.COLUMNS,
                        id == null ? null : "_ID = ?",
                        id == null ? null : new String[]{id},
                        null,
                        null,
                        null))
                {
                    if (c != null && c.moveToFirst())
                    {
                        List<Account> list = new ArrayList<>();
                        String _id;
                        String account;
                        String user;
                        String url;
                        String pwd;
                        String brief;
                        Long create;
                        Long update;
                        boolean expire;
                        Long date_expire;
                        Long parent;
                        do {

                            _id = c.getString(c.getColumnIndex(AccountContract.COLUMN_ID));
                            account = c.getString(c.getColumnIndex(AccountContract.COLUMN_ACCOUNT));
                            user = c.getString(c.getColumnIndex(AccountContract.COLUMN_USER));
                            url = c.getString(c.getColumnIndex(AccountContract.COLUMN_URL));
                            pwd = c.getString(c.getColumnIndex(AccountContract.COLUMN_PWD));
                            brief = c.getString(c.getColumnIndex(AccountContract.COLUMN_BRIEF));
                            create = c.getLong(c.getColumnIndex(AccountContract.COLUMN_DATE_CREATE));
                            update = c.getLong(c.getColumnIndex(AccountContract.COLUMN_DATE_UPDATE));
                            expire = c.getInt(c.getColumnIndex(AccountContract.COLUMN_EXPIRE)) == 1;
                            date_expire = c.getLong(c.getColumnIndex(AccountContract.COLUMN_DATE_EXPIRE));
                            parent = c.getLong(c.getColumnIndex(AccountContract.COLUMN_PARENT));

                            list.add(new Account(_id,
                                                 account,
                                                 user,
                                                 url,
                                                 pwd,
                                                 brief,
                                                 expire,
                                                 create,
                                                 update,
                                                 date_expire,
                                                 parent));

                        } while(c.moveToNext());

                        callback.onSelected(list.toArray(new Account[0]));
                    }
                    else
                    {
                        callback.onSelected(new Account[0]);
                    }
                }
            }
            return null;
        }
    }

    private static class SelectAccRootAsyncTask extends AsyncTask<Void,Void ,Void>
    {
        private SQLiteDatabase db;
        private DaoCallbacks.Select<Account> callback;

        private SelectAccRootAsyncTask(@NonNull SQLiteDatabase db,
                                   @NonNull DaoCallbacks.Select<Account> callback) {

            this.db = db;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (!db.isOpen())
            {
                throw new SQLException("Database instance is closed or is locked by other thread");
            }
            else
            {
                try(Cursor c = db.query(
                        AccountContract.TABLE,
                        AccountContract.COLUMNS,
                        AccountContract.COLUMN_PARENT + " = " +
                                "SELECT " + DirectoryContract.COLUMN_ID + " FROM " +
                                DirectoryContract.TABLE + " WHERE " + DirectoryContract.COLUMN_NAME +
                                " = ?",
                        new String[]{"Root"},
                        null,
                        null,
                        null))
                {
                    if (c != null && c.moveToFirst())
                    {
                        List<Account> list = new ArrayList<>();
                        String _id;
                        String account;
                        String user;
                        String url;
                        String pwd;
                        String brief;
                        Long create;
                        Long update;
                        boolean expire;
                        Long date_expire;
                        Long parent;
                        do {

                            _id = c.getString(c.getColumnIndex(AccountContract.COLUMN_ID));
                            account = c.getString(c.getColumnIndex(AccountContract.COLUMN_ACCOUNT));
                            user = c.getString(c.getColumnIndex(AccountContract.COLUMN_USER));
                            url = c.getString(c.getColumnIndex(AccountContract.COLUMN_URL));
                            pwd = c.getString(c.getColumnIndex(AccountContract.COLUMN_PWD));
                            brief = c.getString(c.getColumnIndex(AccountContract.COLUMN_BRIEF));
                            create = c.getLong(c.getColumnIndex(AccountContract.COLUMN_DATE_CREATE));
                            update = c.getLong(c.getColumnIndex(AccountContract.COLUMN_DATE_UPDATE));
                            expire = c.getInt(c.getColumnIndex(AccountContract.COLUMN_EXPIRE)) == 1;
                            date_expire = c.getLong(c.getColumnIndex(AccountContract.COLUMN_DATE_EXPIRE));
                            parent = c.getLong(c.getColumnIndex(AccountContract.COLUMN_PARENT));

                            list.add(new Account(_id,
                                    account,
                                    user,
                                    url,
                                    pwd,
                                    brief,
                                    expire,
                                    create,
                                    update,
                                    date_expire,
                                    parent));

                        } while(c.moveToNext());

                        callback.onSelected(list.toArray(new Account[0]));
                    }
                    else
                    {
                        callback.onSelected(new Account[0]);
                    }
                }
            }
            return null;
        }
    }

    private static class InsertAccAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private SQLiteDatabase db;
        private Account[] accounts;
        private DaoCallbacks.Update<Account> callback;

        private InsertAccAsyncTask(SQLiteDatabase db, DaoCallbacks.Update<Account> callback,
                                   Account... accounts)
        {
            this.db = db;
            this.accounts = accounts;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (!db.isOpen())
            {
                throw new SQLException("Database instance is closed or is locked by other thread");
            }
            else
            {
                ContentValues values = new ContentValues();
                List<Account> result = new ArrayList<>();

                for (Account acc : accounts)
                {
                    values.clear();
                    values.put(AccountContract.COLUMN_ID, acc.get_id());
                    values.put(AccountContract.COLUMN_ACCOUNT, acc.getAccount());
                    values.put(AccountContract.COLUMN_USER, acc.getUser());
                    values.put(AccountContract.COLUMN_URL, acc.getUrl());
                    values.put(AccountContract.COLUMN_PWD, acc.getPassword());
                    values.put(AccountContract.COLUMN_BRIEF, acc.getBrief());
                    values.put(AccountContract.COLUMN_DATE_CREATE, acc.getDateCreate());
                    values.put(AccountContract.COLUMN_DATE_UPDATE, acc.getDateUpdate());
                    values.put(AccountContract.COLUMN_EXPIRE, acc.isExpire() ? 1 : 0);
                    values.put(AccountContract.COLUMN_DATE_EXPIRE, acc.getDateExpire());
                    values.put(AccountContract.COLUMN_PARENT, acc.getDirectoryId());

                    long id = db.insert(DirectoryContract.TABLE, null, values);
                    if (id != -1)
                    {
                        result.add(acc);
                    }
                }
                callback.onUpdated(result.toArray(new Account[0]));
            }
            return null;
        }
    }

    private static class UpdateAccAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private SQLiteDatabase db;
        private DaoCallbacks.Update<Account> callback;
        private Account[] accounts;

        private UpdateAccAsyncTask(SQLiteDatabase db, DaoCallbacks.Update<Account> updateCallback,
                                   Account... accounts) {
            this.db = db;
            this.callback = updateCallback;
            this.accounts = accounts;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (!db.isOpen())
            {
                throw new SQLException("Database instance is closed or is locked by other thread");
            }
            else
            {
                ContentValues values = new ContentValues();
                List<Account> result = new ArrayList<>();

                for (Account acc : accounts)
                {
                    values.clear();
                    values.put(AccountContract.COLUMN_ID, acc.get_id());
                    values.put(AccountContract.COLUMN_ACCOUNT, acc.getAccount());
                    values.put(AccountContract.COLUMN_USER, acc.getUser());
                    values.put(AccountContract.COLUMN_URL, acc.getUrl());
                    values.put(AccountContract.COLUMN_PWD, acc.getPassword());
                    values.put(AccountContract.COLUMN_BRIEF, acc.getBrief());
                    values.put(AccountContract.COLUMN_DATE_CREATE, acc.getDateCreate());
                    values.put(AccountContract.COLUMN_DATE_UPDATE, acc.getDateUpdate());
                    values.put(AccountContract.COLUMN_EXPIRE, acc.isExpire() ? 1 : 0);
                    values.put(AccountContract.COLUMN_DATE_EXPIRE, acc.getDateExpire());
                    values.put(AccountContract.COLUMN_PARENT, acc.getDirectoryId());

                    db.update(AccountContract.TABLE,
                            values,
                            AccountContract.COLUMN_ID + " = ?",
                            new String[]{acc.get_id()});
                    result.add(acc);
                }
                callback.onUpdated(result.toArray(new Account[0]));
            }
            return null;
        }
    }

    private static class DeleteAccAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private SQLiteDatabase db;
        private DaoCallbacks.Delete callback;
        private String[] ids;

        private DeleteAccAsyncTask(SQLiteDatabase db, DaoCallbacks.Delete callback, String... ids) {

            this.db = db;
            this.callback = callback;
            this.ids = ids;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (!db.isOpen())
            {
                throw new SQLException("");
            }
            else
            {
                int count = 0;
                for (String id : ids)
                {
                    int affected = db.delete(AccountContract.TABLE,
                            AccountContract.COLUMN_ID + " = ?",
                            new String[]{ id} );
                    if (affected == 1)
                    {
                        count++;
                    }
                }
                if (count == ids.length)
                {
                    callback.onDelete(DaoCallbacks.DELETE_OK);
                }
                else if (count != 0)
                {
                    callback.onDelete(DaoCallbacks.DELETE_PARTIAL);
                }
                else
                {
                    callback.onDelete(DaoCallbacks.DELETE_FAIL);
                }
            }
            return null;
        }
    }

// ===============================================================================================//
//                                            K
// ===============================================================================================//

// ===============================================================================================//
//                                       ACCOUNT - K
// ===============================================================================================//
}
