package revolhope.splanes.com.bitwallet.db.contracts;

public interface KContract {

    String TABLE = "K";

    String COLUMN_ID = "_ID";
    String COLUMN_ACC_ID = "ACC_ID";
    String COLUMN_CRYPTO_PWD = "CRYPTO";
    String COLUMN_PARAMS = "PARAMS";
    String COLUMN_DEADLINE = "DEADLINE";

    String[] COLUMNS = new String[] {COLUMN_ID, COLUMN_ACC_ID, COLUMN_CRYPTO_PWD,
                                     COLUMN_PARAMS, COLUMN_DEADLINE};

    String STATEMENT_CREATE =
            "CREATE TABLE K(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_ACC_ID + " VARCHAR() NOT NULL,
            COLUMN_CRYPTO_PWD + " BLOB NOT NULL," +
            COLUMN_PARAMS + " BLOB NOT NULL," +
            COLUMN_DEADLINE + " INTEGER NOT NULL, " +
            "CONTRAINT FK_K FOREIGN KEY (" + COLUMN_ACC_ID + ") REFERENCES " + 
            AccountContract.TABLE + "(" + AccountContract.COLUMN_ID + ") " + 
            "ON DELETE CASCADE ON UPDATE CASCADE, " + 
            "CONTRAINT U_K UNIQUE(" + COLUMN_ACC_ID + "))";
}
