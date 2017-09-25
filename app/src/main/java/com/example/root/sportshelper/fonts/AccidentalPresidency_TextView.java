package com.example.root.sportshelper.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by root on 17-8-2.
 */

public class AccidentalPresidency_TextView extends AppCompatTextView {
    public AccidentalPresidency_TextView(Context context) {
        super(context);
        init(context);
    }

    public AccidentalPresidency_TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AccidentalPresidency_TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        Typeface newFont = Typeface.createFromAsset(context.getAssets(), "fonts/AccidentalPresidency.ttf");
        setTypeface(newFont);
    }
}
