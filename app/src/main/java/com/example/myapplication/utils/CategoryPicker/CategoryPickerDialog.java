package com.example.myapplication.utils.CategoryPicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import androidx.fragment.app.DialogFragment;
import com.example.myapplication.utils.CategoryPicker.CategoryPickerSwatch.OnCategorySelectedListener;
import com.example.myapplication.R;

public class CategoryPickerDialog extends DialogFragment implements OnCategorySelectedListener {
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
    private CategoryPickerPalette palette;
    private ProgressBar progressBar;
    protected CategoryPickerSwatch.OnCategorySelectedListener listener;

    public CategoryPickerDialog(){

    }
    public static CategoryPickerDialog newInstance(int cols, String[] categories, String selected){
        CategoryPickerDialog ret = new CategoryPickerDialog();
        //initialize
        // setArgs
        ret.initialize(cols, categories, selected);

        return ret;
    }
    public void initialize(int cols, String[] categories, String selected){
        setArguments(cols);
        setCategory(categories, selected);
    }
    public void setCategory(String[] categories, String selected){
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
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.category_picker_dialog, null);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        palette = (CategoryPickerPalette) view.findViewById(R.id.category_picker);
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
    public void onCategorySeletected(String selected){
        if(listener!=null){
            listener.onCategorySeletected(selected);
        }
        if(getTargetFragment() instanceof CategoryPickerSwatch.OnCategorySelectedListener){
            final CategoryPickerSwatch.OnCategorySelectedListener listener = (CategoryPickerSwatch.OnCategorySelectedListener) getTargetFragment();
            listener.onCategorySeletected(selected);
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
    public void setOnCategorySelectedListener(CategoryPickerSwatch.OnCategorySelectedListener _listener){
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
