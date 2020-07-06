package com.example.myapplication.utils.WalletIconPicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;

public class IconPickerDialog extends DialogFragment implements IconPickerSwatch.OnIconSelectedListener {
    protected AlertDialog alertDialog;
    protected static final String KEY_TITLE_ID = "title_id";
    protected static final String KEY_ICONS = "categories";
    protected static final String KEY_SELECTED_ICON = "selected_categories";
    protected static final String KEY_COLUMNS = "columns";
    protected static final String KEY_SIZE = "size";

    protected String title = "";
    protected int columns = 2;
    protected String[] categories;
    protected String selected;
    private IconPickerPalette palette;
    private ProgressBar progressBar;
    protected IconPickerSwatch.OnIconSelectedListener listener;

    public IconPickerDialog(){

    }
    public static IconPickerDialog newInstance(String selected){
        IconPickerDialog ret = new IconPickerDialog();
        //initialize
        // setArgs
        String[] categories = {"account_1", "account_2", "account_3", "account_4", "account_5",
                "account_6", "account_7", "account_8", "account_9", "account_10", "account_11", "account_12"};
//        this.categories=categories;
        Log.i("IconPickerDialog", "count_" + categories.length);

        ret.initialize(categories, selected);

        return ret;
    }
    public void initialize(String[] categories, String selected){
//        setArguments(cols);
        setIcon(categories, selected);
    }
    public void setIcon(String[] categories, String selected){
        if(this.categories!=categories || this.selected != selected){
            this.categories = categories;
//            Log.i("IconPickerDialog", "count_" + categories.length);
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
            palette.drawPalette(getCategories(), this.selected);
        }
    }
    public String[] getCategories(){return this.categories;}
    public String getSelected(){return this.selected;}
    @Override
    public void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putStringArray(KEY_ICONS, this.categories);
        bundle.putString(KEY_SELECTED_ICON, this.selected);
    }
    @Override
    public Dialog onCreateDialog(Bundle bundle){
        final Activity activity = getActivity();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.icon_picker_dialog, null);
        progressBar = (ProgressBar) view.findViewById(R.id._icon_progress_bar);
        palette = (IconPickerPalette) view.findViewById(R.id.icon_picker);
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
    public void onIconSelected(String selected){
        if(listener!=null){
            listener.onIconSelected(selected);
        }
        if(getTargetFragment() instanceof IconPickerSwatch.OnIconSelectedListener){
            final IconPickerSwatch.OnIconSelectedListener listener = (IconPickerSwatch.OnIconSelectedListener) getTargetFragment();
            listener.onIconSelected(selected);
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
    public void setOnIconSelectedListener(IconPickerSwatch.OnIconSelectedListener _listener){
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
            categories = bundle.getStringArray(KEY_ICONS);
            selected = (String) bundle.getString(KEY_SELECTED_ICON);
        }
    }
}
