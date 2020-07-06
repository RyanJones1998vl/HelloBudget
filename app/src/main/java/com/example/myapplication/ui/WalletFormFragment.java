package com.example.myapplication.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.myapplication.R;
import com.example.myapplication.db.adapters.DatabaseAdapter;
import com.example.myapplication.db.adapters.TransactionDbAdapter;
import com.example.myapplication.db.adapters.WalletDbAdapter;
import com.example.myapplication.db.adapters.WalletDbAdapter;
import com.example.myapplication.model.Transaction;
import com.example.myapplication.model.Wallet;
import com.example.myapplication.utils.CurrencyPicker.CurrencyPickerDialog;
import com.example.myapplication.utils.CurrencyPicker.CurrencyPickerSwatch;
import com.example.myapplication.utils.WalletIconPicker.IconPickerDialog;
import com.example.myapplication.utils.WalletIconPicker.IconPickerSwatch;
import com.example.myapplication.utils.WalletPicker.WalletPickerDialog;
import com.example.myapplication.utils.WalletPicker.WalletPickerSwatch;

import org.w3c.dom.Text;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Currency;

import static com.example.myapplication.db.adapters.DatabaseAdapter.UpdateMethod.insert;

public class WalletFormFragment extends Fragment {
    private WalletDbAdapter walletDbAdapter = WalletDbAdapter.getInstance();
    private Wallet wallet;

    EditText nameEdition=null;
    ImageView iconEdition = null;
    LinearLayout currencyLayout = null;
    ImageView currencyIcon = null;
    TextView currencyText = null;
    ImageView balanceIcon = null;
    TextView balanceTitle = null;
    EditText balanceEdition = null;
    CheckBox exclusionChecker = null;
    TextView exclusionTitle = null;
    TextView exclusionContent = null;

    Button saveButton = null;
    Button discardButton = null;

    String selectedWalletUID = "";
    String selectedName="";
    String selectedIcon = "";
    String selectedCurrency = "";
    int selectedBalance = 0;
    boolean selectedExclusion = false;

    public WalletFormFragment (){

    }
    private final CurrencyPickerSwatch.OnCurrencySelectedListener currencySelectedListener =
            new CurrencyPickerSwatch.OnCurrencySelectedListener(){
                @Override
                public void onCurrencySelected(String currency){
                    currencyText.setText(currency);
                    currencyIcon.setImageResource(getResources().getIdentifier(currency,"mipmap", getContext().getPackageName()));
                    selectedCurrency = currency;
                    currencyText.setTextColor(getResources().getColor(R.color.sky_blue));
                }
            };
    private final IconPickerSwatch.OnIconSelectedListener iconSelectedListener =
            new IconPickerSwatch.OnIconSelectedListener(){

                @Override
                public void onIconSelected(String icon){
                    selectedIcon = icon;
                    iconEdition.setImageResource(getResources().getIdentifier(selectedIcon.toLowerCase(), "mipmap", getContext().getPackageName()));
                }
            };

    static public WalletFormFragment newInstance(){
        WalletFormFragment formFragment = new WalletFormFragment();
        formFragment.walletDbAdapter = WalletDbAdapter.getInstance();
        return formFragment;
    }
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        walletDbAdapter = WalletDbAdapter.getInstance();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        View view = inflater.inflate(R.layout.fragment_wallet_form, container, false);

        nameEdition = view.findViewById(R.id.wallet_form_name);

        iconEdition = view.findViewById(R.id.wallet_form_category_icon);

        currencyLayout = view.findViewById(R.id.wallet_form_l_currency);
        currencyText = view.findViewById(R.id.wallet_form_currency_text);
        currencyIcon = view.findViewById(R.id.wallet_form_currency_icon);

        balanceIcon = view.findViewById(R.id.wallet_form_balance_icon);
        balanceTitle = view.findViewById(R.id.wallet_form_balance_title);
        balanceEdition = view.findViewById(R.id.wallet_form_balance);
        exclusionChecker = view.findViewById(R.id.wallet_form_exclusion);
        exclusionTitle = view.findViewById(R.id.wallet_form_exclusion_title);
        exclusionContent = view.findViewById(R.id.wallet_form_exclusion_content);

        saveButton = view.findViewById(R.id.wallet_form_button_save);
        discardButton = view.findViewById(R.id.wallet_form_button_discard);

        nameEdition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameEdition.setTextColor(getResources().getColor(R.color.black));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        iconEdition.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showIconPickerDialog();
            }
        });

        currencyLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showCurrencyPickerDialog();
            }
        });


        balanceEdition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0){
                    if(s.toString().matches("-(.*)")){
                        balanceEdition.setTextColor(getResources().getColor(R.color.red));
                    }
                    else if (s.toString().matches("\\d")){
                        balanceEdition.setTextColor(getResources().getColor(R.color.green));
                    } else{
//                        amountEditor.setText("0");

                    }
                    balanceTitle.setTextColor(getResources().getColor(R.color.black));
                    balanceIcon.setImageResource(getResources().getIdentifier("calendar_activated", "mipmap", getContext().getPackageName()));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        exclusionChecker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    exclusionTitle.setTextColor(getResources().getColor(R.color.black));
                    exclusionContent.setTextColor(getResources().getColor(R.color.black));
                    selectedExclusion=true;
                } else{
                    exclusionTitle.setTextColor(getResources().getColor(R.color.gray));
                    exclusionContent.setTextColor(getResources().getColor(R.color.gray));
                    selectedExclusion=false;
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                saveTransaction();
                finishFragment();
            }
        });
        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFragment();
            }
        });
        return view;
    }
    private void showIconPickerDialog(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Log.e("WalletFromFragment", "showIconPickerDialog");
        String currentIcon = "";
        if(wallet!=null){
            currentIcon = wallet.getIcon();
        }
        Log.e("WalletFromFragment", "showIconPickerDialog");

        IconPickerDialog iconPickerDialog = IconPickerDialog.newInstance(
                currentIcon);
        Log.e("WalletFromFragment", "showIconPickerDialog");
        iconPickerDialog.setOnIconSelectedListener(iconSelectedListener);
        Log.e("WalletFromFragment", "showIconPickerDialog");

        iconPickerDialog.show(fragmentManager, "Icon picker");


    }
    private void showCurrencyPickerDialog(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Log.e("WalletFromFragment", "showCurrencyPickerDialog");
        String currentCurrency = "";
        if(wallet!=null){
            currentCurrency = wallet.getIcon();
        }
        Log.e("WalletFromFragment", "showCurrencyPickerDialog");

        CurrencyPickerDialog currencyPickerDialog = CurrencyPickerDialog.newInstance(
                currentCurrency);
        Log.e("WalletFromFragment", "showCurrencyPickerDialog");
        currencyPickerDialog.setOnCurrencySelectedListener(currencySelectedListener);
        Log.e("WalletFromFragment", "showCurrencyPickerDialog");

        currencyPickerDialog.show(fragmentManager, "Currency picker");
    }
    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);

        selectedWalletUID = getArguments().getString("WALLET_UID");
//        Log.i("TransactionFormFragment", transactionUID);
        ActionBar actionBar= ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(selectedWalletUID!=null){
            wallet=walletDbAdapter.getRecord(selectedWalletUID);
            actionBar.setTitle("Edit");
            Log.i("Edited_wallet", selectedWalletUID);
        } else{
            actionBar.setTitle("Create");
        }
//        loadWalletList();
//        loadCategoryList();
        if(wallet!=null){
            initWithWallet(wallet);
            Log.i("Wallet uid ",wallet.getUID());
        } else{
            init();
        }
    }
    private void initWithWallet(Wallet wallet){
        if (wallet==null)
            throw new IllegalArgumentException("Transaction cannot be null");

        nameEdition.setText(wallet.getName());

        if(wallet.getIcon()!=null){
            iconEdition.setImageResource(getResources().getIdentifier(wallet.getIcon(), "mipmap", getContext().getPackageName()));
            selectedIcon = wallet.getIcon();
        }

        if(wallet.getCurrency()!=null){
            currencyText.setText(wallet.getCurrency());
            currencyIcon.setImageResource(getResources().getIdentifier(wallet.getCurrency(),"mipmap", getContext().getPackageName()));
            selectedCurrency = wallet.getCurrency();
            currencyText.setTextColor(getResources().getColor(R.color.sky_blue));
        }
        try{
            balanceEdition.setText(String.valueOf(wallet.getInitial_balance()));
            selectedBalance = wallet.getInitial_balance();

        } catch (Exception e){
            balanceEdition.setText("0");
            selectedBalance = 0;
        }
        balanceTitle.setTextColor(getResources().getColor(R.color.black));
        balanceIcon.setImageResource(getResources().getIdentifier("calendar_activated", "mipmap", getContext().getPackageName()));


        selectedExclusion=wallet.getTotalExclusion();
        exclusionChecker.setEnabled(selectedExclusion);
        if(selectedExclusion){
            exclusionTitle.setTextColor(getResources().getColor(R.color.black));
            exclusionContent.setTextColor(getResources().getColor(R.color.black));
        } else{
            exclusionTitle.setTextColor(getResources().getColor(R.color.gray));
            exclusionContent.setTextColor(getResources().getColor(R.color.gray));
        }
    }
    private void init(){

    }
    private void finishFragment(){
        /*InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(amountEditor.getWindowToken(), 0);*/

        final String action = getActivity().getIntent().getAction();
        if(action!=null && action.equals(Intent.ACTION_INSERT_OR_EDIT)){
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }else{
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private void saveTransaction(){
        Log.i("TransactionFormFragment", "Saving transaction");
        if(walletDbAdapter==null){
            Log.i("TransactionFormFragment", "Loading Transaction DB");
            walletDbAdapter = WalletDbAdapter.getInstance();
        }
        if(wallet==null){
            Log.i("TransactionFormFragment", "transaction is null");

            selectedName = nameEdition.getText().toString();
            selectedBalance = Integer.valueOf(balanceEdition.getText().toString());


            wallet = new Wallet();
            wallet.setName(selectedName);
            wallet.setInitial_balance(selectedBalance);
            wallet.setIcon(selectedIcon);
            wallet.setCurrency(selectedCurrency);
            wallet.setTotalExclusion(selectedExclusion);

            walletDbAdapter.addRecord(wallet, insert);
        } else{

            selectedName = nameEdition.getText().toString();
            selectedBalance = Integer.valueOf(balanceEdition.getText().toString());

//            wallet = new Wallet();
            wallet.setUID(selectedWalletUID);
            wallet.setName(selectedName);
            wallet.setInitial_balance(selectedBalance);
            wallet.setIcon(selectedIcon);
            wallet.setCurrency(selectedCurrency);
            wallet.setTotalExclusion(selectedExclusion);
            walletDbAdapter.addRecord(wallet, DatabaseAdapter.UpdateMethod.update);
            Log.i("Edited_wallet", wallet.getUID() + wallet.getIcon() + wallet.getCurrency());
//            transactionDbAdapter.updateRecord(transactionUID, DatabaseSchema.TransactionEntry.COLUMN_AMOUNT, amountEditor.getText().toString());
//            transactionDbAdapter.updateRecord(transactionUID, DatabaseSchema.TransactionEntry.COLUMN_CATERGORY, selectedCategory);

        }
        finishFragment();

    }
}
