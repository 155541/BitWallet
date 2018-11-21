package revolhope.splanes.com.bitwallet.db.contracts;

public interface AccountContract {

    String TABLE = "ACC";

    String COLUMN_ID = "_ID";
    String COLUMN_ACCOUNT = "ACCOUNT";
    String COLUMN_USER = "USER";
    String COLUMN_URL = "URL";
    String COLUMN_BRIEF = "BRIEF";
    String COLUMN_DATE_CREATE = "DATE_CREATE";
    String COLUMN_DATE_UPDATE = "DATE_UPDATE";
    String COLUMN_EXPIRE = "EXPIRE";
    String COLUMN_DATE_EXPIRE = "DATE_EXPIRE";
    String COLUMN_PARENT = "PARENT";

    String[] COLUMNS = new String[] {
            COLUMN_ID,COLUMN_ACCOUNT, COLUMN_USER, COLUMN_URL,
            COLUMN_BRIEF, COLUMN_DATE_CREATE, COLUMN_DATE_UPDATE,
            COLUMN_EXPIRE, COLUMN_DATE_EXPIRE, COLUMN_PARENT };

    String STATEMENT_CREATE =
            "CREATE TABLE ACC(" +
            COLUMN_ID + " VARCHAR(100) PRIMARY KEY," +
            COLUMN_ACCOUNT + " VARCHAR(50) NOT NULL," +
            COLUMN_USER + " VARCHAR(50) DEFAULT NULL," +
            COLUMN_URL + " VARCHAR(100) DEFAULT NULL," +
            COLUMN_BRIEF + " VARCHAR(200) DEFAULT NULL," +
            COLUMN_DATE_CREATE + " INTEGER NOT NULL," +
            COLUMN_DATE_UPDATE + " INTEGER DEFAULT NULL," +
            COLUMN_EXPIRE + " INTEGER NOT NULL DEFAULT 0 " +
                "CHECK(" + COLUMN_EXPIRE + " = 0 OR " + COLUMN_EXPIRE + " = 1)," +
            COLUMN_DATE_EXPIRE + " INTEGER DEFAULT NULL," +
            COLUMN_PARENT + " INTEGER NOT NULL," +
            "CONSTRAINT FK_ACC FOREIGN KEY (" + COLUMN_PARENT + ") REFERENCES " +
            DirectoryContract.TABLE + "(" + DirectoryContract.COLUMN_ID + ")" +
                " ON UPDATE CASCADE ON DELETE CASCADE)";
}
