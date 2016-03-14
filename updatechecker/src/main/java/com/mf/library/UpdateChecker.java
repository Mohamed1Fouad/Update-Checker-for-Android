package com.mf.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

    private static Activity activity;
    private String App_Store, new_version, update_btn, remind_btn;
    private int reminder_timer = 0;
    private boolean force_close = false;
    private OnCallBack onCallBack;
    private static SharedPreferences sharedPreferences;

    public UpdateChecker(Activity activity) {
        this.activity = activity;
        sharedPreferences = this.activity.getApplicationContext().getSharedPreferences("updateChk", Activity.MODE_PRIVATE);
    }


    public void checkUpdate() {

        try {

            if (reminder_timer < 0)
                throw new CustomException("Number of days must be Positive Integer");

            if (!web_update() && shouldShowUpdate()) {

                if (onCallBack == null || onCallBack.Done(true, true, new_version))
                    showDialog();
            } else {
                if (onCallBack != null) {
                    onCallBack.Done(true, false, null);
                }
                Log.i("UpdateChecker", "no update found");
            }
        } catch (CustomException e) {
            e.printStackTrace();
            if (onCallBack != null) {
                onCallBack.Done(false, false, null);
            }
        }
    }


    private boolean web_update() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {

            App_Store = activity.getApplicationContext().getPackageName();

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

            return curVersion.equals(newVersion);
        } catch (Exception e) {

            if (e.getMessage().contains("HTTP error fetching URL"))
                e = new CustomException("This package (app) "+App_Store +" not available in store");

            e.printStackTrace();

            if (onCallBack != null) {
                onCallBack.Done(false, false, null);
            }
            return true;
        }

    }


    public UpdateChecker setUpdateLabel(String label) {
        update_btn = label;
        return this;
    }

    public UpdateChecker setRemindLabel(String label) {
        remind_btn = label;
        return this;
    }

    public UpdateChecker setRemindDays(int days) {
        reminder_timer = days;
        return this;
    }

    public UpdateChecker setForceCloseOnSkip(boolean force_close) {
        this.force_close = force_close;
        return this;
    }

    public static void clearReminder(Activity activity) {
        sharedPreferences.edit().putLong("saved_date", 0).commit();

    }

    //test

    private void showDialog() {
        new AlertDialog.Builder(activity)
                .setTitle("New Update Available")
                //  .setMessage("New Version is Available now on Google PLay Store ")
                .setMessage("Version " + new_version + " is Available now on Google Play Store")
                .setPositiveButton(update_btn != null ? update_btn : "Update NOW", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + App_Store)));
                        } catch (ActivityNotFoundException anfe) {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + App_Store)));
                        }
                        sharedPreferences.edit().putLong("saved_date", 0).commit();
                        activity.finish();
                    }

                })
                .setNegativeButton(force_close ? "Exit" : (remind_btn != null ? remind_btn : "Remind me later"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!force_close) {
                            if (reminder_timer != 0) {
                                GregorianCalendar gc = new GregorianCalendar();
                                gc.add(Calendar.DATE, (reminder_timer == 0 ? 1 : reminder_timer));

                                Date today = gc.getTime();
                                sharedPreferences.edit().putLong("saved_date", today.getTime()).commit();
                            } else
                                sharedPreferences.edit().putLong("saved_date", 0).commit();
                        } else {
                            sharedPreferences.edit().putLong("saved_date", 0).commit();
                            activity.finish();
                        }
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private boolean shouldShowUpdate() {
        long date_in_mil = sharedPreferences.getLong("saved_date", 0);
        if (date_in_mil != 0) {
            Date saved_date = new Date(date_in_mil);


            Calendar c = new GregorianCalendar();
            Date today = c.getTime();
            long different = today.getTime() - saved_date.getTime();
            long daysInMilli = 1000 * 60 * 60 * 24;
            long elapsedDays = different / daysInMilli;

            if (DateUtils.isToday(date_in_mil) || elapsedDays >= 1) {

                return true;
            } else
                return false;
            // return today.getDay()==saved_date.getDay();
        } else
            return true;
    }


    public UpdateChecker setOnCallBack(OnCallBack listener) {
        this.onCallBack = listener;
        return this;

    }
}
