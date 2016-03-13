package com.ta3rifah.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mf.updatechecker.R;
import com.mf.updatechecker.UpdateChecker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        UpdateChecker up= new UpdateChecker(this);
//        up.setAppPackage("com.ta3rifah.android");
//        up.setRemindLabel("remind me NOW");
//        up.resetReminder();
//        up.start();

        new UpdateChecker(this).setAppPackage("com.ta3rifah.android").resetReminder().setUpdateLabel("NOOOW").start();


    }


}
