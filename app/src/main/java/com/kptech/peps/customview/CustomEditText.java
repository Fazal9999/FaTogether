package com.kptech.peps.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.Log;

import com.kptech.peps.R;


/**
 * Created by BXDC46 on 2/27/2017.
 */

public class CustomEditText extends AppCompatEditText {
    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";
    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(attrs,R.styleable.CustomFontTextView);

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

//    @Override
//    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
//        final InputConnection ic = super.onCreateInputConnection(editorInfo);
//        EditorInfoCompat.setContentMimeTypes(editorInfo, new String[]{"image/*", "image/png", "image/gif", "image/jpeg"});
//
//        return InputConnectionCompat.createWrapper(ic, editorInfo,
//                (inputContentInfo, flags, opts) -> {
//                    Toast.makeText(getContext(), "No gif support", Toast.LENGTH_SHORT).show();
//                    return true;
//                });
//    }

}
