package com.kptech.peps.customview;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Farman on 7/19/2017.
 */

public class TokenTextView extends TextView{
    public TokenTextView(Context context) {
        super(context);
    }

    public TokenTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
    }
}
