package com.kptech.peps.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;

import com.kptech.peps.R;


/**
 * Created by BXDC46 on 2/17/2017.
 */

public class CustomFontTextView extends AppCompatTextView {
    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";
    public CustomFontTextView(Context context) {
        super(context);
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.CustomFontTextView);

        String fontName = attributeArray.getString(R.styleable.CustomFontTextView_textFont);
        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);

        Typeface customFont = selectTypeface(context, fontName, textStyle);
        setTypeface(customFont);

        attributeArray.recycle();
    }

    private Typeface selectTypeface(Context context, String fontName, int textStyle) {
        if(fontName == null){
            return FontCache.getTypeface("OpenSans-Regular.ttf", context);
        }
        else if (fontName.contentEquals(context.getString(R.string.font_normal))) {
            Log.d("Custom Textview","setting hyundainormal");
            return FontCache.getTypeface("OpenSans-Regular.ttf", context);
        }
        else if (fontName.contentEquals(context.getString(R.string.font_bold))) {
            Log.d("Custom TextView","setting");

            return FontCache.getTypeface("OpenSans-Bold.ttf", context);
        }
        else if (fontName.contentEquals(context.getString(R.string.font_light))) {
            Log.d("Custom TextView","setting");

            return FontCache.getTypeface("OpenSans-Light.ttf", context);
        }
        else if (fontName.contentEquals(context.getString(R.string.font_italic))) {
            Log.d("Custom TextView","setting");

            return FontCache.getTypeface("OpenSans-Italic.ttf", context);
        }
        else {
            // no matching font found
            // return null so Android just uses the standard font (Roboto)
             return FontCache.getTypeface("OpenSans-Regular.ttf", context);
        }
    }

}
