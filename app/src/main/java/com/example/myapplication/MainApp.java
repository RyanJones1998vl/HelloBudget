package com.example.myapplication;

import android.app.Application;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myapplication.db.DatabaseHelper;
import com.example.myapplication.db.adapters.DatabaseAdapter;
import com.example.myapplication.db.adapters.TransactionDbAdapter;
import com.example.myapplication.db.adapters.WalletDbAdapter;
import com.example.myapplication.model.Transaction;
import com.example.myapplication.model.Wallet;

import java.sql.Date;
import java.sql.Timestamp;

public class MainApp extends Application {
    private static Context context;
    private static TransactionDbAdapter transactionDbAdapter;
    private static WalletDbAdapter walletDbAdapter;

    private static DatabaseHelper mDbHelper;

    @Override
    public void onCreate(){
        super.onCreate();
        MainApp.context=getApplicationContext();

        initializeDatabaseAdapters();
        seedData();

    }
    public static void initializeDatabaseAdapters(){
        if(mDbHelper!=null){
            mDbHelper.getReadableDatabase().close();
        }

        mDbHelper=new DatabaseHelper(getAppContext());

        SQLiteDatabase mainDb;
        try{
            mainDb=mDbHelper.getWritableDatabase();

        }catch (SQLException sqlEx){
            mainDb=mDbHelper.getReadableDatabase();
        }
        transactionDbAdapter=new TransactionDbAdapter(mainDb);
        walletDbAdapter = new WalletDbAdapter(mainDb, transactionDbAdapter);

    }
    private void seedData(){
        Wallet total = new Wallet();
        total.setName("Total");
        total.setInitial_balance(0);
        total.setTotalExclusion(false);
        total.setCurrency("VND");
        total.setIcon("account_1");
        walletDbAdapter.addRecord(total, DatabaseAdapter.UpdateMethod.insert);
            Log.i("MainApp_DbAdapter", total.getUID());

        Wallet other = new Wallet();
        other.setName("Other");
        other.setInitial_balance(0);
        other.setTotalExclusion(true);
        other.setCurrency("VND");
        other.setIcon("account_2");
        walletDbAdapter.addRecord(total, DatabaseAdapter.UpdateMethod.insert);
        Log.i("MainApp_DbAdapter", other.getUID());


        for(int i =0;i<4;i++){
            Transaction transaction = new Transaction(300*i, "Bills", total.getUID());
            Timestamp date = new Timestamp(System.currentTimeMillis());
//            transaction.setDate(new Timestamp(System.currentTimeMillis()));

            transaction.setDay(Integer.valueOf(date.toString().substring(8,10)));
            transaction.setYear(Integer.valueOf(date.toString().substring(0,4)));
            transaction.setMonth(Integer.valueOf(date.toString().substring(5,7)));

            transaction.setReport_exclusion(false);
            transaction.setContent("Hello");
            transactionDbAdapter.addRecord(transaction, DatabaseAdapter.UpdateMethod.insert);
            Log.i("MainApp_DbAdapter", transactionDbAdapter.getRecordsCount()+" ");

//            Log.i("MainApp_DbAdapter", total.getUID());
//            Log.i("MainApp_DbAdapter", transaction.getWallet());
            Log.i("MainApp_DbAdapter", transaction.getDay() +" " +transaction.getMonth() + " " + transaction.getYear());
            Log.i("MainApp_DbAdapter", transaction.getDate().getClass().toString());


        }

    }
    public static TransactionDbAdapter getTransactionDbAdapter() {
        return transactionDbAdapter;
    }

    public static WalletDbAdapter getWalletDbAdapter() {

        return walletDbAdapter;

    }
    public static SQLiteDatabase getActiveDb(){
        return mDbHelper.getWritableDatabase();
    }

    public static Context getAppContext() {
        return MainApp.context;
    }


}
