package com.example.myapplication.ui.transaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.db.DatabaseCursorLoader;
import com.example.myapplication.db.DatabaseSchema;
import com.example.myapplication.db.adapters.TransactionDbAdapter;
import com.example.myapplication.db.adapters.WalletDbAdapter;
import com.example.myapplication.model.Transaction;
import com.example.myapplication.ui.common.FormActivity;
import com.example.myapplication.ui.utils.EmptyRecyclerView;
import com.example.myapplication.utils.CursorRecyclerAdapter;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TransactionListFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor>
                {
    protected static final String TAG = "TransactionListFragment";
    private static final String STATE_DISPLAY_MODE = "mDisplayMode";

    String[] possible_faces = {"•́ε•̀٥", "◕‿↼ ", "( ͡° ͜ʖ ͡°)", "( ͡ᵔ ͜ʖ ͡ᵔ )", "( ͡~ ͜ʖ ͡°)", "( ͡o ͜ʖ ͡o)",
                                "(͡o‿O͡)", "(✪‿✪)", "( ✧≖ ͜ʖ≖)", "( ಠ ͜ʖಠ)", "( ͡ʘ ͜ʖ ͡ʘ)", ""};

    private TransactionDbAdapter transactionDbAdapter;
    TransactionRecyclerAdapter transactionRecyclerAdapter;
    EmptyRecyclerView emptyRecyclerView;
    TextView inflowText;
    TextView outflowText;
    TextView sumText;
    Button reportButton;

    TextView emptyText;
    ImageView emptyIcon;
    View emptyView;

    LinearLayout overview=null;
    TextView face = null;

    static int inflow=0;
    static int outflow= 0;
    static int sum=0;
//    private TransactionDbAdapter transactionDbAdapter;
    private OnTransactionClickedListener onTransactionClickedListener;
    private CursorLoader cursorLoader;
    private String time_range="";
    private String title = " ";
    static String order = "";
    static String uidWallet = "";
    private int mode;
    public static TransactionListFragment newIntstance(String time_range, int mode, String title, String sortOrder, String walletUid){
        TransactionListFragment fragment=new TransactionListFragment();
        fragment.time_range=time_range;
        fragment.mode=mode;
        fragment.title = title;
        fragment.order=sortOrder;
        fragment.uidWallet = walletUid;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_transaction_list, container, false);
        emptyRecyclerView = view.findViewById(R.id.transaction_list_recyclerview);
        inflowText = view.findViewById(R.id.transaction_inflow);
        outflowText = view.findViewById(R.id.transaction_outflow);
        sumText = view.findViewById(R.id.transaction_balance);
        emptyText=view.findViewById(R.id.transaction_list_text);
//        emptyIcon=view.findViewById(R.id.transaction_list_icon);
//        reportButton = view.findViewById(R.id.transaction_button);
        emptyView = view.findViewById(R.id.transaction_empty_view);
        overview = view.findViewById(R.id.transaction_list_overview);
        face = view.findViewById(R.id.transaction_list_text);

        emptyRecyclerView.setHasFixedSize(true);
        emptyRecyclerView.setEmptyView(emptyView);
/*
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else {
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
        }*/

        return view;

    }
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);

        Bundle args = getArguments();
        getLoaderManager().initLoader(0, null, this);
    }
    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);
        cursorLoader = (CursorLoader) this.onCreateLoader(0, bundle);
        Cursor cursor = cursorLoader.loadInBackground();

        transactionRecyclerAdapter = new TransactionRecyclerAdapter(cursor);
        emptyRecyclerView.setAdapter(transactionRecyclerAdapter);
        emptyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sum = inflow-outflow;
        inflowText.setText(Integer.toString(inflow));
        outflowText.setText(Integer.toString(outflow));
        sumText.setText(Integer.toString(sum));
        if(sum>0){
            sumText.setTextColor(getResources().getColor(R.color.green));
        }else{
            sumText.setTextColor(getResources().getColor(R.color.red));

        }
    }
    @Override
    public void onStart(){
        super.onStart();
        transactionDbAdapter = TransactionDbAdapter.getInstance();
    }
    @Override
    public void onResume(){
        super.onResume();
        refresh();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED)
            return;

        refresh();
    }
    /*@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onTransactionClickedListener = (OnTransactionClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnAccountSelectedListener");
        }
    }*/
    public void onListItemClick(String uid){
        onTransactionClickedListener.transactionSelected(uid);
    }

    public void tryDeleteTransaction(String uid){
        transactionDbAdapter.deleteRecord(uid);
        refresh();
    }
    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

    }*/
    public void openCreateOrEditActivity(long id){
        Intent intent = new Intent(TransactionListFragment.this.getActivity(), FormActivity.class);
        intent.setAction(Intent.ACTION_INSERT_OR_EDIT);
        intent.putExtra("TRANSACTION_UID", transactionDbAdapter.getUID(id));
        intent.putExtra("FORM_TYPE", FormActivity.FormType.TRANSACTION.name());
//        Log.i("TransactionListFragment", FormActivity.FormType.TRANSACTION.name());
//        Log.i("TransactionListFragment", "HELLLLLLLLLLLLLLLLLLLLLL" + transactionDbAdapter.getUID(id));

        startActivityForResult(intent, TransactionActivity.REQUEST_EDIT_TRANSACTION);

    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
//        return null;
        return new TransactionCursorLoader(this.getActivity(), this.time_range, this.mode, this.title);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor){
        transactionRecyclerAdapter.swapCursor(cursor);
        transactionRecyclerAdapter.notifyDataSetChanged();
    }
    @Override
    public void onLoaderReset(Loader<Cursor> arg){
        transactionRecyclerAdapter.swapCursor(null);
    }

    public void refresh() {
        getLoaderManager().restartLoader(0, null, this);
        cursorLoader = (CursorLoader) this.onCreateLoader(0, null);
        Cursor cursor = cursorLoader.loadInBackground();

        transactionRecyclerAdapter = new TransactionRecyclerAdapter(cursor);

        emptyRecyclerView.setAdapter(transactionRecyclerAdapter);
        emptyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        inflowText.setText(Integer.toString(inflow));
        outflowText.setText(Integer.toString(outflow));
        sumText.setText(Integer.toString(sum));
        if (sum==0 && inflow == 0 && outflow ==0){
            emptyRecyclerView.setVisibility(View.GONE);
            overview.setVisibility(View.GONE);
            int rand = (int)System.currentTimeMillis()%(possible_faces.length-1);
            face.setText(possible_faces[rand]);
        }
        if(sum>0){
            sumText.setTextColor(getResources().getColor(R.color.green));
        }else{
            sumText.setTextColor(getResources().getColor(R.color.red));

        }

    }
    private static final class TransactionCursorLoader extends DatabaseCursorLoader{
        private int displayMode = 1;
        private String time_range = "";
        private String title;
        List<String> uid;
        int count;

        public TransactionCursorLoader(Context context, String tr, int mode, String title){
            super(context);
            this.displayMode=mode;
            this.time_range = tr;
            this.title = title;
            uid = new ArrayList<String>();
            count =0;
//            Log.i("TransactionListFragment", tr);
        }
        @Nullable
        @Override
        public Cursor loadInBackground() {
            databaseAdapter = TransactionDbAdapter.getInstance();
            uid.clear();
            Cursor cursor = null;
            int prev = 0;
            long current;
            Timestamp date;
            List<Transaction> transactions = ((TransactionDbAdapter)databaseAdapter).getAllTransactionsBelongTo(uidWallet);
            Log.i("wallet", WalletDbAdapter.getInstance().getRecord(uidWallet).getName() + " " + uid.size());

//            Log.i("TransactionListFragment", "count trans " + transactions.size());
            inflow=0;
            outflow=0;
            Log.i("Tab_info_begin", this.title + " " + uid.size());

            switch (this.time_range){
                case "Day":
                    date = new Timestamp(System.currentTimeMillis());
                    current = day2int(Long.valueOf(date.toString().substring(5,7)), Long.valueOf(date.toString().substring(8,10)),
                            Long.valueOf(date.toString().substring(0,4)));
                    Log.i("TransactionListFragment_info", "title " + this.title + " displaymode " + this.displayMode + " time_range " +this.time_range);

                    for (Transaction t:transactions){
                        long gap = current - day2int(t.getMonth(), t.getDay(), t.getYear());
                        Log.i("TransactionListFragment_t_info", "day_time_gap " + " " + gap+ " current " + current + " date " +date.toString());
//                        Log.i("TransactionListFragment_info", "title " + this.title + " displaymode " + this.displayMode + " time_range " +this.time_range);

//                        if (gap<0 && this.displayMode<0){
//                            if(gap<0){
//                                Log.i("TransactionListFragment_future_gap", gap +" ");
//                            }
//                            if(this.displayMode<0){
//                                Log.i("TransactionListFragment_future_mode", this.displayMode +" ");
//
//                            }
//                            Log.i("TransactionListFragment", "tab_display_mode " + this.displayMode);
//                            Log.i("TransactionListFragment add new item",  "" + this.title);
//                            uid.add(t.getUID());
//                        }
                        if((int)gap<0){
                            if(this.displayMode==-1){

                                uid.add(t.getUID());
                                Log.i("+++++++", "tab display mode " + t.getCategory() + " " + gap + " " + this.displayMode);
                                count++;
                                if(uid.size()!=count && this.displayMode==-1){
                                    uid.clear();
                                    uid.add(t.getUID());

                                }
                                if(this.displayMode==-1){
                                    if(t.getAmount()>=0){
                                        inflow+=t.getAmount();
                                    }else{
                                        outflow+=t.getAmount();
                                    }
                                }
                                Log.i("Future_tab_info_first", "acc " + "gap_"+gap +" mode_"+this.displayMode + " uid_size_"+uid.size() + " count_"+count);

                            }

                        }
                        if((int)gap>=0){
                            if(((int)gap-this.displayMode)==0){
                                if(this.displayMode!=9){
                                    uid.add(t.getUID());

                                    Log.i("Future_tab_info_third", "acc " + "gap_"+gap +" mode_"+this.displayMode + " uid_size_"+uid.size() + " count_"+count);
                                    if(t.getAmount()>=0){
                                        inflow+=t.getAmount();
                                    }else{
                                        outflow+=t.getAmount();
                                    }
                                }
                                Log.i("-------------", "tab display mode " + t.getCategory() + " " +gap + " " +  this.displayMode);
                                count++;
                                if(uid.size()!=count  && this.displayMode>=0){
                                    uid.clear();
                                    uid.add(t.getUID());
                                }

//                                Log.i("Future_tab_info_second", "acc " + "gap_"+gap +" mode_"+this.displayMode + " uid_size_"+uid.size() + " count_"+count);

//                                if(t.getAmount()>=0){
//                                    inflow+=t.getAmount();
//                                }else{
//                                    outflow+=t.getAmount();
//                                }
                            }
                            if(((int)gap-this.displayMode)>0) {
                                if(this.displayMode==9){
                                    uid.add(t.getUID());

                                    Log.i("Future_tab_info_third", "acc " + "gap_"+gap +" mode_"+this.displayMode + " uid_size_"+uid.size() + " count_"+count);
                                    if(t.getAmount()>=0){
                                        inflow+=t.getAmount();
                                    }else{
                                        outflow+=t.getAmount();
                                    }
                                }

//                                if (t.getAmount() >= 0) {
//                                    inflow += t.getAmount();
//                                } else {
//                                    outflow += t.getAmount();
//                                }
                            }
                        }
//                        uid.add(t.getUID());
//                        if(gap-this.displayMode==0){
//                            uid.add(t.getUID());
//                            Log.i("TransactionListFragment", "tab_display_mode " + this.displayMode);
//                            Log.i("TransactionListFragment add new item",  "" + this.title);
//                            if(t.getAmount()>=0){
//                                inflow+=t.getAmount();
//                            }else{
//                                outflow+=t.getAmount();
//                            }
//                        }
//                        if(gap-this.displayMode>0){
//                            Log.i("TransactionListFragment", "tab_display_mode " + this.displayMode);
//                            Log.i("TransactionListFragment add new item",  "" + this.title);
//                            uid.add(t.getUID());
//                            if(t.getAmount()>=0){
//                                inflow+=t.getAmount();
//                            }else{
//                                outflow+=t.getAmount();
//                            }
//                        }
                        if (prev < uid.size() && this.title=="FUTURE tab"){
                            prev=uid.size();
                            Log.i("Future_tab_info", "acc " + "gap_"+gap +" mode_"+this.displayMode + " uid_size_"+uid.size());

                        }
                    }

                    break;
                case "Month":
                    date = new Timestamp(System.currentTimeMillis());
                    current = month2int(Long.valueOf(date.toString().substring(5,7)), Long.valueOf(date.toString().substring(0,4)));
                    for (Transaction t:transactions){
                        long gap = current - month2int(t.getMonth(), t.getYear());
                        Log.i("TransactionListFragment", "month time gap " + t.getUID() + " " + gap+ " " + current+" " + date.toString());

//                        if (gap<0 && this.displayMode==-1){
//                            uid.add(t.getUID());
//                        }
//                        if(gap-this.displayMode==0){
//                            uid.add(t.getUID());
//                            if(t.getAmount()>=0){
//                                inflow+=t.getAmount();
//                            }else{
//                                outflow+=t.getAmount();
//                            }
//                        }
//                        if(gap<0){
//                            if(this.displayMode==-1){
//
//                                uid.add(t.getUID());
//                                Log.i("+++++++", "tab display mode " + t.getCategory() + " " + gap + " " + this.displayMode);
//                            }
//
//                        }else if((gap-this.displayMode)==0){
//                            uid.add(t.getUID());
//                            Log.i("-------------", "tab display mode " + t.getCategory() + " " +gap + " " +  this.displayMode);
//                            if(this.title=="FURTURE"){
//                                Log.i("Tab_info", this.title + " " + uid.size());
//
//                            }
//                            if(t.getAmount()>=0){
//                                inflow+=t.getAmount();
//                            }else{
//                                outflow+=t.getAmount();
//                            }
//                        }else if((gap-this.displayMode)>0) {
//                            uid.add(t.getUID());
//                            if (t.getAmount() >= 0) {
//                                inflow += t.getAmount();
//                            } else {
//                                outflow += t.getAmount();
//                            }
//                        }
                        if((int)gap<0){
                            if(this.displayMode==-1){

                                uid.add(t.getUID());
                                Log.i("+++++++", "tab display mode " + t.getCategory() + " " + gap + " " + this.displayMode);
                                count++;
                                if(uid.size()!=count && this.displayMode==-1){
                                    uid.clear();
                                    uid.add(t.getUID());

                                }
                                if(this.displayMode==-1){
                                    if(t.getAmount()>=0){
                                        inflow+=t.getAmount();
                                    }else{
                                        outflow+=t.getAmount();
                                    }
                                }
                                Log.i("Future_tab_info_first", "acc " + "gap_"+gap +" mode_"+this.displayMode + " uid_size_"+uid.size() + " count_"+count);

                            }

                        }
                        if((int)gap>=0){
                            if(((int)gap-this.displayMode)==0){
                                if(this.displayMode!=9){
                                    uid.add(t.getUID());

                                    Log.i("Future_tab_info_third", "acc " + "gap_"+gap +" mode_"+this.displayMode + " uid_size_"+uid.size() + " count_"+count);
                                    if(t.getAmount()>=0){
                                        inflow+=t.getAmount();
                                    }else{
                                        outflow+=t.getAmount();
                                    }
                                }
                                Log.i("-------------", "tab display mode " + t.getCategory() + " " +gap + " " +  this.displayMode);
                                count++;
                                if(uid.size()!=count  && this.displayMode>=0){
                                    uid.clear();
                                    uid.add(t.getUID());
                                }

//                                Log.i("Future_tab_info_second", "acc " + "gap_"+gap +" mode_"+this.displayMode + " uid_size_"+uid.size() + " count_"+count);

//                                if(t.getAmount()>=0){
//                                    inflow+=t.getAmount();
//                                }else{
//                                    outflow+=t.getAmount();
//                                }
                            }
                            if(((int)gap-this.displayMode)>0) {
                                if(this.displayMode==9){
                                    uid.add(t.getUID());

                                    Log.i("Future_tab_info_third", "acc " + "gap_"+gap +" mode_"+this.displayMode + " uid_size_"+uid.size() + " count_"+count);
                                    if(t.getAmount()>=0){
                                        inflow+=t.getAmount();
                                    }else{
                                        outflow+=t.getAmount();
                                    }
                                }

//                                if (t.getAmount() >= 0) {
//                                    inflow += t.getAmount();
//                                } else {
//                                    outflow += t.getAmount();
//                                }
                            }
                        }
//                        if (prev < uid.size() && this.title=="FURTURE"){
//                            prev=uid.size();
//                            Log.i("Future_tab_info", "acc ");
//
//                        }
                    }
                    break;
                case "Year":
                    date = new Timestamp(System.currentTimeMillis());
                    current = year2int(Long.valueOf(date.toString().substring(0,4)));

                    for (Transaction t:transactions){
                        long gap = current - year2int(t.getYear());
                        Log.i("TransactionListFragment", "year time gap " + t.getUID() + " " + gap+ " " + current + " " +date.toString());

//                        if (gap<0 && this.displayMode==-1){
//                            uid.add(t.getUID());
//                        }
//                        if(gap-this.displayMode==0){
//                            uid.add(t.getUID());
//                            if(t.getAmount()>=0){
//                                inflow+=t.getAmount();
//                            }else{
//                                outflow+=t.getAmount();
//                            }
//                        }
//                        if(gap-this.displayMode==0){
//                            uid.add(t.getUID());
//                            Log.i("TransactionListFragment", "tab display m ode " + this.displayMode);
//
//                            if(t.getAmount()>=0){
//                                inflow+=t.getAmount();
//                            }else{
//                                outflow+=t.getAmount();
//                            }
//                        }
//                        if(gap-this.displayMode>0) {
//                            uid.add(t.getUID());
//                            if (t.getAmount() >= 0) {
//                                inflow += t.getAmount();
//                            } else {
//                                outflow += t.getAmount();
//                            }
//                        }
//                        if (prev < uid.size() && this.title=="FURTURE"){
//                            prev=uid.size();
//                            Log.i("Future_tab_info", "acc ");
//
//                        }
                        if((int)gap<0){
                            if(this.displayMode==-1){

                                uid.add(t.getUID());
                                Log.i("+++++++", "tab display mode " + t.getCategory() + " " + gap + " " + this.displayMode);
                                count++;
                                if(uid.size()!=count && this.displayMode==-1){
                                    uid.clear();
                                    uid.add(t.getUID());

                                }
                                if(this.displayMode==-1){
                                    if(t.getAmount()>=0){
                                        inflow+=t.getAmount();
                                    }else{
                                        outflow+=t.getAmount();
                                    }
                                }
                                Log.i("Future_tab_info_first", "acc " + "gap_"+gap +" mode_"+this.displayMode + " uid_size_"+uid.size() + " count_"+count);

                            }

                        }
                        if((int)gap>=0){
                            if(((int)gap-this.displayMode)==0){
                                if(this.displayMode!=9){
                                    uid.add(t.getUID());

                                    Log.i("Future_tab_info_third", "acc " + "gap_"+gap +" mode_"+this.displayMode + " uid_size_"+uid.size() + " count_"+count);
                                    if(t.getAmount()>=0){
                                        inflow+=t.getAmount();
                                    }else{
                                        outflow+=t.getAmount();
                                    }
                                }
                                Log.i("-------------", "tab display mode " + t.getCategory() + " " +gap + " " +  this.displayMode);
                                count++;
                                if(uid.size()!=count  && this.displayMode>=0){
                                    uid.clear();
                                    uid.add(t.getUID());
                                }

//                                Log.i("Future_tab_info_second", "acc " + "gap_"+gap +" mode_"+this.displayMode + " uid_size_"+uid.size() + " count_"+count);

//                                if(t.getAmount()>=0){
//                                    inflow+=t.getAmount();
//                                }else{
//                                    outflow+=t.getAmount();
//                                }
                            }
                            if(((int)gap-this.displayMode)>0) {
                                if(this.displayMode==9){
                                    uid.add(t.getUID());

                                    Log.i("Future_tab_info_third", "acc " + "gap_"+gap +" mode_"+this.displayMode + " uid_size_"+uid.size() + " count_"+count);
                                    if(t.getAmount()>=0){
                                        inflow+=t.getAmount();
                                    }else{
                                        outflow+=t.getAmount();
                                    }
                                }

//                                if (t.getAmount() >= 0) {
//                                    inflow += t.getAmount();
//                                } else {
//                                    outflow += t.getAmount();
//                                }
                            }
                        }
                    }
                    break;
                case "Quarter":
                default:
                    date = new Timestamp(System.currentTimeMillis());
                    current = month2int(Long.valueOf(date.toString().substring(5,7)), Long.valueOf(date.toString().substring(0,4)));
                    for (Transaction t:transactions){
                        long gap = current - quarter2int(t.getMonth(), t.getYear());
                        Log.i("TransactionListFragment", "quarter time gap " + t.getUID() + " " + gap + " " + current + " " +date.toString());
                        if((int)gap<0){
                            if(this.displayMode==-1){

                                uid.add(t.getUID());
                                Log.i("+++++++", "tab display mode " + t.getCategory() + " " + gap + " " + this.displayMode);
                                count++;
                                if(uid.size()!=count && this.displayMode==-1){
                                    uid.clear();
                                    uid.add(t.getUID());

                                }
                                if(this.displayMode==-1){
                                    if(t.getAmount()>=0){
                                        inflow+=t.getAmount();
                                    }else{
                                        outflow+=t.getAmount();
                                    }
                                }
                                Log.i("Future_tab_info_first", "acc " + "gap_"+gap +" mode_"+this.displayMode + " uid_size_"+uid.size() + " count_"+count);

                            }

                        }
                        if((int)gap>=0){
                            if(((int)gap-this.displayMode)==0){
                                if(this.displayMode!=9){
                                    uid.add(t.getUID());

                                    Log.i("Future_tab_info_third", "acc " + "gap_"+gap +" mode_"+this.displayMode + " uid_size_"+uid.size() + " count_"+count);
                                    if(t.getAmount()>=0){
                                        inflow+=t.getAmount();
                                    }else{
                                        outflow+=t.getAmount();
                                    }
                                }
                                Log.i("-------------", "tab display mode " + t.getCategory() + " " +gap + " " +  this.displayMode);
                                count++;
                                if(uid.size()!=count  && this.displayMode>=0){
                                    uid.clear();
                                    uid.add(t.getUID());
                                }

//                                Log.i("Future_tab_info_second", "acc " + "gap_"+gap +" mode_"+this.displayMode + " uid_size_"+uid.size() + " count_"+count);

//                                if(t.getAmount()>=0){
//                                    inflow+=t.getAmount();
//                                }else{
//                                    outflow+=t.getAmount();
//                                }
                            }
                            if(((int)gap-this.displayMode)>0) {
                                if(this.displayMode==9){
                                    uid.add(t.getUID());

                                    Log.i("Future_tab_info_third", "acc " + "gap_"+gap +" mode_"+this.displayMode + " uid_size_"+uid.size() + " count_"+count);
                                    if(t.getAmount()>=0){
                                        inflow+=t.getAmount();
                                    }else{
                                        outflow+=t.getAmount();
                                    }
                                }

//                                if (t.getAmount() >= 0) {
//                                    inflow += t.getAmount();
//                                } else {
//                                    outflow += t.getAmount();
//                                }
                            }
                        }
//                        if (gap<0 && this.displayMode==-1){
//                            uid.add(t.getUID());
//                        }
//
//                        if(gap-this.displayMode==0){
//                            uid.add(t.getUID());
//                            if(t.getAmount()>=0){
//                                inflow+=t.getAmount();
//                            }else{
//                                outflow+=t.getAmount();
//                            }
//                        }
//                        if(gap-this.displayMode==0){
//                            uid.add(t.getUID());
//                            Log.i("TransactionListFragment", "tab display m ode " + this.displayMode);
//
//                            if(t.getAmount()>=0){
//                                inflow+=t.getAmount();
//                            }else{
//                                outflow+=t.getAmount();
//                            }
//                        }
//                        if(gap-this.displayMode>0) {
//                            uid.add(t.getUID());
//                            if (t.getAmount() >= 0) {
//                                inflow += t.getAmount();
//                            } else {
//                                outflow += t.getAmount();
//                            }
//                        }
                    }
                    break;

            }
            Log.i("Tab_info_end", this.title + " " + uid.size());

//            Log.i("TransactionListFragment", String.join(", ", uid));
            if(order == "Amount"){
                cursor = ((TransactionDbAdapter)databaseAdapter).fetchAllRecordsWithUID_Amount(uid);
            } else{
                cursor = ((TransactionDbAdapter)databaseAdapter).fetchAllRecordsWithUID_Category(uid);

            }
            if (cursor!=null){
                registerContentObserver(cursor);
            }
            sum=inflow-outflow;
//            Log.i("TransactionListFragment", Integer.toString(cursor.getCount()));
            return cursor;
        }
    }
    private static long day2int(long month, long date, long year){
        switch ((int)month-1){
            case 1:
            case 3:
            case 5:
            case 8:
            case 10:
                return (month-1)!=1?date + (month-1)*30:(year%4==0)?date + (month-1)*29:date + (month-1)*28;
            default:
                return (month-1)*31 +date;
        }
    }
    private static long month2int(long month, long year){
        return month+year*12;
    }
    private static long year2int(long year){
        return year;
    }
    private static long quarter2int(long month, long year){
        return (long) Math.floor((month2int(month,year)-1)/3);
    }
    class TransactionRecyclerAdapter extends CursorRecyclerAdapter<TransactionRecyclerAdapter.TransactionViewHolder> {
        public TransactionRecyclerAdapter(Cursor cursor){super(cursor);}

        @Override
        public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_transaction, parent, false);

            return new TransactionViewHolder(view);
        }
        @Override
        public void onBindViewHolderCursor(final TransactionViewHolder holder, final Cursor cursor){
            final String transactionUID = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseSchema.TransactionEntry.COLUMN_UID));
            transactionDbAdapter = TransactionDbAdapter.getInstance();

            holder.transactionId = transactionDbAdapter.getID(transactionUID);
            holder.transactionUid = transactionUID;
 //            holder.card_day.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseSchema.TransactionEntry.COLUMN_DATE)));
//            holder.card_sum.setText(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseSchema.TransactionEntry.COLUMN_AMOUNT)));
//            holder.card_amount.setText(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseSchema.TransactionEntry.COLUMN_AMOUNT)));
//            holder.card_month_year.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseSchema.TransactionEntry.COLUMN_DATE)));
//            holder.card_count.setText("0");
            String cat = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseSchema.TransactionEntry.COLUMN_CATERGORY));
            int amount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseSchema.TransactionEntry.COLUMN_AMOUNT));
            holder.card_icon.setImageResource(getResources().getIdentifier(cat.toLowerCase(), "mipmap", getContext().getPackageName()));
            holder.card_category.setText(cat);
            holder.card_amount.setText(Integer.toString(amount));
            if(amount<0){
                holder.card_amount.setTextColor(getResources().getColor(R.color.red));
            }else{
                holder.card_amount.setTextColor(getResources().getColor(R.color.green));
            }
        }

        class TransactionViewHolder extends RecyclerView.ViewHolder implements androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener {
            TextView card_day;
            TextView card_month_year;
            TextView card_count;
            TextView card_sum;

            TextView card_category;
            TextView card_title;
            TextView card_amount;

            ImageView card_icon;
            TextView card_option;

            long transactionId;
            String transactionUid;
            public TransactionViewHolder(View itemView){
                super(itemView);
//                card_day = itemView.findViewById(R.id.card_day);
//                card_count=itemView.findViewById(R.id.card_count);
//                card_month_year = itemView.findViewById(R.id.card_month_year);
//                card_sum = itemView.findViewById(R.id.card_sum);
                card_icon = itemView.findViewById(R.id.card_category_icon);
                card_option = itemView.findViewById(R.id.card_option);
                card_category = itemView.findViewById(R.id.card_category);
                card_amount = itemView.findViewById(R.id.card_amount);
//                card_title = itemView.findViewById(R.id.card_title);

                card_option.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                        MenuInflater inflater = popupMenu.getMenuInflater();
                        inflater.inflate(R.menu.transaction_option, popupMenu.getMenu());
                        popupMenu.show();
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.transaction_edit:
                                        openCreateOrEditActivity(transactionId);
//                                        Log.i("TransactionListForm", "Edit");

                                        return true;
                                    case R.id.transaction_delete:
                                        tryDeleteTransaction(transactionUid);
//                                        Log.i("TransactionListForm", transactionUid);
                                        return true;
                                    default:
//                                        Log.i("TransactionListForm", "False");

                                        return false;

                                }

                            }
                        });
                    }
                });
            }
            @Override
            public boolean onMenuItemClick(MenuItem item){
                switch (item.getItemId()){
                    case R.id.transaction_edit:
                        openCreateOrEditActivity(transactionId);
                        return true;
                    case R.id.transaction_delete:
                        tryDeleteTransaction(transactionUid);
                        return true;
                    default:
                        return false;
                }
            }
        }
    }
}
