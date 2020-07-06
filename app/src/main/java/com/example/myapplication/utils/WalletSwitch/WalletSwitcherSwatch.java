package com.example.myapplication.utils.WalletSwitch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.db.adapters.DatabaseAdapter;
import com.example.myapplication.db.adapters.TransactionDbAdapter;
import com.example.myapplication.db.adapters.WalletDbAdapter;
import com.example.myapplication.model.Transaction;
import com.example.myapplication.model.Wallet;
import com.example.myapplication.ui.common.FormActivity;
import com.example.myapplication.ui.transaction.TransactionActivity;
import com.example.myapplication.ui.transaction.TransactionListFragment;

import java.util.List;

public class WalletSwitcherSwatch extends FrameLayout implements View.OnClickListener {
    private String wallet;
    private ImageView icon;
    private TextView name;
    private Button edit;
    private Button delete;
    private OnWalletSelectedListener walletSelectedListener;


    public interface OnWalletSelectedListener{
        public void onWalletSeletected(String wallet);

    }
    public WalletSwitcherSwatch(Context context, String _wallet, boolean chosen, OnWalletSelectedListener listener){
        super(context);

        wallet = _wallet;
        walletSelectedListener = listener;

        LayoutInflater.from(context).inflate(R.layout.wallet_switcher_swatch, this);
        icon = (ImageView) findViewById(R.id.wallet_switcher_swatch_icon);
        name = (TextView) findViewById(R.id.wallet_switcher_swatch_name);
        edit = (Button) findViewById(R.id.wallet_switcher_swatch_edit);
        delete = (Button) findViewById(R.id.wallet_switcher_swatch_delete);

        edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getContext(), FormActivity.class);
                intent.setAction(Intent.ACTION_INSERT_OR_EDIT);
                intent.putExtra("WALLET_UID", wallet);
                intent.putExtra("FORM_TYPE", FormActivity.FormType.WALLET.name());
//                Log.i("WalletSwitcherSwatch", FormActivity.FormType.WALLET.name());
//                Log.i("TransactionListFragment", "HELLLLLLLLLLLLLLLLLLLLLL" + transactionDbAdapter.getUID(id));

                getContext().startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                List<Transaction> transactionList = TransactionDbAdapter.getInstance().getAllTransactionsBelongTo(wallet);
                for (int i=0;i<transactionList.size();i++){
                    Transaction transaction = transactionList.get(i);
                    transaction.setWallet(wallet);
                    TransactionDbAdapter.getInstance().addRecord(transaction, DatabaseAdapter.UpdateMethod.update);
                }
                Wallet w = WalletDbAdapter.getInstance().getRecord(wallet);
                w.setDeleted(true);
                Log.i("Wallet_deletion", w.isDeleted()+"");
                WalletDbAdapter.getInstance().addRecord(w, DatabaseAdapter.UpdateMethod.update);


            }

        });
        setBackgroundColor(getResources().getColor(R.color.white));
        setWallet(_wallet);
        setChosen(chosen);
        setOnClickListener(this);
    }
    private void setWallet(String cat){
        icon.setImageResource(getResources().getIdentifier(WalletDbAdapter.getInstance().getRecord(cat).getIcon(), "mipmap",getContext().getPackageName()));
        name.setText(WalletDbAdapter.getInstance().getRecord(cat).getName());
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
