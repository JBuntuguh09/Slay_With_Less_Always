package com.lonewolf.slay_with_less_always.Resources;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ShortCut_To {

    public static final String DATEWITHTIME = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATEFORMATDDMMYYYY = "dd/MM/yyyy";
    public static final String DATEFORMATDDMMYYYY2 = "dd-MM-yyyy";
    public static final String DATEFORMATYYYYMMDD = "yyyy-MM-dd";
    public static final String TIME = "hh:mm a";
    public static final String DATEWITHTIMEDDMMYYY = "dd-MM-yyyy'T'HH:mm:ss.SSS'Z'";



    public static String getCurrentDatewithTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEWITHTIMEDDMMYYY, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMATYYYYMMDD, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getCurrentDateFormat2() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMATDDMMYYYY, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String[] getCurrenciesLabel = { "Ghana Cedis", "US Dollar","Euro", "UK Pound", "Nigerian Naira", "CFA Franc"};
    public static String[] getCurrencies = { "Ghana Cedis", "US Dollar","Euro", "UK Pound", "Nigerian Naira", "CFA Franc"};


    public static Bitmap decodeBase64(String input) {

        try {
            byte[] decodedByte = Base64.decode(input, 0);
            return BitmapFactory.decodeByteArray(decodedByte, 0,
                    decodedByte.length);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static String getTimeFromDate(String str){
        if(str != null && !str.equalsIgnoreCase("null") && str.trim().length()!=0){
            SimpleDateFormat sdf1 = new SimpleDateFormat(DATEWITHTIME, Locale.US);
            SimpleDateFormat sdf2 = new SimpleDateFormat(TIME, Locale.US);

            try {
                Date date = sdf1.parse(str);
                return sdf2.format(date);
            }catch (Exception e){
                e.printStackTrace();
                return "";
            }
        }else {
            return " ";
        }
    }

    public static String getCurrentDayMonthYear() {

        Calendar c = Calendar.getInstance();
        int currMonth = c.get(Calendar.MONTH);
        int currYear = c.get(Calendar.YEAR);
        int curDay = c.get(Calendar.DAY_OF_MONTH);


        if(currMonth==0){

            return "January "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==1){
            return "February "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==2){
            return "March "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==3){
            return "April "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==4){
            return "May "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==5){
            return "June "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==6){
            return "July "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==7){
            return "August "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==8){
            return "September "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==9){
            return "October "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==10){
            return "November "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }else if(currMonth==11){
            return "December "+String.valueOf(curDay)+", "+String.valueOf(currYear);
        }
        else {
            return "";
        }

    }

    public static String getDateTimeForAPI(String dateFormatted) {
        Calendar apiDate = Calendar.getInstance();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMATDDMMYYYY);
            apiDate.setTime(dateFormat.parse(dateFormatted));
            Calendar corrTime = Calendar.getInstance();
            apiDate.set(Calendar.HOUR_OF_DAY, corrTime.get(Calendar.HOUR_OF_DAY));
            apiDate.set(Calendar.MINUTE, corrTime.get(Calendar.MINUTE));
            apiDate.set(Calendar.SECOND, corrTime.get(Calendar.SECOND));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //2014-03-15T21:04:43.162Z
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEWITHTIME);
        return dateFormat.format(apiDate.getTime());
    }

    public static String getDateForAPP(String strDate) {
        if (strDate != null && !strDate.equalsIgnoreCase("null") && strDate.trim().length() != 0) {

            SimpleDateFormat sdf1 = new SimpleDateFormat(DATEWITHTIME);
            SimpleDateFormat sdf2 = new SimpleDateFormat(DATEFORMATDDMMYYYY2);

            try {

                Date date = sdf1.parse(strDate);
                return sdf2.format(date);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        } else {
            return "";
        }
    }

    public static String getFormatDateAPI(String str) {
        if (str != null && !str.equalsIgnoreCase("null") && str.trim().length() != 0) {

            SimpleDateFormat sdf1 = new SimpleDateFormat(DATEFORMATDDMMYYYY);
            SimpleDateFormat sdf2 = new SimpleDateFormat(DATEFORMATYYYYMMDD);

            try {

                Date date = sdf1.parse(str);
                return sdf2.format(date);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        } else {
            return "";
        }
    }

    public static void sortData(ArrayList<HashMap<String, String>> list, final String field){

            Collections.sort(list, new Comparator<HashMap<String, String>>() {
                @Override
                public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {

                    return lhs.get(field).compareTo(rhs.get(field));
                }
            });

    }

    public static void setPic(StorageReference storageReference, final Activity activity, final ImageView pic, String path, final ProgressBar progressBar){
        storageReference.child(path).getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                DisplayMetrics dm = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

//                imageView.setMinimumHeight(dm.heightPixels);
//                imageView.setMinimumWidth(dm.widthPixels);
                pic.setImageBitmap(bm);
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    public static void setAutoFilter(ArrayList<HashMap<String, String>> arrayList, final String type){
        if(arrayList.size()>0){

            Collections.sort(arrayList, new Comparator<HashMap<String, String>>() {
                @Override
                public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {

                    return Integer.valueOf(rhs.get(type)).compareTo(Integer.parseInt(lhs.get(type)));
                }
            });


        }
    }

    public static String getCurrency(String currency){
        String currCurrency = "GH₵";

        switch (currency) {
            case "Ghana Cedis":

                currCurrency = "GH₵";
                break;
            case "Nigerian Naira":

                currCurrency = "₦";

                break;
            case "US Dollar":

                currCurrency = "$";

                break;
            case "UK Pound":

                currCurrency = "£";

                break;
            case "Euro":

                currCurrency = "€";

                break;
            case "CFA Franc":

                currCurrency = "CFA";

                break;
        }

        return currCurrency;
    }

}
