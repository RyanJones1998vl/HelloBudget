package com.example.myapplication.db;

import android.provider.BaseColumns;

public class DatabaseSchema {
    private DatabaseSchema(){}
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "main_database";

    public interface CommonColumns extends BaseColumns {
        public static final String COLUMN_UID           = "uid";
        public static final String COLUMN_CREATED_AT    = "created_at";
        public static final String COLUMN_MODIFIED_AT   = "modified_at";
    }
    public static abstract class TransactionEntry implements CommonColumns{
        public static final String TABLE_NAME   =   "Transactions";

        public static final String COLUMN_AMOUNT    = "amount";
        public static final String COLUMN_CATERGORY = "category";
        public static final String COLUMN_DATE      = "date";
        public static final String COLUMN_DAY       = "day";
        public static final String COLUMN_MONTH     = "month";
        public static final String COLUMN_YEAR      = "year";
        public static final String COLUMN_WALLET_UID= "wallet_uid";
        public static final String COLUMN_CONTENT= "content";
        public static final String COLUMN_REPORT_EXCLUSION="report_exclusion";

        public static final String INDEX_UID        = "transaction_uid_index";

    }
    public static abstract class WalletEntry implements CommonColumns{
        public static final String TABLE_NAME   =   "Wallets";

        public static final String COLUMN_NAME      =   "name";
        public static final String COLUMN_CURRENCY  =   "currency";
        public static final String COLUMN_INITIAL_BALANCE   =   "initial_balance";
        public static final String COLUMN_TOTAL_EXCLUSION   =   "total_exclusion";
        public static final String COLUMN_ICON   =   "icon";
        public static final String COLUMN_DELETED   =   "deleted";

        public static final String INDEX_UID    =   "wallet_uid_index";
    }
}
