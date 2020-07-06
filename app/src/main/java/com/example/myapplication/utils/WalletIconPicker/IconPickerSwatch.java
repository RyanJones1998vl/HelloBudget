package com.example.myapplication.utils.WalletIconPicker;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.db.adapters.WalletDbAdapter;
import com.example.myapplication.model.Wallet;

import java.util.ArrayList;
import java.util.List;

public class IconPickerSwatch extends FrameLayout implements View.OnClickListener {
    private String icon;
    private ImageView image;
    private TextView name;
    private ImageView ticker;
    private IconPickerSwatch.OnIconSelectedListener iconSelectedListener;
    private List<String> usedIcons = new ArrayList<String>();

    public interface OnIconSelectedListener{
        public void onIconSelected(String icon);
    }
    private void getUsedIcons(){
        List<Wallet> wallets = WalletDbAdapter.getInstance().getAllWallets();
        for(Wallet w:wallets){
            if(!usedIcons.contains(w.getIcon())){
                usedIcons.add(w.getIcon());
            }
        }
    }
    public IconPickerSwatch(Context context, String _icon, boolean chosen, IconPickerSwatch.OnIconSelectedListener listener){
        super(context);
        getUsedIcons();

        icon = _icon;
        iconSelectedListener = listener;

        LayoutInflater.from(context).inflate(R.layout.icon_picker_swatch, this);
        image = (ImageView) findViewById(R.id.icon_picker_swatch_icon);
        name = (TextView) findViewById(R.id.icon_picker_swatch_name);
        ticker = (ImageView) findViewById(R.id.icon_picker_swatch_ticker);

        setIcon(_icon);
        setChosen(chosen);
        setOnClickListener(this);
        setBackgroundColor(getResources().getColor(R.color.milk));
    }
    private void setIcon(String cat){

        image.setImageResource(getResources().getIdentifier(cat, "mipmap",getContext().getPackageName()));
        name.setText(cat);
        if(usedIcons.contains(cat)){
            ticker.setVisibility(VISIBLE);
        }else{
            ticker.setVisibility(GONE);
        }
//        abb.setText(cat);
    }
    private void setChosen(boolean cho){
        if(cho){
            name.setTextColor(Color.GREEN);
        }else{
            name.setTextColor(Color.BLACK);
        }
    }
    @Override
    public void onClick(View v){
        if(iconSelectedListener!=null){
            iconSelectedListener.onIconSelected(icon);
        }
    }
}
