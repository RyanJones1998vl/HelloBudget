package com.example.myapplication.utils.WalletSwitch;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;

import com.example.myapplication.utils.WalletSwitch.WalletSwitcherSwatch;

public class WalletSwitcherPalette extends LinearLayout {
    public WalletSwitcherSwatch.OnWalletSelectedListener walletSelectedListener;
    private int numCols;
    public WalletSwitcherPalette(Context context, AttributeSet set){super(context, set);}
    public WalletSwitcherPalette(Context context){
        super(context);
    }
    public void init(int numCols, WalletSwitcherSwatch.OnWalletSelectedListener listener){
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
    public void drawPalette(String[] cats, String selected){
        if(cats == null){
            return ;
        }
        for (String cat:cats){
            View swatch = createWalletSwatch(cat, selected);
            addView(swatch);
        }
    }
    private ImageView createBlankSpace(){
        ImageView view = new ImageView(getContext());
        return view;
    }
    private WalletSwitcherSwatch createWalletSwatch(String cat, String selected){
        WalletSwitcherSwatch view = new WalletSwitcherSwatch(getContext(), cat, cat==selected, walletSelectedListener);
        return view;
    }
}
