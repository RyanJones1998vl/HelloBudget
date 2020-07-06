package com.example.myapplication.utils.WalletPicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import androidx.fragment.app.DialogFragment;
import com.example.myapplication.utils.WalletPicker.WalletPickerSwatch.OnWalletSelectedListener;
import com.example.myapplication.R;

public class WalletPickerDialog extends DialogFragment implements OnWalletSelectedListener {
    protected AlertDialog alertDialog;
    protected static final String KEY_TITLE_ID = "title_id";
    protected static final String KEY_CATEGORIES = "categories";
    protected static final String KEY_SELECTED_CATEGORIES = "selected_categories";
    protected static final String KEY_COLUMNS = "columns";
    protected static final String KEY_SIZE = "size";

    protected String title = "";
    protected int columns = 2;
    protected String[] categories;
    protected String selected;
    private WalletPickerPalette palette;
    private ProgressBar progressBar;
    protected WalletPickerSwatch.OnWalletSelectedListener listener;

    public WalletPickerDialog(){

    }
    public static WalletPickerDialog newInstance(int cols, String[] categories, String selected){
        WalletPickerDialog ret = new WalletPickerDialog();
        //initialize
        // setArgs
        ret.initialize(cols, categories, selected);

        return ret;
    }
    public void initialize(int cols, String[] categories, String selected){
        setArguments(cols);
        setWallet(categories, selected);
    }
    public void setWallet(String[] categories, String selected){
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
        bundle.putStringArray(KEY_CATEGORIES, this.categories);
        bundle.putString(KEY_SELECTED_CATEGORIES, this.selected);
    }
    @Override
    public Dialog onCreateDialog(Bundle bundle){
        final Activity activity = getActivity();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.wallet_picker_dialog, null);
        progressBar = (ProgressBar) view.findViewById(R.id._wallet_progress_bar);
        palette = (WalletPickerPalette) view.findViewById(R.id.wallet_picker);
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
    public void onWalletSeletected(String selected){
        if(listener!=null){
            listener.onWalletSeletected(selected);
        }
        if(getTargetFragment() instanceof WalletPickerSwatch.OnWalletSelectedListener){
            final WalletPickerSwatch.OnWalletSelectedListener listener = (WalletPickerSwatch.OnWalletSelectedListener) getTargetFragment();
            listener.onWalletSeletected(selected);
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
        bundle.putString(KEY_TITLE_ID, "Wallets");
        bundle.putInt(KEY_COLUMNS, cols);
        setArguments(bundle);
    }
    public void setOnWalletSelectedListener(WalletPickerSwatch.OnWalletSelectedListener _listener){
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
            categories = bundle.getStringArray(KEY_CATEGORIES);
            selected = (String) bundle.getString(KEY_SELECTED_CATEGORIES);
        }
    }

}
