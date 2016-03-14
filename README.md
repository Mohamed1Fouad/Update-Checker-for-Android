# Update-Checker-for-Android

The project aims to provide check new update available of your application on the Store so you can save your time and use this library since Google play haven't provide any API to check. how it works ? it detect your application package and version and check if there is another version available on store or not .

## Features
- Show Alert Dialog and with 2 options : update now and (remind me lateror exit).
- Detect your package and version automatically
- Can create your custom buttons (change text) for update now and remind me later.
- Can make app force close if user not update.
- Specify number of days that library will not take effect when user choose "remind me"
- Callback to implement your own custom logic.

## Screenshots
![Screenshot](https://github.com/Mohamed1Fouad/Update-Checker-for-Android/blob/master/device-2016-03-13-230931.png)


## Quick Setup

#### Automatically ([ ![Download](https://api.bintray.com/packages/mohamed1fouad/maven/Update-Checker-for-android/images/download.svg) ](https://bintray.com/mohamed1fouad/maven/Update-Checker-for-android/_latestVersion)):
**Automatically with Gradle**
``` gradle
dependencies {
    compile 'com.m1f:updatechecker:1.1.2'
}
```
 
## Manual:

### 1. Import library

### 2. Android Manifest
``` xml
<manifest>
    <!-- Include following permission -->
	<uses-permission android:name="android.permission.INTERNET" />
	...
</manifest>
```

### 3. Activity class
``` java
public class MainActivity extends AppCompatActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       
       new UpdateChecker(this)
                .setUpdateLabel("Update") // optional - this to edit update button (default is "Update NOW")
                .setRemindLabel("Remind me") // optional - this to edit remind button (default is "Remind me later")
                .setRemindDays(2) //optional app remind user every 2 days (default is everyday)
                .setForceCloseOnSkip(true) //optional user will choose update or close app 
                .setOnCallBack(new OnCallBack() {     //optional Callback to implement your own custom logic it
                    @Override
                    public boolean Done(boolean success, boolean isUpdateAvailable, String new_version) {
                        System.out.println("is success=" + success + " is update available=" + isUpdateAvailable + " new version is" + new_version);

                        //return true will show default library dialog if new version available 
                        // and false will hide the dialog 
                        
                        return true;
                    }
                }).checkUpdate();  //start app update checker
        
        UpdateChecker.clearReminder(this); // if you setRemindDays and want to clear cache to appear everyday again 
       
       
    }

    
}
```

### License

```
The MIT License (MIT)

 Copyright (c) 2016 Mohamed Fouad

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 ```
