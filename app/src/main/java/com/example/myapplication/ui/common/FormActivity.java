package com.example.myapplication.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.R;
import com.example.myapplication.ui.WalletFormFragment;
import com.example.myapplication.ui.transaction.TransactionFormFragment;

public class FormActivity extends AppCompatActivity {
    private String transactionUID;
    public enum FormType {TRANSACTION, WALLET}
    private LinearLayout mainscreen_toolbar = null;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.transaction_form);

        String transUID = getIntent().getStringExtra("transaction uid");

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert (actionBar!=null);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_home_black_24dp);

        mainscreen_toolbar = findViewById(R.id.toolbar_mainscreen);
        mainscreen_toolbar.setVisibility(View.GONE);


        final Intent intent = getIntent();
        String formTypeString = intent.getStringExtra("FORM_TYPE");
        FormType formType = FormType.valueOf(formTypeString);

        switch (formType){
            case WALLET:
                showWalletFormFragment(getIntent().getExtras());
                break;
            case TRANSACTION:
            default:
                showTransactionFormFragment(getIntent().getExtras());
                break;
            /*case WALLET:
                showWalletFormFragment(getIntent().getExtras());
                break;*/
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showTransactionFormFragment(Bundle args){
        TransactionFormFragment transactionFormFragment = TransactionFormFragment.newInstance();
        transactionFormFragment.setArguments(args);
        showFormFragment(transactionFormFragment);
    }/*
    private void showWalletFormFragment(Bundle args){
        WalletFormFragment walletFormFragment = WalletFormFragment.newInstance();
        walletFormFragment.setArguments(args);
        showFormFragment(walletFormFragment);
    }*/
    private void showWalletFormFragment(Bundle args){
        WalletFormFragment walletFormFragment = WalletFormFragment.newInstance();
        walletFormFragment.setArguments(args);
        showFormFragment(walletFormFragment);
    }
    private void showFormFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

}
