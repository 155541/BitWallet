package revolhope.splanes.com.bitwallet.db.contracts;

public interface KContract {

    String TABLE = "K";

    String COLUMN_ID = "_ID";
    String COLUMN_K = "_K";
    String COLUMN_CHECKSUM = "CHECKSUM";

    String[] COLUMNS = new String[] {COLUMN_ID, COLUMN_K, COLUMN_CHECKSUM};

    String STATEMENT_CREATE =
            "CREATE TABLE K("+
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_K + " BLOB NOT NULL," +
            COLUMN_CHECKSUM + " BLOB NOT NULL)";
}
