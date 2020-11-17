package com.example.gisulee.lossdog.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateCalculator {

    static public String getTodayToString(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    /* return value
    * case 0    d1 == d2
    * case 1    d1 > d2
    * case -1   d1 < d2 */
    static public int compare(String strDate1, String strDate2){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = null;
        Date d2 = null;

        try {
            d1 = sdf.parse(strDate1);
            d2 = sdf.parse(strDate2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return d1.compareTo(d2);
    }

    static public String getBeforeMonthToString(int N) {
        Calendar cal = new GregorianCalendar(Locale.KOREA);
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -N); // 한달을 더한다.

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(cal.getTime());
        return strDate;
    }

    static public String getConvertFormat(String strDate){
        String result = strDate.replaceAll("-","");
        return result;
    }

    static public int getYearFromString(String strDate){
        Date dateBeginDate = new Date();
        SimpleDateFormat transFormat;
        if(strDate.contains("-"))
            transFormat = new SimpleDateFormat("yyyy-MM-dd");
        else
            transFormat = new SimpleDateFormat("yyyyMMdd");

        try {
            dateBeginDate = transFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = new GregorianCalendar(Locale.KOREA);
        cal.setTime(dateBeginDate);
        return cal.get(Calendar.YEAR);
    }

    static public int getMonthFromString(String strDate){
        Date dateBeginDate = new Date();
        SimpleDateFormat transFormat;
        if(strDate.contains("-"))
            transFormat = new SimpleDateFormat("yyyy-MM-dd");
        else
            transFormat = new SimpleDateFormat("yyyyMMdd");

        try {
            dateBeginDate = transFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = new GregorianCalendar(Locale.KOREA);
        cal.setTime(dateBeginDate);
        return cal.get(Calendar.MONTH);
    }

    static public int getDayFromString(String strDate){
        Date dateBeginDate = new Date();
        SimpleDateFormat transFormat;
        if(strDate.contains("-"))
            transFormat = new SimpleDateFormat("yyyy-MM-dd");
        else
            transFormat = new SimpleDateFormat("yyyyMMdd");

        try {
            dateBeginDate = transFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = new GregorianCalendar(Locale.KOREA);
        cal.setTime(dateBeginDate);
        return cal.get(Calendar.DAY_OF_MONTH);
    }
}
