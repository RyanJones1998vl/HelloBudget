package com.example.myapplication.ui.transaction;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.myapplication.R;
import com.example.myapplication.db.DatabaseSchema;
import com.example.myapplication.db.adapters.DatabaseAdapter;
import com.example.myapplication.db.adapters.TransactionDbAdapter;
import com.example.myapplication.db.adapters.WalletDbAdapter;
import com.example.myapplication.model.Transaction;
import com.example.myapplication.model.Wallet;
import com.example.myapplication.ui.common.FormActivity;
import com.example.myapplication.utils.CategoryPicker.CategoryPickerDialog;
import com.example.myapplication.utils.CategoryPicker.CategoryPickerSwatch;
import com.example.myapplication.utils.WalletPicker.WalletPickerDialog;
import com.example.myapplication.utils.WalletPicker.WalletPickerSwatch;

import org.w3c.dom.Text;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.myapplication.db.adapters.DatabaseAdapter.UpdateMethod.insert;

public class TransactionFormFragment extends Fragment {
    private TransactionDbAdapter transactionDbAdapter;

    private Transaction transaction=null;
    ActionBar actionBar=null;
    EditText amountEditor=null;
    EditText contentEditor = null;
    ImageView contentIcon = null;
    ImageView datePicker=null;
    ImageView categoryPicker=null;
    TextView categoryText = null;
    ImageView categoryIcon = null;
    CheckBox exclusionChecker=null;
    LinearLayout walletPicker=null;
    TextView walletText = null;
    ImageView walletIcon = null;
    DatePickerDialog datePickerDialog;
    TextView selectedDateText = null;
    Button saveButton = null;
    Button discardButton = null;
    TextView exclusionText = null;

    LinearLayout mainscreen_toolbar = null;

    ImageView amountTicker = null;
    ImageView categoryTicker = null;
    ImageView noteTicker = null;
    ImageView dateTicker = null;
    ImageView walletTicker = null;
    TextView highlightedText = null;

    private String selectedWallet = "Total";
    private String selectedCategory = "";
    private Timestamp selectedDate = null;

    private String transactionUID = null;

    int isAmountFilled = 0;
    int isCategoryFilled = 0;
    int isNoteFilled = 0;
    int isDateFilled = 0;
    int isTotalFilled = 0;

    boolean isAcceptable = false;
    public TransactionFormFragment(){

    }

    private final WalletPickerSwatch.OnWalletSelectedListener walletSelectedListener =
            new WalletPickerSwatch.OnWalletSelectedListener(){
                @Override
                public void onWalletSeletected(String wallet){
                    walletText.setText(wallet);
                    walletIcon.setImageResource(getResources().getIdentifier(WalletDbAdapter.getInstance().getRecordWithName(selectedWallet).getIcon(),
                            "mipmap", getContext().getPackageName()));
                    selectedWallet = wallet;
                    walletText.setTextColor(getResources().getColor(R.color.black));
                }
            };
    private final CategoryPickerSwatch.OnCategorySelectedListener categorySelectedListener =
            new CategoryPickerSwatch.OnCategorySelectedListener(){

                @Override
                public void onCategorySeletected(String category){
                    categoryText.setText(category);
                    categoryIcon.setImageResource(getResources().getIdentifier(category.toLowerCase(), "mipmap", getContext().getPackageName()));
//                    categoryIcon.setImageResource();
                    selectedCategory = category;
                    categoryText.setTextColor(getResources().getColor(R.color.black));
                }
            };

    static public TransactionFormFragment newInstance(){
        TransactionFormFragment formFragment = new TransactionFormFragment();
        formFragment.transactionDbAdapter = TransactionDbAdapter.getInstance();
        return formFragment;
    }
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        transactionDbAdapter = TransactionDbAdapter.getInstance();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        View view = inflater.inflate(R.layout.fragment_transaction_form, container, false);
        amountEditor = view.findViewById(R.id.transaction_form_amount);
        contentEditor = view.findViewById(R.id.transaction_form_content);
        datePicker = view.findViewById(R.id.transaction_form_date);
        categoryPicker = view.findViewById(R.id.transaction_form_category_icon);
        categoryText = view.findViewById(R.id.transaction_form_category);
        categoryIcon = view.findViewById(R.id.transaction_form_category_icon);
        exclusionChecker = view.findViewById(R.id.transaction_form_exclusion);
        walletPicker = view.findViewById(R.id.transaction_form_l_wallet);
        walletText = view.findViewById(R.id.transaction_form_wallet);
        walletIcon = view.findViewById(R.id.transaction_form_wallet_icon);
        selectedDateText = view.findViewById(R.id.transaction_form_selected_date);
        saveButton = view.findViewById(R.id.transaction_form_button_save);
        discardButton = view.findViewById(R.id.transaction_form_button_discard);
        contentIcon = view.findViewById(R.id.transaction_form_content_icon);
        exclusionText = view.findViewById(R.id.transaction_form_exclusion_title);



        amountTicker = view.findViewById(R.id.transaction_form_ticker_amount);
        categoryTicker = view.findViewById(R.id.transaction_form_ticker_category);
        dateTicker = view.findViewById(R.id.transaction_form_ticker_date);
        noteTicker = view.findViewById(R.id.transaction_form_ticker_note);
        walletTicker = view.findViewById(R.id.transaction_form_ticker_wallet);

        highlightedText = view.findViewById(R.id.transaction_form_highlighted_text);

        amountTicker.setVisibility(GONE);
        categoryTicker.setVisibility(GONE);
        dateTicker.setVisibility(GONE);
        noteTicker.setVisibility(GONE);
        walletTicker.setVisibility(GONE);
        highlightedText.setVisibility(GONE);

        saveButton.setBackgroundColor(getResources().getColor(R.color.milk));
        saveButton.setTextColor(getResources().getColor(R.color.green));


        if(transaction!=null){
            walletText.setText(WalletDbAdapter.getInstance().getRecord(transaction.getWallet()).toString());
            walletIcon.setImageResource(getResources().getIdentifier(WalletDbAdapter.getInstance().getRecord(transaction.getWallet()).getIcon(),
                    "mipmap", getContext().getPackageName()));
        }

        amountEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0){
                    try{
                        int amount = Integer.valueOf(s.toString());
                        if(amount<0){
                            amountEditor.setTextColor(getResources().getColor(R.color.red));
                        }else{
                            amountEditor.setTextColor(getResources().getColor(R.color.green));
                        }
                        isAmountFilled=1;
                        amountTicker.setVisibility(GONE);

                        Log.i("TransactionFormFragmen_amount", "change_to_"+isAmountFilled+"_"+amount);

                    } catch(Exception e){
                        isAmountFilled=-1;
                        Log.i("TransactionFormFragmen_amount", "change_to_"+isAmountFilled);
                        amountTicker.setVisibility(View.VISIBLE);
                    }
//                    if(s.toString().matches("-(.*)")){
//                        amountEditor.setTextColor(getResources().getColor(R.color.red));
//                        isAmountFilled=1;
//                        int amount = Integer.valueOf(amountEditor.getText().toString());
//
//                        Log.i("TransactionFormFragmen_amount", "change_to_"+isAmountFilled+"_"+amount);
//
////                        try {
////                            int amount = Integer.valueOf(amountEditor.getText().toString());
////                            isAmountFilled=1;
////                            amountTicker.setVisibility(View.GONE);
////
////                        } catch (Exception e){
////                            amountTicker.setVisibility(View.VISIBLE);
////                        }
//                    }
//                    else if (s.toString().matches("\\d")){
//                        amountEditor.setTextColor(getResources().getColor(R.color.green));
//                        isAmountFilled=1;
//                        int amount = Integer.valueOf(amountEditor.getText().toString());
//
//                        Log.i("TransactionFormFragmen_amount", "change_to_"+isAmountFilled+"_"+amount);
//
////                        try {
////                            int amount = Integer.valueOf(amountEditor.getText().toString());
////                            isAmountFilled=1;
////                            amountTicker.setVisibility(View.GONE);
////
////                        } catch (Exception e){
////                            amountTicker.setVisibility(View.VISIBLE);
////                        }
//                    } else{
////                        amountEditor.setText("0");
////                        amountTicker.setVisibility(View.VISIBLE);
//                        isAmountFilled=-1;
//                        int amount = Integer.valueOf(amountEditor.getText().toString());
//
//                        Log.i("TransactionFormFragmen_amount", "change_to_"+isAmountFilled+"_"+amount);
//
//                    }
                    if(isAmountFilled==1 && isCategoryFilled==1){
                        saveButton.setTextColor(getResources().getColor(R.color.white));
                        saveButton.setBackgroundColor(getResources().getColor(R.color.green));

                        isAcceptable=true;
                        highlightedText.setVisibility(GONE);
                    }
                    if(isAmountFilled!=1){
                        saveButton.setTextColor(getResources().getColor(R.color.green));
                        saveButton.setBackgroundColor(getResources().getColor(R.color.milk));
//                        highlightedText.setVisibility(View.VISIBLE);
                        isAcceptable=false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        contentEditor.setHint("Write note");
        contentEditor.setText("");
        contentEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0){
                    if( s.toString()!="Write note"){
                        contentIcon.setImageResource(R.mipmap.note_activated);
                        contentEditor.setTextColor(getResources().getColor(R.color.black));
                    }
//                    else{
//                        contentIcon.setImageResource(R.mipmap.note_deactivated);
//                        contentEditor.setTextColor(getResources().getColor(R.color.gray));
//                    }
                    if(noteTicker.getVisibility()==VISIBLE){
                        noteTicker.setVisibility(GONE);
                    }
                    isNoteFilled=1;
                }
                else{
//                    s = "Write note";
                    contentIcon.setImageResource(R.mipmap.note_deactivated);
                    contentEditor.setTextColor(getResources().getColor(R.color.gray));
                    isNoteFilled=-1;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        categoryPicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showCategoryPickerDialog();
                isCategoryFilled=1;
                if(isAmountFilled!=-1){
                    saveButton.setTextColor(getResources().getColor(R.color.white));
                    saveButton.setBackgroundColor(getResources().getColor(R.color.green));
                }
            }
        });
        walletPicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showWalletPickerDialog();
                if(walletTicker.getVisibility()==VISIBLE){
                    walletTicker.setVisibility(GONE);
                }
                isTotalFilled=1;

            }
        });
        exclusionChecker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    exclusionText.setTextColor(getResources().getColor(R.color.black));
                } else{
                    exclusionText.setTextColor(getResources().getColor(R.color.gray));
                }
            }
        });
        datePicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (dateTicker.getVisibility() != GONE){
                    dateTicker.setVisibility(GONE);
                }
                final Calendar calendar = Calendar.getInstance();
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                final int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int yr, int mth, int d){
                        selectedDateText.setTextColor(getResources().getColor(R.color.black));
                        datePicker.setImageResource(R.mipmap.calendar_activated);
                        if(new Date(System.currentTimeMillis())==new Date(yr, mth, d)){
                            selectedDateText.setText("Today");
                        } else{
                            selectedDateText.setText(yr + "-" + String.format("%02d", mth+1)  + "-" + String.format("%02d", d));

                        }
                        Log.i("TransactionFormFragment", selectedDateText.getText().toString());

                        selectedDate = Timestamp.valueOf(selectedDateText.getText().toString() + " 10:59:26");
                        Log.i("TransactionFormFragment", selectedDate.toString());
                    }
                }, year, month, day);
                datePickerDialog.show();
                if(dateTicker.getVisibility()==VISIBLE){
                    dateTicker.setVisibility(GONE);
                }
                isDateFilled=1;

            }
        });
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                saveTransaction();
                if(isAcceptable){
                    finishFragment();
                }
            }
        });
        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFragment();
            }
        });
//        amountEditor.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                if (amountTicker.getVisibility() != GONE){
//                    amountTicker.setVisibility(GONE);
//                }
//            }
//        });
//        contentEditor.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                if (noteTicker.getVisibility() != GONE){
//                    noteTicker.setVisibility(GONE);
//                }
//            }
//        });
//        categoryIcon.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                if (categoryTicker.getVisibility() != GONE){
//                    categoryTicker.setVisibility(GONE);
//                }
//            }
//        });
//        walletIcon.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                if (walletTicker.getVisibility() != GONE){
//                    walletTicker.setVisibility(GONE);
//                }
//            }
//        });
        return view;
    }
    private void showCategoryPickerDialog(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        String currentCategory = "";
        if(transaction!=null){
            currentCategory = transaction.getCategory();

        }

        CategoryPickerDialog categoryPickerDialog = CategoryPickerDialog.newInstance(
                2,
                getCategoryOption(),
                selectedCategory);
        categoryPickerDialog.setOnCategorySelectedListener(categorySelectedListener);
        categoryPickerDialog.show(fragmentManager, "Category picker");

    }
    public String[] getCategoryOption(){
        String[] categories = new String[] {"Bills", "Entertainment", "Essential", "Gadget", "Game", "Gift", "Interest",
                                            "Music", "Others", "Shopping", "Study", "Tech"};
        return categories;
    }
    private void showWalletPickerDialog(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Log.e("TransactionFormFragment", "showWalletPickerDialog");
        String currentWallet = "";
        if(transaction!=null){
            currentWallet = transaction.getWallet();
        }
        Log.e("TransactionFormFragment", "showWalletPickerDialog");

        WalletPickerDialog walletPickerDialog = WalletPickerDialog.newInstance(
                2,
                getWalletOptions(),
                currentWallet);
        Log.e("TransactionFormFragment", "showWalletPickerDialog");
        walletPickerDialog.setOnWalletSelectedListener(walletSelectedListener);
        Log.e("TransactionFormFragment", "showWalletPickerDialog");

        walletPickerDialog.show(fragmentManager, "Category picker");
    }
    private String[] getWalletOptions(){
        List<Wallet> wallets = WalletDbAdapter.getInstance().getAllWallets();
        if(wallets.size()==0){
            Wallet wallet = new Wallet();
            wallet.setName("Total");
            wallet.setTotalExclusion(true);
            wallet.setCurrency("VND");
            WalletDbAdapter.getInstance().addRecord(wallet, insert);
            return getWalletOptions();
        }
        String[] wName = new String[wallets.size()];
        for (int i =0;i<wallets.size();i++){
            wName[i] = wallets.get(i).getName();
        }
        return wName;
    }
    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);

        transactionUID = getArguments().getString("TRANSACTION_UID");
//        Log.i("TransactionFormFragment", transactionUID);
        actionBar= ((AppCompatActivity)getActivity()).getSupportActionBar();

        if(transactionUID!=null){
            transaction=transactionDbAdapter.getRecord(transactionUID);
            actionBar.setTitle("Edit");
            initWithTransaction(transaction);
        } else{
            actionBar.setTitle("Create");

            init();

        }
//        loadWalletList();
//        loadCategoryList();

    }
    private void initWithTransaction(Transaction transaction){
        if (transaction==null)
            throw new IllegalArgumentException("Transaction cannot be null");
        long amount = transaction.getAmount();
        Timestamp date = transaction.getDate();
        Wallet wallet = WalletDbAdapter.getInstance().getRecord(transaction.getWallet());
        String category = transaction.getCategory();
        amountEditor.setText(Long.toString(transaction.getAmount()));
        contentEditor.setText(transaction.getContent());

        if(new Date(System.currentTimeMillis())==new Date(date.getYear(), date.getMonth(), date.getDate())){
            selectedDateText.setText("Today");
        } else{
            Log.i("TransactionFormFragment", "loaded selected date " + transaction.getDay() + " " +transaction.getMonth() + " "+transaction.getYear());
//            selectedDateText.setText(date.toString().substring(8,10) + "/" + date.toString().substring(5,7) + "/" + date.toString().substring(0,4));
//            selectedDateText.setText(transaction.getDay()+"/"+transaction.getMonth()+"/"+transaction.getYear());
            selectedDateText.setText(transaction.getYear() + "-" + String.format("%02d", transaction.getMonth())  + "-" + String.format("%02d", transaction.getDay()));

        }
        selectedDate = new Timestamp((int)transaction.getYear(), (int)transaction.getMonth(), (int)transaction.getDay(), 0, 0, 0, 0);
        Log.i("TransactionFormFragment", "loaded selected date " + selectedDate.toString().substring(0,4) + " " +selectedDate.toString().substring(5,7) + " "+selectedDate.toString().substring(8,10));

        selectedDateText.setTextColor(getResources().getColor(R.color.black));
//        selectedDateText.setText( transaction.getDate().toString());

        categoryText.setText(transaction.getCategory());
        selectedCategory = category;
        categoryIcon.setImageResource(getResources().getIdentifier(category.toLowerCase(), "mipmap", getContext().getPackageName()));
//                    categoryIcon.setImageResource();
        categoryText.setTextColor(getResources().getColor(R.color.black));

        exclusionChecker.setEnabled(transaction.getReport_exclusion());

        walletText.setText(wallet.getName());
        selectedWallet = wallet.getName();
        walletIcon.setImageResource(getResources().getIdentifier(WalletDbAdapter.getInstance().getRecordWithName(selectedWallet).getIcon(),
                "mipmap", getContext().getPackageName()));
        walletText.setTextColor(getResources().getColor(R.color.black));
    }
    private void init(){

    }
    private void finishFragment(){
        InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(amountEditor.getWindowToken(), 0);

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
    public void tickFields(){
        if(isAmountFilled!=1){
            amountTicker.setVisibility(View.VISIBLE);
        }
        if(isTotalFilled!=1){
            walletTicker.setVisibility(View.VISIBLE);
        }
        if(isNoteFilled!=1){
            noteTicker.setVisibility(View.VISIBLE);
        }
        if(isCategoryFilled!=1){
            categoryTicker.setVisibility(View.VISIBLE);
        }
        if(isDateFilled!=1){
            dateTicker.setVisibility(View.VISIBLE);
        }
    }
    private void checkFields(){
//        if(walletIcon.getDrawable()!=getResources().getDrawable(R.mipmap.wallet_activated)){
//            isTotalFilled = -1;
//        }
//        if(contentIcon.getDrawable()!=getResources().getDrawable(R.mipmap.note_activated)){
//            isNoteFilled = -1;
//        }
    }
    private void saveTransaction(){
        Log.i("TransactionFormFragment", "Saving transaction");
        checkFields();
        if (isAmountFilled != 1|| isCategoryFilled != 1){
            isAcceptable=false;
            tickFields();
            highlightedText.setVisibility(View.VISIBLE);
            return;
        }
        isAcceptable=true;

        if(transactionDbAdapter==null){
            Log.i("TransactionFormFragment", "Loading Transaction DB");
            transactionDbAdapter = TransactionDbAdapter.getInstance();
        }
        if(actionBar.getTitle()=="Create"){
            Log.i("TransactionFormFragment", "transaction is null");

            int amount = Integer.valueOf(amountEditor.getText().toString());
            String content = contentEditor.getText().toString();
            if(isDateFilled!=1){
                selectedDate=new Timestamp(System.currentTimeMillis());
            }
            Timestamp date = selectedDate;
//            Log.i("TransactionFormFragment", selectedDate.toString());

            String category = selectedCategory;
            boolean exclusion = exclusionChecker.isChecked();
            Log.i("TransactionFormFragment", "Selected wallet" + selectedWallet);
            String wallet = WalletDbAdapter.getInstance().getRecordWithName(selectedWallet).getUID();

            transaction = new Transaction(amount, category,wallet);
            transaction.setContent(content);
//            transaction.setDate(selectedDate);

            transaction.setDay(Long.valueOf(date.toString().substring(8,10)));
            transaction.setYear(Long.valueOf(date.toString().substring(0,4)));
            transaction.setMonth(Long.valueOf(date.toString().substring(5,7)));

            transaction.setReport_exclusion(exclusion);
            Log.i("TransactionFormFragment", "Transaction information" + amountEditor.getText().toString() + contentEditor.getText().toString()
                    + transaction.getDay() + transaction.getMonth() +transaction.getYear() + selectedCategory + selectedWallet);

            transactionDbAdapter.addRecord(transaction, insert);
        } else{
            Log.i("TransactionFormFragment", "transaction is non-null and uid is" + transactionUID);
            Log.i("TransactionFormFragment", "Transaction information" + amountEditor.getText() + contentEditor.getText().toString()
                + selectedDate + selectedCategory + selectedWallet);

            int amount = Integer.valueOf(amountEditor.getText().toString());
            String content = contentEditor.getText().toString();
            Timestamp date = selectedDate;
            String category = selectedCategory;
            boolean exclusion = exclusionChecker.isChecked();
            String wallet = WalletDbAdapter.getInstance().getRecordWithName(selectedWallet).getUID();

            transaction = new Transaction(amount, category,wallet);
            transaction.setUID(transactionUID);
            transaction.setAmount(amount);
            transaction.setCategory(selectedCategory);
            transaction.setContent(content);
//            transaction.setDate(date);
            transaction.setDay(Long.valueOf(date.toString().substring(8,10)));
            transaction.setYear(Long.valueOf(date.toString().substring(0,4)));
            transaction.setMonth(Long.valueOf(date.toString().substring(5,7)));

            transaction.setWallet(wallet);
            transaction.setReport_exclusion(exclusion);
            transactionDbAdapter.addRecord(transaction, DatabaseAdapter.UpdateMethod.update);
//            transactionDbAdapter.updateRecord(transactionUID, DatabaseSchema.TransactionEntry.COLUMN_AMOUNT, amountEditor.getText().toString());
//            transactionDbAdapter.updateRecord(transactionUID, DatabaseSchema.TransactionEntry.COLUMN_CATERGORY, selectedCategory);

        }
        finishFragment();

    }
}
