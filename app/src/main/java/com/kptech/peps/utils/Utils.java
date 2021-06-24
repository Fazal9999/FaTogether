package com.kptech.peps.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by suchandra on 9/17/2018.
 */

public class Utils {
    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    public static SharedPreferences preferences;

    //Spannable String
    public static void setSpannableString(Context context, TextView tv, String txt, int startIndex, int endIndex, int color) {
        Spannable wordToSpan = new SpannableString(txt);
        ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(color);
        wordToSpan.setSpan(foregroundSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(wordToSpan);
    }

    public static void setSpannableStringTillFirstWord(Context context, TextView tv, String txt, int color) {
        if (DataValidator.isValid(txt)) {
            String arr[] = txt.split(" ");
            Spannable wordToSpan = new SpannableString(txt);
            ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(color);
            wordToSpan.setSpan(foregroundSpan, 0, arr[0].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(wordToSpan);
        }
    }

    public static void setSpannableStringTillFirstWordWithFont(Context context, TextView tv, String txt, int color, float font) {
        if (DataValidator.isValid(txt)) {
            String arr[] = txt.split(" ");
            Spannable wordToSpan = new SpannableString(txt);
            ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(color);
            wordToSpan.setSpan(new RelativeSizeSpan(1.3f), 0, arr[0].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordToSpan.setSpan(foregroundSpan, 0, arr[0].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(wordToSpan);
        }
    }

    public static void setSpannableStringWithFont(Context context, TextView tv, String txt, int startIndex, int endIndex, int color) {
        Spannable wordToSpan = new SpannableString(txt);
        wordToSpan.setSpan(new RelativeSizeSpan(1.3f), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(color);
        wordToSpan.setSpan(foregroundSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(wordToSpan);
    }

    public static String getSpannableStringWithFont(Context context, String txt, int startIndex, int endIndex, int color) {
        Spannable wordToSpan = new SpannableString(txt);
        wordToSpan.setSpan(new RelativeSizeSpan(1.3f), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(color);
        wordToSpan.setSpan(foregroundSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return wordToSpan.toString();
    }

    public static Spannable getSpannableWithFont(Context context, String txt, int startIndex, int endIndex, int color) {
        Spannable wordToSpan = new SpannableString(txt);
        wordToSpan.setSpan(new RelativeSizeSpan(1.2f), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(color);
        wordToSpan.setSpan(foregroundSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return wordToSpan;
    }

    //Get Json String
    public String getParamString(HashMap<String, String> req) {
        JSONObject params = new JSONObject();

        for (String key : req.keySet()) {
            try {
                params.put(key, req.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return params.toString();
    }

    public static String getHashedPwd(String pwd) {
        MessageDigest md = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(pwd.getBytes());
            byte[] digest = md.digest();
            if (digest != null)
                for (int i = 0; i < digest.length; i++)
                    stringBuffer.append(String.format("%02x", 0xFF & digest[i]));


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    public static String getStringValForArray(List<String> list) {
        String strVal = "";
        if (list != null && list.size() > 0) {
            int i = 0;
            for (String val : list) {
                if (i > 0) {
                    strVal += ",";
                }
                strVal += val;
                i++;
            }
        }
        return strVal;
    }

    public static List<String> getArrayFromString(String val) {
        List<String> list = new ArrayList<>();
        if (val != null) {
            String[] array = val.split(",");
            for (int i = 0; i < array.length; i++) {
                list.add(array[i]);
            }
        }

        return list;
    }

    public static String getKey(String email) {
        String result = "";
        for (int i = 0; i < email.length(); i++) {
            if ((email.charAt(i) != '.') && (email.charAt(i) != '@')) {
                result += email.charAt(i);
            }
        }
        return result;
    }

    public static String howLongAgo(String date) {
        SimpleDateFormat sdf = mSimpleDateFormat;
        Date postTime = new Date();
        Date nowTime = new Date();
        String agoTime = "huh";

        try {
            if (postTime!=null){
                postTime = sdf.parse(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert postTime != null;
        long timePast = nowTime.getTime() - postTime.getTime();

        long seconds = 1000;
        long minutes = seconds * 60;
        long hours = minutes * 60;
        long days = hours * 24;
        long weeks = days * 7;
        long months = weeks * 4;
        long years = months * 12;

        long dayCount = timePast / days;
        long weekCount = timePast / weeks;
        long monthCount = timePast / months;
        long yearCount = timePast / years;

        if(dayCount != 0) {
            timePast = timePast % days;
        }

        long hourCount = timePast / hours;
        if (hourCount != 0) {
            timePast = hours / timePast;
        }

        long minCount = timePast / minutes;
        if (minCount != 0) {
            timePast = timePast % minutes;
        }

        long secCount = timePast / seconds;
        if (secCount != 0) {
            timePast = timePast % seconds;
        }


        if (yearCount > 0) {
            if (yearCount > 1) {
                agoTime = yearCount + " years ago";
            } else {
                agoTime = "Last year";
            }
        } else if (monthCount > 0) {
            if (monthCount > 2) {
                agoTime = monthCount + " months ago";
            } else {
                agoTime = "Last month";
            }
        } else if (weekCount > 0) {
            if (weekCount > 1) {
                agoTime = weekCount + " weeks ago";
            } else {
                agoTime = "Last week";
            }
        } else if (dayCount > 0) {
            if (dayCount > 1) {
                agoTime = dayCount + " days ago";
            } else {
                agoTime = "Yesterday";
            }
        } else if (hourCount > 0) {
            if (hourCount > 1) {
                agoTime = hourCount + " hours ago";
            } else {
                agoTime = hourCount + " hour ago";
            }
        } else if (minCount > 0) {
            if (minCount > 1) {
                agoTime = minCount + " minutes ago";
            } else {
                agoTime = minCount + " minute ago";
            }
        } else if (secCount <= 10) {
            agoTime = "moments ago";
        } else {
            agoTime = secCount + " seconds ago";
        }

        return agoTime;
    }

    public static String getGrpName(String name){
        String val = "";
        String [] words = name.split("\\s+");
        int i=0;
        for(String k:words){
            i++;
            val += k.charAt(0);

            if(i >=2)
                break;
        }

        return val;
    }

    public static String randomString(int count){
        final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random RANDOM = new Random();
        StringBuilder sb = new StringBuilder(count);

        for (int i = 0; i < count; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString();
    }

    public static String getTodayString(Context context){
        Date date = new Date();
        DateFormat dateFormat = mSimpleDateFormat;
        return dateFormat.format(date);
    }

}
