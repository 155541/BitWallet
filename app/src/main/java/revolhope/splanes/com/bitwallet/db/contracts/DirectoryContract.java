package revolhope.splanes.com.bitwallet.db.contracts;

public interface DirectoryContract {

    String TABLE = "DIR";

    String COLUMN_ID = "_ID";
    String COLUMN_NAME = "NAME";
    String COLUMN_PARENT = "PARENT";

    String[] COLUMNS = new String[] {COLUMN_ID, COLUMN_NAME, COLUMN_PARENT};

    String STATEMENT_CREATE =
            "CREATE TABLE DIR(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME + " VARCHAR(50) NOT NULL," +
            COLUMN_PARENT + " INTEGER," +
            "CONSTRAINT FK_DIR_DIR FOREIGN KEY ("+ COLUMN_PARENT +") " +
            "REFERENCES " + TABLE + "(" + COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE)";

    String STATEMENT_INSERT_ROOT = "INSERT INTO DIR(NAME,PARENT) " +
                                    "VALUES('Root',NULL)";
}
