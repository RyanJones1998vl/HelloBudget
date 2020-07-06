package com.example.myapplication.utils.WalletPicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.db.adapters.TransactionDbAdapter;
import com.example.myapplication.db.adapters.WalletDbAdapter;
import com.example.myapplication.model.Transaction;
import com.example.myapplication.model.Wallet;

import java.util.List;

public class WalletPickerSwatch extends FrameLayout implements View.OnClickListener {
    private String wallet;
    private ImageView icon;
    private TextView name;
    private TextView amount;
    private OnWalletSelectedListener walletSelectedListener;


    public interface OnWalletSelectedListener{
        public void onWalletSeletected(String wallet);

    }
    private int getBalance(String wallet){
        List<Transaction> trans = TransactionDbAdapter.getInstance().getAllTransactionsBelongTo(wallet);
        int balance = WalletDbAdapter.getInstance().getRecordWithName(wallet).getInitial_balance();
        for(Transaction t:trans){
            balance += t.getAmount();
        }
        return balance;
    }
    public WalletPickerSwatch(Context context, String _wallet, boolean chosen, OnWalletSelectedListener listener){
        super(context);

        wallet = _wallet;
        walletSelectedListener = listener;

        String iconName = WalletDbAdapter.getInstance().getRecordWithName(wallet).getIcon();
        int balance =  getBalance(wallet);

        Log.i("WalletPickerSwatch", "icon_" +iconName + " balance_"+balance);
        LayoutInflater.from(context).inflate(R.layout.wallet_picker_swatch, this);
        icon = (ImageView) findViewById(R.id.wallet_picker_swatch_icon);
        name = (TextView) findViewById(R.id.wallet_picker_swatch_name);
        amount = (TextView) findViewById(R.id.wallet_picker_swatch_balance);

        setWallet(_wallet, iconName, balance);
        setChosen(chosen);
        setOnClickListener(this);

        setBackgroundColor(getResources().getColor(R.color.milk));
    }
    private void setWallet(String cat, String iconName, int balance){
        icon.setImageResource(getResources().getIdentifier(iconName, "mipmap",getContext().getPackageName()));
        amount.setText(String.valueOf(balance));
        name.setText(cat);

//        name.setText(cat.split("_").toString());

    }
    private void setChosen(boolean cho){
        if(cho){
            name.setTextColor(Color.GREEN);
        }else{
            name.setTextColor(Color.BLACK);
        }
    }
    @Override
    public void onClick(View v){
        if(walletSelectedListener!=null){
            walletSelectedListener.onWalletSeletected(wallet);
        }
    }
}
