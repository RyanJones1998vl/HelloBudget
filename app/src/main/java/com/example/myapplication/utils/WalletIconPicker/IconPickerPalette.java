package com.example.myapplication.utils.WalletIconPicker;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;


public class IconPickerPalette extends LinearLayout {
    public IconPickerSwatch.OnIconSelectedListener iconSelectedListener;
    private int numCols;
    public IconPickerPalette(Context context, AttributeSet set){super(context, set);}
    public IconPickerPalette(Context context){
        super(context);
    }
    public void init(int numCols, IconPickerSwatch.OnIconSelectedListener listener){
        this.numCols = numCols;
        iconSelectedListener = listener;

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
        int id = 0;
//        this.removeAllViews();
//        int tableElements = 0;
//        int rowElements=0;
//        int rowNumber = 0;
//
//        TableRow row = createTableRow();
        Log.i("IconPikcerPalette", "count_" + cats.length);

        for (String cat:cats){
//            tableElements++;
            Log.i("IconPikcerPalette", "name_"+cat + " id_" + ++id);

            View swatch = createIconSwatch(cat, selected);
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
    private IconPickerSwatch createIconSwatch(String cat, String selected){
        IconPickerSwatch view = new IconPickerSwatch(getContext(), cat, cat==selected, iconSelectedListener);
        return view;
    }
}
