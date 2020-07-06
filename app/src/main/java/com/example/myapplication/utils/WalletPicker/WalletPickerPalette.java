package com.example.myapplication.utils.WalletPicker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.Image;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class WalletPickerPalette extends LinearLayout {
    public WalletPickerSwatch.OnWalletSelectedListener walletSelectedListener;
    private int numCols;
    public WalletPickerPalette(Context context, AttributeSet set){super(context, set);}
    public WalletPickerPalette(Context context){
        super(context);
    }
    public void init(int numCols, WalletPickerSwatch.OnWalletSelectedListener listener){
        this.numCols = numCols;
        walletSelectedListener = listener;

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
//        this.removeAllViews();
//        int tableElements = 0;
//        int rowElements=0;
//        int rowNumber = 0;
//
//        TableRow row = createTableRow();
        for (String cat:cats){
//            tableElements++;
            View swatch = createWalletSwatch(cat, selected);
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
    private WalletPickerSwatch createWalletSwatch(String cat, String selected){
        WalletPickerSwatch view = new WalletPickerSwatch(getContext(), cat, cat==selected, walletSelectedListener);
        return view;
    }
}
