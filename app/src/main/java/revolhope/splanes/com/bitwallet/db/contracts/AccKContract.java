package revolhope.splanes.com.bitwallet.db.contracts;

public interface AccKContract {

    String TABLE = "ACC_K";

    String COLUMN_ID = "_ID";
    String COLUMN_ACCID = "ACC_ID";
    String COLUMN_KID = "K_ID";
    String COLUMN_ACTIVE = "ACTIVE";
    String COLUMN_DEADLINE = "DEADLINE";

    String[] COLUMNS = new String[] {
            COLUMN_ID, COLUMN_ACCID, COLUMN_KID,
            COLUMN_ACTIVE, COLUMN_DEADLINE};


    String STATEMENT_CREATE =
            "CREATE TABLE ACC_K(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_ACCID + " INTEGER NOT NULL," +
                    COLUMN_KID + " INTEGER NOT NULL," +
                    COLUMN_ACTIVE + " INTEGER NOT NULL DEFAULT 0 " +
                        "CHECK(" + COLUMN_ACTIVE + " = 0 OR " + COLUMN_ACTIVE + " = 1)," +
                    COLUMN_DEADLINE + " INTEGER NOT NULL," +

                    "CONSTRAINT U_ACC_K UNIQUE(" + COLUMN_ACCID + ", " + COLUMN_KID + " )," +

                    "CONSTRAINT FK_ACC_K1 FOREIGN KEY(" + COLUMN_ACCID + ") REFERENCES " +
                    AccountContract.TABLE + "(" + AccountContract.COLUMN_ID + ")" +
                        " ON UPDATE CASCADE ON DELETE CASCADE," +

                    "CONSTRAINT FK_ACC_K2 FOREIGN KEY(" + COLUMN_KID + ") REFERENCES " +
                    KContract.COLUMN_ID + " ( " + KContract.COLUMN_ID + " )" +
                        " ON UPDATE CASCADE ON DELETE CASCADE)";

}
