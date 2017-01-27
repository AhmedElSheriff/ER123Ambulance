package com.example.android.er123ambulance;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Abshafi on 1/27/2017.
 */

public class MyEditText extends EditText {

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setError(CharSequence error, Drawable icon) {
        setCompoundDrawables(null,null,icon,null);
    }
}
