package com.example.myapplication.db.adapters;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.MainApp;
import com.example.myapplication.db.DatabaseSchema;
import com.example.myapplication.model.Wallet;

import java.util.ArrayList;
import java.util.List;

public class WalletDbAdapter extends DatabaseAdapter<Wallet> {
    private final TransactionDbAdapter transactionDbAdapter;

    public WalletDbAdapter(SQLiteDatabase db, TransactionDbAdapter _transactionDbAdapter){
        super(db, DatabaseSchema.WalletEntry.TABLE_NAME, new String[]{
                DatabaseSchema.WalletEntry.COLUMN_NAME,
                DatabaseSchema.WalletEntry.COLUMN_CURRENCY,
                DatabaseSchema.WalletEntry.COLUMN_INITIAL_BALANCE,
                DatabaseSchema.WalletEntry.COLUMN_TOTAL_EXCLUSION,
                DatabaseSchema.WalletEntry.COLUMN_ICON,
                DatabaseSchema.WalletEntry.COLUMN_DELETED
        });
        this.transactionDbAdapter=_transactionDbAdapter;
    }
    public WalletDbAdapter(SQLiteDatabase db){
        super(db, DatabaseSchema.WalletEntry.TABLE_NAME, new String[]{
                DatabaseSchema.WalletEntry.COLUMN_NAME,
                DatabaseSchema.WalletEntry.COLUMN_CURRENCY,
                DatabaseSchema.WalletEntry.COLUMN_INITIAL_BALANCE,
                DatabaseSchema.WalletEntry.COLUMN_TOTAL_EXCLUSION,
                DatabaseSchema.WalletEntry.COLUMN_ICON

        });
        this.transactionDbAdapter= new TransactionDbAdapter(db);
    }
    public static WalletDbAdapter getInstance(){
        return MainApp.getWalletDbAdapter();
    }

    public TransactionDbAdapter getTransactionDbAdapter() {
        return transactionDbAdapter;
    }
    @Override
    public void addRecord(@NonNull Wallet wallet, UpdateMethod updateMethod){
        Log.d(LOG_TAG, "Replace transaction to db");
        mDb.beginTransaction();
        try{
            super.addRecord(wallet, updateMethod);
            Log.i("WalletDbAdapter", wallet.toString());
        } catch (SQLException sqlEx){
            Log.e(LOG_TAG, sqlEx.getMessage());
            Log.i("WalletDbAdapter", "catch");

        } finally {
            mDb.endTransaction();
            Log.i("WalletDbAdapter", Long.toString(this.getRecordsCount()));

        }
    }
    public Wallet getRecordWithName(@NonNull String name){
        Cursor cursor = fetchRecordWithName(name);
        try {
            if (cursor.moveToFirst()) {
                return buildModelInstance(cursor);
            }
            else {
                throw new IllegalArgumentException(LOG_TAG + ": Record with " + name + " does not exist");
            }
        } finally {
            cursor.close();
        }
    }

    public Cursor fetchRecordWithName(String name){
        return mDb.query(mTableName, null, DatabaseSchema.WalletEntry.COLUMN_NAME + "=?" ,
                new String[]{name}, null, null, null);
    }
    @Override
    protected @NonNull
    SQLiteStatement setBindings(@NonNull SQLiteStatement stmt, @NonNull Wallet wallet){

        Log.d("TransactionDbAdapter", stmt.toString() + wallet.getName());
        stmt.clearBindings();
        stmt.bindString(1, wallet.getName());
        stmt.bindString(2, wallet.getCurrency());
        stmt.bindLong(3, wallet.getInitial_balance());
        stmt.bindLong(4, wallet.getTotalExclusion()?1:0);
        stmt.bindString(5, wallet.getIcon());
        stmt.bindLong(6, wallet.isDeleted()==false?0:1);

        stmt.bindString(7, wallet.getUID());

        Log.d("TransactionDbAdapter", stmt.toString() + wallet.getName());

//        ContentValues contentValues = new ContentValues();
//        contentValues.put(DatabaseSchema.WalletEntry.COLUMN_NAME, wallet.getName());
//        contentValues.put(DatabaseSchema.WalletEntry.COLUMN_CURRENCY, wallet.getCurrency());
//        contentValues.put(DatabaseSchema.WalletEntry.COLUMN_INITIAL_BALANCE, wallet.getInitial_balance());
//        contentValues.put(DatabaseSchema.WalletEntry.COLUMN_TOTAL_EXCLUSION, wallet.getTotalExclusion());
//        contentValues.put(DatabaseSchema.WalletEntry.COLUMN_UID, wallet.generateUID());
//        long rowID = mDb.insert(DatabaseSchema.WalletEntry.TABLE_NAME, null, contentValues);
//        Log.d("TransactionDbAdapter", contentValues.toString() + " " + Long.toString(rowID));

        return stmt;
    }
    public List<Wallet> getAllWallets(){
        Cursor cursor = fetchAllRecords();
        List<Wallet> wallets=new ArrayList<Wallet>();
        try{
            while(cursor.moveToNext()){
                wallets.add(buildModelInstance(cursor));
            }
        }finally {
            cursor.close();
        }
        List<Wallet> result=new ArrayList<Wallet>();
        for(Wallet w: wallets){
            if (w.isDeleted()!=true){
                Log.i("Load_wallet_isDelete", "deletion is false");

                result.add(w);
            }
//            Log.i("Load_wallet_isDelete", "deletion is true");
//            result.add(w);
        }
        return result;
    }
    @Override
    public Wallet buildModelInstance(@NonNull final Cursor cursor){
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseSchema.WalletEntry.COLUMN_NAME));
        String currency = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseSchema.WalletEntry.COLUMN_CURRENCY));
        int intial = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseSchema.WalletEntry.COLUMN_INITIAL_BALANCE));


        Wallet wallet = new Wallet();
        populateBaseModelAttributes(cursor, wallet);

        wallet.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseSchema.WalletEntry.COLUMN_NAME)));
        wallet.setCurrency(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseSchema.WalletEntry.COLUMN_CURRENCY)));
        wallet.setInitial_balance(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseSchema.WalletEntry.COLUMN_INITIAL_BALANCE)));
        wallet.setTotal_exclusion(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseSchema.WalletEntry.COLUMN_TOTAL_EXCLUSION))==1);
        wallet.setIcon(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseSchema.WalletEntry.COLUMN_ICON)));
        wallet.setDeleted(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseSchema.WalletEntry.COLUMN_DELETED))==1);
        return wallet;
    }
}
