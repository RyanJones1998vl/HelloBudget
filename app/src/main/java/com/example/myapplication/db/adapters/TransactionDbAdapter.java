package com.example.myapplication.db.adapters;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.MainApp;
import com.example.myapplication.db.DatabaseSchema.TransactionEntry;
import com.example.myapplication.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionDbAdapter extends DatabaseAdapter<Transaction> {
    public TransactionDbAdapter(SQLiteDatabase db){
        super(db, TransactionEntry.TABLE_NAME, new String[]{
                TransactionEntry.COLUMN_AMOUNT      ,
                TransactionEntry.COLUMN_CATERGORY   ,
                TransactionEntry.COLUMN_DAY        ,
                TransactionEntry.COLUMN_MONTH   ,
                TransactionEntry.COLUMN_YEAR,
                TransactionEntry.COLUMN_REPORT_EXCLUSION,
                TransactionEntry.COLUMN_WALLET_UID  ,
                TransactionEntry.COLUMN_CONTENT
        });
    }
    public static TransactionDbAdapter getInstance(){
        return MainApp.getTransactionDbAdapter();
    }
    @Override
    public void addRecord(@NonNull Transaction transaction, UpdateMethod updateMethod){
        Log.d(LOG_TAG, "Replace transaction to db");
        mDb.beginTransaction();
        try{
            super.addRecord(transaction, updateMethod);
        } catch (SQLException sqlEx){
            Log.e(LOG_TAG, sqlEx.getMessage());
        } finally {
            mDb.endTransaction();
        }
    }
    @Override
    protected @NonNull SQLiteStatement setBindings(@NonNull SQLiteStatement stmt, @NonNull Transaction transaction){
        stmt.clearBindings();
        stmt.bindLong(1, transaction.getAmount());
        stmt.bindString(2, transaction.getCategory());
        stmt.bindLong(3, transaction.getDay());
        stmt.bindLong(4, transaction.getMonth());
        stmt.bindLong(5, transaction.getYear());

        Log.i("TransactionDbAdapter", transaction.getDay()+" "+transaction.getMonth()+ " "+transaction.getYear());
        stmt.bindLong(6, transaction.getReport_exclusion()?1:0);
        stmt.bindString(7, transaction.getWallet());
        Log.i("TransactionDbAdapter", stmt.toString());

        stmt.bindString(8, transaction.getContent()!=null?transaction.getContent():"");
        stmt.bindString(9, transaction.getUID());

        Log.i("TransactionDbAdapter", stmt.toString());

        return stmt;
    }
    public List<Transaction> getAllTransactions(){
        Cursor cursor = fetchAllRecords();
        int count = 0;
        List<Transaction> transactions=new ArrayList<Transaction>();
        try{
            while(cursor.moveToNext()){
                Log.i("TransactionDbAdapter", "count trans" + ++count);
                transactions.add(buildModelInstance(cursor));
            }
        }finally {
            cursor.close();
        }
        return transactions;
    }
    public List<Transaction> getAllTransactionsBelongTo(String wallet){
        Cursor cursor = fetchAllRecords();
        List<Transaction> transactions = new ArrayList<Transaction>();
        try{
            while(cursor.moveToNext()){
                transactions.add(buildModelInstance(cursor));
            }
        }finally{
            cursor.close();
        }
        List<Transaction> result = new ArrayList<Transaction>();
        for(Transaction t :transactions){
            Log.i("wallet_tran", wallet);
            Log.i("wallet_tran", t.getWallet());
            if(t.getWallet().equals(wallet)){
                result.add(t);
                Log.i("wallet_tran", t.getWallet());
            }
        }
        Log.i("wallet_tran", result.size() +"");

        return result;
    }
    @Override
    public Transaction buildModelInstance(@NonNull final Cursor cursor){
        long amount = cursor.getLong(cursor.getColumnIndexOrThrow(TransactionEntry.COLUMN_AMOUNT));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(TransactionEntry.COLUMN_CATERGORY));
        String wallet = cursor.getString(cursor.getColumnIndexOrThrow(TransactionEntry.COLUMN_WALLET_UID));

        long day = cursor.getLong(cursor.getColumnIndexOrThrow(TransactionEntry.COLUMN_DAY));
        long month = cursor.getLong(cursor.getColumnIndexOrThrow(TransactionEntry.COLUMN_MONTH));
        long year = cursor.getLong(cursor.getColumnIndexOrThrow(TransactionEntry.COLUMN_YEAR));

        Transaction transaction = new Transaction(amount, category, wallet);
        populateBaseModelAttributes(cursor, transaction);

        transaction.setDay(day);
        transaction.setMonth(month);
        transaction.setYear(year);

        Log.i("TransactionDbAdapter",  "buildModelInstance "+transaction.getDay()+" "+transaction.getMonth()+ " "+transaction.getYear());

        transaction.setReport_exclusion(cursor.getInt(cursor.getColumnIndexOrThrow(TransactionEntry.COLUMN_REPORT_EXCLUSION))==1);
        return transaction;
    }
}
