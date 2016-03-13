package com.mf.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mf.library.OnCallBack;
import com.mf.library.UpdateChecker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

   new UpdateChecker(this)
           .setRemindDays(0)
           .setRemindLabel("ok")
           .setForceCloseOnSkip(true)
           .setOnCallBack(new OnCallBack() {
       @Override
       public boolean Done(boolean success, boolean isUpdateAvailable, String new_version) {
           System.out.println("is success="+success+" is update available="+isUpdateAvailable+" new version is"+ new_version);
           return true;
       }
   }).checkUpdate();
    }
}
