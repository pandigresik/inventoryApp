package com.asligresik.inventoryApp.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import androidx.appcompat.app.AlertDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.asligresik.inventoryApp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by sikurro on 2/8/2017.
 */
public class AppUtility {

    public static String getCurrentDateTime(){
        String waktu = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        waktu = dateFormat.format(date);
        return waktu;
    }

    public static String getCurrentLocalDateTime(Context ctx){
        String waktu;
        DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(ctx);
    //    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        waktu = dateFormat.format(date);
        return waktu;
    }

    public static String formatAngkaRibuan( String angka ){
        return "";
    }

    public static String formatAngkaRibuan( int angka ){
        return "";
    }

    public static String unFormatAngkaRibuan( String angka ){
        return "";
    }

    public static String unFormatAngkaRibuan( int angka ){
        return "";
    }

    public static String getTag( Class aClass ){
        return aClass.getSimpleName();
    }

    private static String convertMonthDate(char a){
        int bulan = ((int) a) - 64;
        String bulanStr = bulan < 10 ? "0"+ bulan : ""+bulan;
        return bulanStr;
    }

    public static TableLayout createTableLayout(Context ctx, String[][] rv) {
        // 1) Create a tableLayout and its params
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
        TableLayout tableLayout = new TableLayout(ctx);
        tableLayout.setBackgroundColor(Color.BLACK);

        // 2) create tableRow params
        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
        tableRowParams.setMargins(1, 1, 1, 1);
        tableRowParams.weight = 1;

        for (int i = 0; i < rv.length ; i++) {
            // 3) create tableRow
            TableRow tableRow = new TableRow(ctx);
            tableRow.setBackgroundColor(Color.BLACK);

            for (int j= 0; j < rv[i].length; j++) {
                TextView textView = new TextView(ctx);
                //  textView.setText(String.valueOf(j));
                textView.setBackgroundColor(Color.WHITE);
                textView.setGravity(Gravity.CENTER);
                textView.setText(rv[i][j]);
            //    textView.setPadding(5,5,5,5);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,10);
                tableRow.addView(textView, tableRowParams);
            }

            tableLayout.addView(tableRow, tableLayoutParams);
        }

        return tableLayout;
    }


    public static void showAlert(Context ctx, String pesan, String title){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        dialog.setIcon(R.drawable.bell);
        String judul = "Informasi";
        if(!title.isEmpty()){
             judul = title;
        }
        dialog.setTitle(judul);
        dialog.setMessage(pesan);
        dialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();            }
        });
        dialog.show();
    }

    /**
     *  Convert one date format string  to another date format string in android
     * @param inputDateFormat Input SimpleDateFormat
     * @param outputDateFormat Output SimpleDateFormat
     * @param inputDate  input Date String
     * @return
     * @throws ParseException
     */
    public static String formatDateFromDateString(String inputDateFormat, String outputDateFormat, String inputDate) throws ParseException {
        Date mParsedDate;
        String mOutputDateString;
        SimpleDateFormat mInputDateFormat = new SimpleDateFormat(inputDateFormat, java.util.Locale.getDefault());
        SimpleDateFormat mOutputDateFormat = new SimpleDateFormat(outputDateFormat, java.util.Locale.getDefault());
        mParsedDate = mInputDateFormat.parse(inputDate);
        mOutputDateString = mOutputDateFormat.format(mParsedDate);
        return mOutputDateString;

    }

    public static HashMap<String,String> extractBarcode(String barcode) {
        HashMap<String,String> hashMap = new HashMap<>();
        String[] keyMap = {"kodeBarang","qty","tgl","referencesNumber"};
        String[] lines = barcode.split("\\r?\\n|&");
        for(int i = 0; i < lines.length; i++){
            hashMap.put(keyMap[i],lines[i]);
        }
        return hashMap;
    }
}
