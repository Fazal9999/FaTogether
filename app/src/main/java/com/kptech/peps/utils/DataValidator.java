package com.kptech.peps.utils;

import android.text.TextUtils;
import android.widget.EditText;

/**
 * Created by bxdc46 on 6/4/2016.
 */
public class DataValidator {

    //CHeck if a given string is not null and empty
    public static boolean isValid(String val){
        if( (val != null) && !val.isEmpty()&&!val.equals("")){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isDataValid(EditText edt){
        if( edt.getText() != null && isValid(edt.getText().toString())){
            return true;
        }else{
            String error = ""+ edt.getHint();
            edt.setError(error);
            return false;
        }
    }


   //CHeck the email format
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


}
