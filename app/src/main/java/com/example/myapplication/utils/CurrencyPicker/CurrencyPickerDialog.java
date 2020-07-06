package com.example.myapplication.utils.CurrencyPicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.utils.WalletPicker.WalletPickerDialog;
import com.example.myapplication.utils.WalletPicker.WalletPickerPalette;
import com.example.myapplication.utils.WalletPicker.WalletPickerSwatch;

public class CurrencyPickerDialog extends DialogFragment implements CurrencyPickerSwatch.OnCurrencySelectedListener {
    protected AlertDialog alertDialog;
    protected static final String KEY_TITLE_ID = "title_id";
    protected static final String KEY_CURRENCIES = "categories";
    protected static final String KEY_SELECTED_CURRENCY = "selected_categories";
    protected static final String KEY_COLUMNS = "columns";
    protected static final String KEY_SIZE = "size";

    protected String title = "";
    protected int columns = 2;
    protected String[] categories;
    protected String selected;
    private CurrencyPickerPalette palette;
    private ProgressBar progressBar;
    protected CurrencyPickerSwatch.OnCurrencySelectedListener listener;

    public CurrencyPickerDialog(){

    }
    public static CurrencyPickerDialog newInstance(String selected){
        CurrencyPickerDialog ret = new CurrencyPickerDialog();
        //initialize
        // setArgs
        String[] categories = {"brazil", "china", "europe", "india", "japan", "malaysia", "singapore", "thailand",
                        "uk", "usa", "vietnam"};
        ret.initialize(categories, selected);

        return ret;
    }
    public void initialize(String[] categories, String selected){
//        setArguments(cols);
        setCurrency(categories, selected);
    }
    public void setCurrency(String[] categories, String selected){
        if(this.categories!=categories || this.selected != selected){
            this.categories = categories;
            this.selected = selected;
            refreshPalette();
        }
    }
    public void setCategories(String[] categories){
        if(this.categories!= categories){
            this.categories = categories;
            refreshPalette();
        }
    }
    public void setSelected(String selected){
        if(this.selected != selected){
            this.selected = selected;
            refreshPalette();
        }
    }
    private void refreshPalette(){
        if(palette!=null && categories!=null){
            palette.drawPalette(this.categories, this.selected);
        }
    }
    public String[] getCategories(){return this.categories;}
    public String getSelected(){return this.selected;}
    @Override
    public void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putStringArray(KEY_CURRENCIES, this.categories);
        bundle.putString(KEY_SELECTED_CURRENCY, this.selected);
    }
    @Override
    public Dialog onCreateDialog(Bundle bundle){
        final Activity activity = getActivity();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.currency_picker_dialog, null);
        progressBar = (ProgressBar) view.findViewById(R.id._currency_progress_bar);
        palette = (CurrencyPickerPalette) view.findViewById(R.id.currency_picker);
        palette.init(columns, this);

        if(this.categories!=null){
            showPaletteView();
        }
        alertDialog = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(view)
                .create();

        return alertDialog;
    }
    @Override
    public void onCurrencySelected(String selected){
        if(listener!=null){
            listener.onCurrencySelected(selected);
        }
        if(getTargetFragment() instanceof CurrencyPickerSwatch.OnCurrencySelectedListener){
            final CurrencyPickerSwatch.OnCurrencySelectedListener listener = (CurrencyPickerSwatch.OnCurrencySelectedListener) getTargetFragment();
            listener.onCurrencySelected(selected);
        }
        if(selected != this.selected){
            this.selected = selected;
            palette.drawPalette(categories, selected);
        }
        dismiss();
    }
    public void showProgressBarView(){
        if(progressBar!=null && palette!=null){
            progressBar.setVisibility(View.VISIBLE);
            palette.setVisibility(View.GONE);
        }
    }
    public void showPaletteView() {
        if (progressBar != null && palette != null) {
            progressBar.setVisibility(View.GONE);
            refreshPalette();
            palette.setVisibility(View.VISIBLE);
        }
    }

    public void setArguments(int cols){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE_ID, "Categories");
        bundle.putInt(KEY_COLUMNS, cols);
        setArguments(bundle);
    }
    public void setOnCurrencySelectedListener(CurrencyPickerSwatch.OnCurrencySelectedListener _listener){
        listener = _listener;
    }
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);

        if(getArguments() != null){
            title = getArguments().getString(KEY_TITLE_ID);
            columns = getArguments().getInt(KEY_COLUMNS);
        }
        if(bundle!=null){
            categories = bundle.getStringArray(KEY_CURRENCIES);
            selected = (String) bundle.getString(KEY_SELECTED_CURRENCY);
        }
    }
}
