package com.example.myapplication.ui.transaction;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.db.adapters.TransactionDbAdapter;
import com.example.myapplication.db.adapters.WalletDbAdapter;
import com.example.myapplication.model.Transaction;
import com.example.myapplication.model.Wallet;
import com.example.myapplication.ui.common.FormActivity;
import com.example.myapplication.utils.CategoryPicker.CategoryPickerDialog;
import com.example.myapplication.utils.CategoryPicker.CategoryPickerSwatch;
import com.example.myapplication.utils.Refreshable;
import com.example.myapplication.utils.WalletPicker.WalletPickerSwatch;
import com.example.myapplication.utils.WalletSwitch.WalletSwitcherDialog;
import com.example.myapplication.utils.WalletSwitch.WalletSwitcherSwatch;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {
    protected static final String LOG_TAG="TransactionActivity";
    public static final int REQUEST_EDIT_TRANSACTION = 0x10;

    public static final int INDEX_FUTURE_TRANSACTIONS_FRAGMENT=0;
    public static final int INDEX_CURRENT_TRANSACTIONS_FRAGMENT=1;
    public static final int INDEX_PAST_1_TRANSACTIONS_FRAGMENT=2;
    public static final int INDEX_PAST_2_TRANSACTIONS_FRAGMENT=3;
    public static final int INDEX_PAST_3_TRANSACTIONS_FRAGMENT=4;
    public static final int INDEX_PAST_4_TRANSACTIONS_FRAGMENT=5;
    public static final int INDEX_PAST_5_TRANSACTIONS_FRAGMENT=6;
    public static final int INDEX_PAST_6_TRANSACTIONS_FRAGMENT=7;
    public static final int INDEX_PAST_7_TRANSACTIONS_FRAGMENT=8;
    public static final int INDEX_PAST_8_TRANSACTIONS_FRAGMENT=9;
    public static final int INDEX_PAST_9_TRANSACTIONS_FRAGMENT=10;

    public static final String LAST_OPEN_TAB_INDEX =  "last_open_tab";
    public static final String EXTRA_TAB_INDEX = "transaction_extra_TAB_INDEX";
    public static final int DEFAULT_NUM_PAGES = 11;

    public String time_range="Day";
    public String view_by = "day";
    private SparseArray fragmentPageReferenceMap = new SparseArray();
    private String sortOrder = "Amount";
    private Wallet currentWallet = new Wallet();
    TabLayout tabLayout;
    ViewPager viewPager;
    Button floatingActionButton;
    LinearLayout form_toolbar;
    LinearLayout mainscreen_toolbar;
    ImageView wallet_icon;
    ImageView time_icon;
    ImageView view_icon;
    TextView wallet_name;
    TextView wallet_balance;


    private TransactionViewPagerAdapter transactionViewPagerAdapter;

    private class TransactionViewPagerAdapter extends FragmentStatePagerAdapter{
        public TransactionViewPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);

        }

        @Override
        public Fragment getItem(int i){
            TransactionListFragment currentFragment = (TransactionListFragment)fragmentPageReferenceMap.get(i);
            if(currentFragment==null){
                switch (i){
                    case 0:
                        currentFragment=TransactionListFragment.newIntstance(time_range, -1, "FUTURE tab", sortOrder, currentWallet.getUID());
                        Log.i("TransactionActivity", "FURTURE tab " + Integer.toString(i));
                        break;
                    case 1:
                        currentFragment=TransactionListFragment.newIntstance(time_range, 0, "PRESENT tab", sortOrder, currentWallet.getUID());
                        Log.i("TransactionActivity", "PRESENT tab" + Integer.toString(i));

                        break;
                    case 2:
                        currentFragment=TransactionListFragment.newIntstance(time_range, 1, "PAST 1 tab", sortOrder, currentWallet.getUID());
                        Log.i("TransactionActivity", "PAST 1 tab" + Integer.toString(i));
                        break;
                    case 3:
                        currentFragment=TransactionListFragment.newIntstance(time_range, 2, "PAST 2 tab", sortOrder, currentWallet.getUID());
                        Log.i("TransactionActivity", "PAST 2 tab" + Integer.toString(i));
                        break;
                    case 4:
                        currentFragment=TransactionListFragment.newIntstance(time_range, 3, "PAST 3 tab", sortOrder, currentWallet.getUID());
                        Log.i("TransactionActivity", "PAST 3 tab" + Integer.toString(i));
                        break;
                    case 5:
                        currentFragment=TransactionListFragment.newIntstance(time_range, 4, "PAST 4 tab", sortOrder, currentWallet.getUID());
                        Log.i("TransactionActivity", "PAST 4 tab" + Integer.toString(i));
                        break;

                    case 6:
                        currentFragment=TransactionListFragment.newIntstance(time_range, 5, "PAST 5 tab", sortOrder, currentWallet.getUID());
                        Log.i("TransactionActivity", "PAST 5 tab" + Integer.toString(i));
                        break;
                    case 7:
                        currentFragment=TransactionListFragment.newIntstance(time_range, 6, "PAST 6 tab", sortOrder, currentWallet.getUID());
                        Log.i("TransactionActivity", "PAST 6 tab" + Integer.toString(i));
                        break;
                    case 8:
                        currentFragment=TransactionListFragment.newIntstance(time_range, 7, "PAST 7 tab", sortOrder, currentWallet.getUID());
                        Log.i("TransactionActivity", "PAST 7 tab" + Integer.toString(i));
                        break;
                    case 9:
                        currentFragment=TransactionListFragment.newIntstance(time_range, 8, "PAST 8 tab", sortOrder, currentWallet.getUID());
                        Log.i("TransactionActivity", "PAST 8 tab" + Integer.toString(i));
                        break;
                    case 10:
                        currentFragment=TransactionListFragment.newIntstance(time_range, 9, "PAST 9 tab", sortOrder, currentWallet.getUID());
                        Log.i("TransactionActivity", "PAST " +(i-1) +" tab" + Integer.toString(i));
                        break;
                    default:
                        currentFragment=TransactionListFragment.newIntstance(time_range, 10, "PAST __ tab", sortOrder, currentWallet.getUID());
                        Log.i("TransactionActivity", "PAST 9 tab" + Integer.toString(i));
                        break;


                }
                fragmentPageReferenceMap.put(i, currentFragment);
            }
            return currentFragment;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object){
            super.destroyItem(container, position, object);
            fragmentPageReferenceMap.remove(position);
        }
        @Override
        public CharSequence getPageTitle(int position){
            switch (position){
                case INDEX_FUTURE_TRANSACTIONS_FRAGMENT:
                    return "FUTURE";

                case INDEX_CURRENT_TRANSACTIONS_FRAGMENT:
                    if(time_range=="Day"){
                        return "Today";
                    }else{
                        return "THIS " + time_range.toUpperCase();
                    }
                case INDEX_PAST_1_TRANSACTIONS_FRAGMENT:
                    if(time_range=="Day"){
                        return "Yesterday";
                    }else{
                        return "LAST " + time_range.toUpperCase();
                    }
                case INDEX_PAST_2_TRANSACTIONS_FRAGMENT:
                case INDEX_PAST_3_TRANSACTIONS_FRAGMENT:
                case INDEX_PAST_4_TRANSACTIONS_FRAGMENT:
                case INDEX_PAST_5_TRANSACTIONS_FRAGMENT:
                case INDEX_PAST_6_TRANSACTIONS_FRAGMENT:
                case INDEX_PAST_7_TRANSACTIONS_FRAGMENT:
                case INDEX_PAST_8_TRANSACTIONS_FRAGMENT:
                case INDEX_PAST_9_TRANSACTIONS_FRAGMENT:
                    if(time_range=="Day"){
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, -(position-1));
                        Log.i("TransactionActitivity", "Tab title " + cal.getTime().toString().substring(0, 10));
                        return cal.getTime().toString().substring(0, 10);
                    }else if(time_range=="Month"){
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, -(position-1)*30);
                        String time = cal.getTime().toString();
                        Log.i("TransactionActitivity", "Tab title " + time.substring(4,8) + time.substring(30));

                        return time.substring(4,8) + time.substring(30);
                    } else if (time_range=="Year"){
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, -(position-1)*365);
                        String time = cal.getTime().toString();
                        Log.i("TransactionActitivity", "Tab title " + time.substring(30));

                        return time.substring(30);
                    } else if(time_range=="Quarter"){
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, -(position-1)*30*3);
                        String time = cal.getTime().toString();
                        switch (time.substring(4,7)){
                            case "Jan":
                            case "Feb":
                            case "Mar":
                                return "Q1 - " + time.substring(30);

                            case "Apr":
                            case "May":
                            case "Jun":
                                return "Q2 - " + time.substring(30);
                            case "Jul":
                            case "Oct":
                            case "Nov":
                                return "Q3 - " + time.substring(30);
                            default:
                                return "Q4 - " + time.substring(30);

                        }

                    }
                default:
                    return "HELLO";
            }
        }
        @Override
        public int getCount(){return DEFAULT_NUM_PAGES;}
    }
    public TransactionListFragment getCurrentFragment(){
        int index=viewPager.getCurrentItem();
        Fragment fragment=(Fragment) fragmentPageReferenceMap.get(index);
        if(fragment==null){
            fragment=transactionViewPagerAdapter.getItem(index);
        }
        return (TransactionListFragment) fragment;
    }

    public int getContentView(){return R.layout.activity_transaction;}
//    public int getTileRes(){return R.string.title_accounts;}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        final Intent intent=getIntent();

        //init()
        getSupportActionBar().hide();
        setContentView(R.layout.activity_transaction);
        floatingActionButton = findViewById(R.id.transaction_fab);
        form_toolbar = findViewById(R.id.toolbar_form);
        form_toolbar.setVisibility(View.GONE);
        mainscreen_toolbar = findViewById(R.id.toolbar_mainscreen);
        wallet_icon = findViewById(R.id.toolbar_wallet_icon);
        time_icon = findViewById(R.id.toolbar_time_icon);
        view_icon = findViewById(R.id.toolbar_view_icon);
        wallet_name = findViewById(R.id.toolbar_wallet_name);
        wallet_balance = findViewById(R.id.toolbar_wallet_balance);

        tabLayout = (TabLayout)findViewById(R.id.transaction_tab_layout);

        transactionViewPagerAdapter = new TransactionViewPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.transaction_viewpager);
        viewPager.setAdapter(transactionViewPagerAdapter);

//        Log.i("Viewpager num", Integer.toString(transactionViewPagerAdapter.getCount()));
//        Log.i("Viewpager num", Long.toString(WalletDbAdapter.getInstance().getRecordsCount()));
//        Log.i("Viewpager num", Long.toString(TransactionDbAdapter.getInstance().getRecordsCount()));
//        Log.i("Viewpager num", Integer.toString(transactionViewPagerAdapter.getCount()));

        currentWallet = WalletDbAdapter.getInstance().getAllRecords().get(0);
        wallet_name.setText(currentWallet.getName());
        wallet_balance.setText(currentWallet.getCurrency());
        wallet_icon.setImageResource(getResources().getIdentifier(currentWallet.getIcon(), "mipmap", getPackageName()));

        tabLayout.setupWithViewPager(viewPager);
        String title="";
        for (int i =0;i<3;i++){
            switch (i){
                case INDEX_FUTURE_TRANSACTIONS_FRAGMENT:
                    title = "FUTURE";
                    break;
                case INDEX_CURRENT_TRANSACTIONS_FRAGMENT:
                    if(time_range=="Day"){
                        title = "Today";
                        break;
                    }else{
                        title = "THIS " + time_range.toUpperCase();
                        break;
                    }
                case INDEX_PAST_1_TRANSACTIONS_FRAGMENT:
                    if(time_range=="Day"){
                        title = "Yesterday";
                        break;
                    }else{
                        title =  "LAST " + time_range.toUpperCase();
                        break;
                    }
                default:
                    title = "HELLO";
            }
//            tabLayout.addTab(tabLayout.newTab().setText(title));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //nothing to see here, move along
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //nothing to see here, move along
            }
        });

        setCurrentTab();

        floatingActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent addTransactionIntent = new Intent(TransactionActivity.this, FormActivity.class);
                addTransactionIntent.setAction(Intent.ACTION_INSERT_OR_EDIT);
                addTransactionIntent.putExtra("FORM_TYPE", FormActivity.FormType.TRANSACTION.name());
                startActivityForResult(addTransactionIntent, TransactionActivity.REQUEST_EDIT_TRANSACTION);
            }
        });
        wallet_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                PopupMenu popupMenu = new PopupMenu(this, v);
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.mainscreen_options, popupMenu.getMenu());

                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.main_time_create:
//                                createTimeRangeList();
                                createWallet();
                                return true;
                            case R.id.main_time_switch:
                                switchWallet();
                                return true;
                            case R.id.main_view_option:
                                if(sortOrder == "Amount") {
                                    switchToCategoryView();
                                }else{
                                    switchToAmountView();
                                }
                                return true;
                            default:

                                return false;

                        }

                    }
                });            }
        });
        time_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.timerange_options, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.timerange_day:
                                switchTimeRange("Day");
                                return true;
                            case R.id.timerange_month:
                                switchTimeRange("Month");
                                return true;

                            case R.id.timerange_quarter:
                                switchTimeRange("Quarter");
                                return true;
                            case R.id.timerange_year:
                                switchTimeRange("Year");
                                return true;
                            default:
                                return false;
                        }

                    }
                });
            }
        });
        view_icon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                sortOrder = sortOrder.toLowerCase() == "category" ? "Amount" : "Category";
                transactionViewPagerAdapter = new TransactionViewPagerAdapter(getSupportFragmentManager());
                viewPager = (ViewPager) findViewById(R.id.transaction_viewpager);
                viewPager.setAdapter(transactionViewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
//                finish();
//                startActivity(getIntent());
            }
        });

    }
    private void createWallet(){
        Intent addTransactionIntent = new Intent(TransactionActivity.this, FormActivity.class);
        addTransactionIntent.setAction(Intent.ACTION_INSERT_OR_EDIT);
        addTransactionIntent.putExtra("FORM_TYPE", FormActivity.FormType.WALLET.name());
        startActivityForResult(addTransactionIntent, TransactionActivity.REQUEST_EDIT_TRANSACTION);
//        this.recreate();

        finish();
        startActivity(getIntent());
    }
    private final WalletSwitcherSwatch.OnWalletSelectedListener walletSelectedListener =
            new WalletSwitcherSwatch.OnWalletSelectedListener(){

                @Override
                public void onWalletSeletected(String wallet){
                    Wallet w = WalletDbAdapter.getInstance().getRecord(wallet);
                    wallet_name.setText(w.getName());
                    wallet_balance.setText(w.getCurrency());
                    currentWallet = w;
                    wallet_icon.setImageResource(getResources().getIdentifier(w.getIcon(), "mipmap", getPackageName()));

                    transactionViewPagerAdapter = new TransactionViewPagerAdapter(getSupportFragmentManager());
                    viewPager = (ViewPager) findViewById(R.id.transaction_viewpager);
                    viewPager.setAdapter(transactionViewPagerAdapter);
                    tabLayout.setupWithViewPager(viewPager);


                }
            };
    private void switchWallet(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        String currentCategory = "";
//        if(currentWallet!=null){
//            currentCategory = get.getCategory();
//
//        }

        WalletSwitcherDialog walletSwitcherDialog = WalletSwitcherDialog.newInstance(
                2,
                getWalletOptions(),
                currentWallet.getUID());
        walletSwitcherDialog.setOnWalletSelectedListener(walletSelectedListener);
        walletSwitcherDialog.show(fragmentManager, "Wallet Switcher");
    }
    private String[] getWalletOptions(){
        List<Wallet> wallets = WalletDbAdapter.getInstance().getAllWallets();
        List<String> uids = new ArrayList<String>() ;
        for(Wallet w:wallets){
            if(w.isDeleted()!=true){
                uids.add(w.getUID());
                Log.i("getWalletOptions", "====");
            }
        }
        Log.i("getWalletOptions", uids.size()+"");

        String[] results = new String[uids.size()];
        return uids.toArray(results);
    }
    private void switchToCategoryView(){
        sortOrder = "Category";
        transactionViewPagerAdapter = new TransactionViewPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.transaction_viewpager);
        viewPager.setAdapter(transactionViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
    private void switchToAmountView(){
        sortOrder = "Amount";
        transactionViewPagerAdapter = new TransactionViewPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.transaction_viewpager);
        viewPager.setAdapter(transactionViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
//    private final WalletPickerSwatch.OnWalletSelectedListener walletSelectedListener =
//            new WalletPickerSwatch.OnWalletSelectedListener(){
//                @Override
//                public void onWalletSeletected(String wallet){
////                    walletText.setText(wallet);
////                    walletIcon.setImageResource(getResources().getIdentifier(WalletDbAdapter.getInstance().getRecordWithName(selectedWallet).getIcon(),
////                            "mipmap", getContext().getPackageName()));
////                    selectedWallet = wallet;
////                    walletText.setTextColor(getResources().getColor(R.color.black));
//                }
//            };
    private void switchTimeRange(String tr){
        this.time_range=tr;
        transactionViewPagerAdapter = new TransactionViewPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.transaction_viewpager);
        viewPager.setAdapter(transactionViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }
    @Override
    protected void onStart(){
        super.onStart();
    }
    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        setCurrentTab();

        int index = viewPager.getCurrentItem();
        Fragment fragment = (Fragment)fragmentPageReferenceMap.get(index);
        if(fragment!=null){
            ((Refreshable)fragment).refresh();
        }
    }
    public void setCurrentTab(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int lastTabIndex = preferences.getInt(LAST_OPEN_TAB_INDEX, INDEX_CURRENT_TRANSACTIONS_FRAGMENT);
        int index = getIntent().getIntExtra(EXTRA_TAB_INDEX, lastTabIndex);
        viewPager.setCurrentItem(index);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putInt(LAST_OPEN_TAB_INDEX, viewPager.getCurrentItem()).apply();
    }
    public static void start(Context context){
        Intent intent = new Intent(context, TransactionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.transaction_option, menu);
        return true;
    }
    /*@Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:

        }
    }*//*
    public void transactionSelected(String uid){
        Intent intent = new Intent(this, TransactionActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra("transaction_uid", uid);

        startActivity(intent);
    }*/
}
