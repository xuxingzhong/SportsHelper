package com.example.root.sportshelper.fonts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by root on 17-7-31.
 */

public class Black_Simplified_TextView extends AppCompatTextView {
    public Black_Simplified_TextView(Context context) {
        super(context);
        init(context);
    }

    public Black_Simplified_TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Black_Simplified_TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        Typeface newFont = Typeface.createFromAsset(context.getAssets(), "fonts/Black_Simplified.ttf");
        setTypeface(newFont);
    }
}
