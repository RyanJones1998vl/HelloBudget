package com.example.myapplication.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myapplication.db.DatabaseSchema.TransactionEntry;
import com.example.myapplication.db.DatabaseSchema.WalletEntry;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = DatabaseHelper.class.getName();

    private static final String TRANSACTIONS_TABLE_CREATE = "create table " + DatabaseSchema.TransactionEntry.TABLE_NAME + " ("
            + TransactionEntry._ID              +   " integer primary key autoincrement, "
            + TransactionEntry.COLUMN_UID       +   " varchar(255) not null UNIQUE, "
            + TransactionEntry.COLUMN_CREATED_AT       + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
            + TransactionEntry.COLUMN_MODIFIED_AT      + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
            + TransactionEntry.COLUMN_AMOUNT    +   " integer NOT NULL, "
            + TransactionEntry.COLUMN_CATERGORY +   " varchar(255), "
//            + TransactionEntry.COLUMN_DATE      +   " TIMESTAMP NOT NULL, "
            + TransactionEntry.COLUMN_DAY       +   " integer NOT NULL, "
            + TransactionEntry.COLUMN_MONTH     +   " integer NOT NULL, "
            + TransactionEntry.COLUMN_YEAR      +   " integer NOT NULL, "

            + TransactionEntry.COLUMN_REPORT_EXCLUSION+   " tinyint default 0, "
            + TransactionEntry.COLUMN_WALLET_UID+   " varchar(255) not null, "
            + TransactionEntry.COLUMN_CONTENT   +   " varchar(255), "
            + "FOREIGN KEY (" 	+ TransactionEntry.COLUMN_WALLET_UID + ") REFERENCES " + WalletEntry.TABLE_NAME + " (" + WalletEntry.COLUMN_UID + ") "
            + ");" + createUpdatedAtTrigger(TransactionEntry.TABLE_NAME);

    private static final String WALLETS_TABLE_CREATE = "create table " + DatabaseSchema.WalletEntry.TABLE_NAME + " ("
            + WalletEntry._ID                   +   " integer primary key autoincrement, "
            + WalletEntry.COLUMN_UID            +   " varchar(255) not null UNIQUE, "
            + WalletEntry.COLUMN_CREATED_AT     +   " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
            + WalletEntry.COLUMN_MODIFIED_AT    +   " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
            + WalletEntry.COLUMN_NAME           +   " varchar(255) NOT NULL, "
            + WalletEntry.COLUMN_CURRENCY       +   " varchar(255) NOT NULL, "
            + WalletEntry.COLUMN_INITIAL_BALANCE+   " long, "
            + WalletEntry.COLUMN_TOTAL_EXCLUSION+   " long default 0, "
            + WalletEntry.COLUMN_DELETED        +   " long default 0, "
            + WalletEntry.COLUMN_ICON           +   " varchar(255) "
            + ");" + createUpdatedAtTrigger(WalletEntry.TABLE_NAME);


    private static final String SQL_DELETE_TRANSACTION_ENTRIES =
            "DROP TABLE IF EXISTS " + TransactionEntry.TABLE_NAME;
    private static final String SQL_DELETE_WALLET_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseSchema.WalletEntry.TABLE_NAME;


    public DatabaseHelper(Context context, String databaseName){
        super(context, databaseName, null, DatabaseSchema.DATABASE_VERSION);
    }
    public DatabaseHelper(Context context){
        super(context, DatabaseSchema.DATABASE_NAME, null, DatabaseSchema.DATABASE_VERSION);
    }
    static String createUpdatedAtTrigger(String tableName){
        return "CREATE TRIGGER update_time_trigger "
                + "  AFTER UPDATE ON " + tableName + " FOR EACH ROW"
                + "  BEGIN " + "UPDATE " + tableName
                + "  SET " + DatabaseSchema.CommonColumns.COLUMN_MODIFIED_AT + " = CURRENT_TIMESTAMP"
                + "  WHERE OLD." + DatabaseSchema.CommonColumns.COLUMN_UID + " = NEW." + DatabaseSchema.CommonColumns.COLUMN_UID + ";"
                + "  END;";
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOG_TAG, "Creating database tables");
        createDatabaseTables(db);
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        Log.i(LOG_TAG, "Opening database tables");
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");

    }
    private void createDatabaseTables(SQLiteDatabase db){
        Log.i(LOG_TAG, "Creating database tables");

        db.execSQL(SQL_DELETE_TRANSACTION_ENTRIES);


        db.execSQL(TRANSACTIONS_TABLE_CREATE);
        db.execSQL(WALLETS_TABLE_CREATE);

        String createTransactionUidIndex = "CREATE UNIQUE INDEX '" + TransactionEntry.INDEX_UID + "' ON "
                + TransactionEntry.TABLE_NAME + "(" + TransactionEntry.COLUMN_UID + ")";
        String createWalletUidIndex = "CREATE UNIQUE INDEX '" + WalletEntry.INDEX_UID + "' ON "
                + WalletEntry.TABLE_NAME + "(" + WalletEntry.COLUMN_UID + ")";

        db.execSQL(createTransactionUidIndex);
        db.execSQL(createWalletUidIndex);

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                Log.i("DatabaseHelper", c.getString(0));
                c.moveToNext();
            }
        }

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.i(LOG_TAG, "Upgrading database from version "
                + oldVersion + " to " + newVersion);

        db.execSQL(SQL_DELETE_TRANSACTION_ENTRIES);
        db.execSQL(SQL_DELETE_WALLET_ENTRIES);
        onCreate(db);

    }
}
