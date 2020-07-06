package com.example.myapplication.utils.CurrencyPicker;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.utils.WalletPicker.WalletPickerSwatch;

public class CurrencyPickerSwatch extends FrameLayout implements View.OnClickListener {
    private String currency;
    private ImageView icon;
    private TextView name;
    private TextView abb;
    private CurrencyPickerSwatch.OnCurrencySelectedListener currencySelectedListener;


    public interface OnCurrencySelectedListener{
        public void onCurrencySelected(String currency);
    }
    public CurrencyPickerSwatch(Context context, String _currency, boolean chosen, CurrencyPickerSwatch.OnCurrencySelectedListener listener){
        super(context);

        currency = _currency;
        currencySelectedListener = listener;

        LayoutInflater.from(context).inflate(R.layout.currency_picker_swatch, this);
        icon = (ImageView) findViewById(R.id.currency_picker_swatch_icon);
        name = (TextView) findViewById(R.id.currency_picker_swatch_name);
        abb = (TextView)findViewById(R.id.currency_picker_swatch_abb);

        setCurrency(_currency);
        setChosen(chosen);
        setOnClickListener(this);
        setBackgroundColor(getResources().getColor(R.color.milk));
    }
    private void setCurrency(String cat){
        icon.setImageResource(getResources().getIdentifier(cat, "mipmap",getContext().getPackageName()));
        name.setText(cat);
        String abbreviation ;
        switch (cat.toLowerCase()){
            case "brazil":
                abbreviation = "BZL";
                break;
            case "china":
                abbreviation = "CHN";
                break;
            case "europe":
                abbreviation = "ERP";
                break;
            case "india":
                abbreviation = "IDN";
                break;
            case "japan":
                abbreviation = "JPN";
                break;
            case "malaysia":
                abbreviation = "MLS";
                break;
            case "singapore":
                abbreviation = "SGP";
                break;
            case "thailand":
                abbreviation = "THL";
                break;
            case "uk":
                abbreviation = "UK";
                break;
            case "use":
                abbreviation = "USA";
                break;
            case "vietnam":
                abbreviation = "VND";
                break;
            default:
                abbreviation = cat.toUpperCase();

        }
        abb.setText(abbreviation);
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
        if(currencySelectedListener!=null){
            currencySelectedListener.onCurrencySelected(currency);
        }
    }
}
