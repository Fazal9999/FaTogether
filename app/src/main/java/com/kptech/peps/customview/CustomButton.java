package com.kptech.peps.customview;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import com.kptech.peps.R;
import com.kptech.peps.videoEditor.PortraitCameraActivity;


/**
 * Created by suchandra on 6/6/2018.
 */

public class CustomButton extends androidx.appcompat.widget.AppCompatButton {
    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";
    public CustomButton(Context context) {
        super(context);
    }
    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
        // TODO Auto-generated constructor stub
    }
    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
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
        else {
            // no matching font found
            // return null so Android just uses the standard font (Roboto)
            return FontCache.getTypeface("OpenSans-Regular.ttf", context);
        }
    }
}
