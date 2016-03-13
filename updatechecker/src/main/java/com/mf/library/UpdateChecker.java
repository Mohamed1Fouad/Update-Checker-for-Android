package com.mf.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.text.format.DateUtils;
import android.util.Log;
import org.jsoup.Jsoup;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Mohamed1Fouad on 3/13/16.
 */
public class UpdateChecker {

    private Activity activity;
    private String App_Store,new_version,update_btn,remind_btn;
    private int reminder_timer=0;
    public UpdateChecker(Activity activity){
        this.activity=activity;
    }

    public UpdateChecker setAppPackage(String Package){
        App_Store=Package;
        return this;
    }

    public void start(){
        if(!web_update() && shouldShowUpdate() )
            showDialoge();
        else
            Log.i("UpdateChecker","no update found");
    }





    private boolean web_update(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            String curVersion = activity.getApplication().getPackageManager().getPackageInfo(App_Store, 0).versionName;
            String newVersion = curVersion;
            newVersion = Jsoup.connect("http://play.google.com/store/apps/details?id=" + App_Store + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div[itemprop=softwareVersion]")
                    .first()
                    .ownText();
            new_version = newVersion;

            System.out.println("NEW VERSION="+newVersion);
            return curVersion.equals(newVersion) ;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }


    public UpdateChecker setUpdateLabel(String lable){
        update_btn=lable;
        return this;
    }

    public UpdateChecker setRemindLabel(String lable){
        remind_btn=lable;
        return this;
    }

    public UpdateChecker setRemindDays(int days){
        reminder_timer=days;
        return this;
    }

    public UpdateChecker resetReminder(){
        activity.getApplicationContext().getSharedPreferences("updateChk", Activity.MODE_PRIVATE).edit().putLong("saved_date",0).commit();
return  this;
    }

    //test

    private void showDialoge(){
        new AlertDialog.Builder(activity)
                .setTitle("New Update Available")
              //  .setMessage("New Version is Available now on Google PLay Store ")
                .setMessage("Version "+new_version +" is Available now on Google Play Store")
                .setPositiveButton(update_btn!=null ? update_btn :"Update NOW", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + App_Store)));
                        } catch (ActivityNotFoundException anfe) {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + App_Store)));
                        }
                        activity.getApplicationContext().getSharedPreferences("updateChk", Activity.MODE_PRIVATE).edit().putLong("saved_date",0).commit();
                        activity.finish();
                    }

                })
                .setNegativeButton(remind_btn!=null ? remind_btn :"Remind me later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        GregorianCalendar gc = new GregorianCalendar();
                        gc.add(Calendar.DATE, (reminder_timer==0 ? 1: reminder_timer));

                        Date today = gc.getTime();
                        activity.getApplicationContext().getSharedPreferences("updateChk", Activity.MODE_PRIVATE).edit().putLong("saved_date",today.getTime()).commit();
                        System.out.println(new Date(today.getTime()).toString());

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private boolean shouldShowUpdate(){
        long date_in_mil= activity.getApplicationContext().getSharedPreferences("updateChk", Activity.MODE_PRIVATE).getLong("saved_date",0);
        if(date_in_mil!=0) {
            Date saved_date = new Date(date_in_mil);


            Calendar c = new GregorianCalendar();
            Date today = c.getTime();
            long different = today.getTime() - saved_date.getTime();
            long daysInMilli = 1000 * 60 * 60 * 24;
            long elapsedDays = different / daysInMilli;
            System.out.println("DIFF DAYS=="+elapsedDays);
            if(DateUtils.isToday(date_in_mil) ||elapsedDays>=1){

                return true;
            }
            else
                return false;
            // return today.getDay()==saved_date.getDay();
        }
        else
            return true;
    }
}
