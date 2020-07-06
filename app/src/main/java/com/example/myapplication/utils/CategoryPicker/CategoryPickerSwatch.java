package com.example.myapplication.utils.CategoryPicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;

public class CategoryPickerSwatch extends FrameLayout implements View.OnClickListener {
    private String category;
    private ImageView icon;
    private TextView name;
    private OnCategorySelectedListener categorySelectedListener;


    public interface OnCategorySelectedListener{
        public void onCategorySeletected(String category);

    }
    public CategoryPickerSwatch(Context context, String _category, boolean chosen, OnCategorySelectedListener listener){
        super(context);

        category = _category;
        categorySelectedListener = listener;

        LayoutInflater.from(context).inflate(R.layout.category_picker_swatch, this);
        icon = (ImageView) findViewById(R.id.category_picker_swatch_icon);
        name = (TextView) findViewById(R.id.category_picker_swatch_name);

        icon.setImageResource(getResources().getIdentifier(_category.toLowerCase(), "mipmap", getContext().getPackageName()));
        name.setText(_category);

//        setCategory(_category);
        setChosen(chosen);
        setOnClickListener(this);
    }
    private void setCategory(String cat){
//        icon.setImageResource(getResources().getIdentifier(cat, "drawable",getContext().getPackageName()));
        name.setText(cat.split("_").toString());

    }
    private void setChosen(boolean cho){
        if(cho){
            name.setTextColor(getResources().getColor(R.color.black));
        }else{
            name.setTextColor(getResources().getColor(R.color.gray));
        }
    }
    @Override
    public void onClick(View v){
        if(categorySelectedListener!=null){
            categorySelectedListener.onCategorySeletected(category);
        }
    }
}
