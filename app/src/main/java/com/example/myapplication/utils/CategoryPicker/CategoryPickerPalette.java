package com.example.myapplication.utils.CategoryPicker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.Image;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class CategoryPickerPalette extends LinearLayout {
    public CategoryPickerSwatch.OnCategorySelectedListener categorySelectedListener;
    private int numCols;
    public CategoryPickerPalette(Context context, AttributeSet set){super(context, set);}
    public CategoryPickerPalette(Context context){
        super(context);
    }
    public void init(int numCols, CategoryPickerSwatch.OnCategorySelectedListener listener){
        this.numCols = 1;
        categorySelectedListener = listener;
        setBackgroundColor(Color.MAGENTA);
        setOrientation(VERTICAL);
    }
    private TableRow createTableRow(){
        TableRow row = new TableRow(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(params);
        return row;
    }
    private void addSwatchToRow(TableRow row, View swatch, int rowNumber){
        row.addView(swatch, 0);
    }
    public void drawPalette(String[] cats, String selected){
        if(cats == null){
            return ;
        }
        this.removeAllViews();
        int tableElements = 0;
        int rowElements=0;
        int rowNumber = 0;

//        TableRow row = createTableRow();


        for (String cat:cats){
//            tableElements++;
            View swatch = createCategorySwatch(cat, selected);
            addView(swatch);
//            addSwatchToRow(row, swatch, rowNumber);
//
//            rowElements++;
//            if(rowElements==numCols){
//                addView(row);
//                row = createTableRow();
//                rowElements=0;
//                rowNumber++;
//            }
//        }
//        if(rowElements>0){
//            while(rowElements!=numCols){
//                addSwatchToRow(row, createBlankSpace(), rowNumber);
//                rowElements++;
//            }
//            addView(row);
        }
    }
    private ImageView createBlankSpace(){
        ImageView view = new ImageView(getContext());
        return view;
    }
    private CategoryPickerSwatch createCategorySwatch(String cat, String selected){
        CategoryPickerSwatch view = new CategoryPickerSwatch(getContext(), cat, cat==selected, categorySelectedListener);
        view.setBackgroundColor(Color.WHITE);
        return view;
    }
}

