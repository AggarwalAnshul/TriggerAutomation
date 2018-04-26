package com.apkglobal.keeden;

/**
 * Created by Anshul Aggarwal on 31-01-2018.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.AlarmClock;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Anshul Aggarwal on 28-01-2018.
 */

public class Headset extends Service {

    private final static String TAG = Wifi.class.getSimpleName();
    SQLiteDatabase sd;
    String time_start, time_end;
    String battery;
    String bluetooth;
    int user_wifi_state, headset;
    int batLevel;
    String bluetooth_state = null;
    String tag = "--------->";
    int netId = 0;

    int user_hour_start = 0;
    int user_minute_start = 0;
    int user_hour_end = 0;
    int user_minute_end = 0;
    String user_extract[] = new String[2];
    String user_extract2[] = new String[2];


    /*used for Extracting numbers from the string*/
    public static int extractAlphaNumeric(String alphaNumeric) {
        Log.d("----------------->", "call form Alphanumeric: Content: " + alphaNumeric + "length: " + alphaNumeric.length());
        if (alphaNumeric.equals("null")) {
            return -1;
        }
        if (alphaNumeric.length() > 0) {
            alphaNumeric = alphaNumeric.replaceAll("\\D+", "");
        }
        int extract_no = alphaNumeric.length() > 0 ? Integer.parseInt(alphaNumeric) : 0;
        return extract_no;
    }

    public void createdatabase() {
        sd = openOrCreateDatabase("trigger3", Context.MODE_PRIVATE, null);
        sd.execSQL("create table if not exists conditions(headset integer, time_start varchar(5), time_end varchar(5) , bluetooth varchar(5) , battery varchar(2) , wifi integer );");
    }

    private void createActions() {
        sd = openOrCreateDatabase("trigger3", Context.MODE_PRIVATE, null);
        createdatabase();
        sd.execSQL("create table if not exists actions(wifi varchar(3), sound_profile varchar(3)" +
                ", play_music varchar(3), bluetooth varchar(3), timer varchar(5)" +
                " ,alarm varchar(5) );");
    }

    public void delete_data() {
        sd = openOrCreateDatabase("trigger3", Context.MODE_PRIVATE, null);
        sd.delete("conditions", null, null);
    }

    public void read_data() {

        sd = openOrCreateDatabase("trigger3", Context.MODE_PRIVATE, null);
        createdatabase();
        Cursor c = sd.rawQuery("select * from conditions", null);
        Log.e("---------->", "c size: " + c.getCount());
        Log.e("********", "Checking the cursor factory");
        if (c.getCount() != 0) {
            c.moveToFirst();
            do {
                headset = c.getInt(0);
                time_start = c.getString(1).trim();
                time_end = c.getString(2).trim();
                bluetooth = c.getString(3).trim();
                battery = c.getString(4).trim();
                user_wifi_state = c.getInt(5);
                Log.d("-------------->", "Time_start:  " + c.getString(0));
                Log.d("-------------->", "Time_End:  " + c.getString(1));
                Log.d("-------------->", "Bluetooth:  " + c.getString(2));
                Log.d("-------------->", "Battery:  " + c.getString(3));
                Log.d("-------------->", "Wifif state" + c.getString(4));

            } while (c.moveToNext());
        }
    }

    public boolean compare_time(int user_hour_start, int user_hour_end, int user_minute_start, int user_minute_end
            , int sys_hour_start, int sys_minute_start) {

        Log.d(tag, "user_hour_start: " + user_hour_start);
        Log.d(tag, "user_hour_end: " + user_hour_end);
        Log.d(tag, "user_minute_end: " + user_minute_end);
        Log.d(tag, "user_minute_start" + user_minute_start);
        if (user_hour_start == -1 && user_hour_end == -1) {
            return true; //no restricitons on time
        } else if (user_hour_start != -1 && user_hour_end != -1) { //time constaraints on both intervalas
            if (sys_hour_start * 60 + sys_minute_start >= user_hour_start * 60 + user_minute_start
                    && sys_hour_start * 60 + sys_minute_start <= user_hour_end * 60 + user_minute_end) {
                return true;
            }
            return false; //conditions not fullfiled
        } else if (user_hour_start != -1) { //conditions on the start time
            if (sys_hour_start * 60 + sys_minute_start >= user_hour_start * 60 + user_minute_start) {
                return true;
            }
            return true;
        } else if (user_hour_end != -1) { //conditions on the end time
            if (user_hour_end * 60 + user_minute_end >= sys_hour_start * 60 + sys_minute_start) {
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean compare_bluetooth(String user_bluetooth, String sys_bluetooth) {
        Log.d(tag, "System bluetooth: " + sys_bluetooth);
        Log.d(tag, "User bluettoh: " + user_bluetooth);
        if (!user_bluetooth.equals("null")) {
            if (user_bluetooth.equals(sys_bluetooth)) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean compare_wifi(int user_wifi_state, int sys_Wifi_Enabled) {
        int check_state = 0;
        if (user_wifi_state == 0) {
            return true;
        } //no preferances
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnectedOrConnecting()) {
            check_state = 7;
        } else if (!networkInfo.isConnected()) {
            check_state = 5;
        }

        if (user_wifi_state == check_state || user_wifi_state == sys_Wifi_Enabled) {
            return true;
        }

        return false;

    }

    public boolean compare_battery(int user_battery, int sys_battery) {
        Log.d(tag, "call form compare_battery: User Battery:" + user_battery);
        Log.d(tag, "call form compare_battery: system Battery:" + sys_battery);

        if (user_battery != -1) {
            if (sys_battery <= user_battery) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toastd.makeText(this, "Service start ho gayi dude !", //Toastd.LENGTH_SHORT).show();
//        Toast.makeText(this, "This is the call from the Main headset acion service", Toast.LENGTH_SHORT).show();
        Log.d("------------->", "Service is started");
        read_data();

        //Fetching the current statistics to match against the user requirements
        //Fetching the Date and the time
        DateFormat df = new SimpleDateFormat("HH:mm");
        String time = df.format(Calendar.getInstance().getTime());
        //Fetching the bluetooth status
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            bluetooth_state = "true";
        } else {
            bluetooth_state = "false";
        }
        //Fetching the Battery Status
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }


        //splitting the regext to obtain time constaraints
        Log.d(tag, "time_Start: " + time_start);
        Log.d(tag, "time_end: " + time_end);
        if (!time_start.equals("null")) {
            int index = time_start.indexOf(":");
            String string1 = time_start.substring(0, index);
            String string2 = time_start.substring(index + 1, time_start.length());
            user_hour_start = extractAlphaNumeric(string1);
            user_minute_start = extractAlphaNumeric(string2);
            Log.d(tag, "call from Regex functoin breaker workaround Strign1: " + string1);
            Log.d(tag, "call form Regex function breaker workaround String2: " + string2);
        } else if (time_start.equals("null")) {
            user_minute_start = user_hour_start = extractAlphaNumeric(time_start);
            Log.d(tag, "call from withing the regext : user_hour_start: " + user_hour_start);
            Log.d(tag, "call from withing the regext : user_minute_start: " + user_minute_start);
        }

        if (!time_end.equals("null")) {

            int index = time_end.indexOf(":");
            String string1 = time_end.substring(0, index);
            String string2 = time_end.substring(index + 1, time_end.length());
            user_hour_end = extractAlphaNumeric(string1);
            user_minute_end = extractAlphaNumeric(string2);
        } else if (time_end.equals("null")) {
            user_minute_end = user_hour_end = extractAlphaNumeric(time_end);
            Log.d(tag, "call from withing the regext : user_hour_end: " + user_hour_end);
            Log.d(tag, "call from withing the regext : user_minute_end: " + user_minute_end);
        }

        String sys_extract[] = time.split(":", 2);
        final int sys_hour_start = extractAlphaNumeric(sys_extract[0]);
        final int sys_minute_start = extractAlphaNumeric(sys_extract[1]);
        Log.d(tag, "-----------------------------------------------------------------------");
        final int battery_level = extractAlphaNumeric(battery); //system battery level
        final int incoming_user_battery = battery_level;
        Log.d(tag, "extracted battery level: " + battery_level);
        Log.d(tag, "-----------------------------------------------------------------------");

        Log.d(tag, "alphaNumeric converted values of the timed strings");
        Log.d(tag, "user_hour_start" + user_hour_start);
        Log.d(tag, "user_hour_end" + user_hour_end);
        Log.d(tag, "user_minute_start" + user_minute_start);
        Log.d(tag, "user_minute_end" + user_minute_end);
        Log.d(tag, "sys_hour_start" + sys_hour_start);
        Log.d(tag, "sys_minute_start" + sys_minute_start);


        if (user_hour_start > sys_hour_start) {
            String temp = "says that user_hour_start > sys_hour_start";
            Log.d(tag, "Viability test: " + temp);
        }

        Log.d(tag, "Result of Compare_time: " + compare_time(user_hour_start, user_hour_end, user_minute_start, user_minute_end
                , sys_hour_start, sys_minute_start));

        Log.d(tag, "User Battery Requirements: " + battery_level + " System battery Level" + batLevel);
        Log.d(tag, "User Bluetooth State: " + bluetooth + " System bluetooth state: " + bluetooth_state);


        int check1 = 0, check2 = 0;
        if (batLevel <= battery_level) {
            check1 = 1;
        }
        if (bluetooth_state.equals(bluetooth)) {
            check2 = 1;
        }
        Log.d(tag, "Check 1: " + check1);
        Log.d(tag, "Check 2: " + check2);
        Log.d(tag, "System state length: " + bluetooth_state.length() + "User bluetooth state length: " + bluetooth.length());
        //checking obtained values against the user configurations

        {
                    /*Perform the followinf fucntion else*/
            Log.d(tag, "Will execute the service methods Hang on");
            final WifiManager wifiManager = (WifiManager) Headset.this.getSystemService(Context.WIFI_SERVICE);
            // Need to wait a bit for the SSID to get picked up;
            // if done immediately all we'll get is null


            new Handler().postDelayed(new Runnable() {
                                          @Override
                                          public void run() {
                                              ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                              NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                                              WifiInfo info = wifiManager.getConnectionInfo();
                                              String mac = info.getMacAddress();
                                              String ssid = info.getSSID();
                                              Log.d(tag, "Checked for 60sec delayed period");
                                              Log.d(tag, "System wifi state " + wifiManager.WIFI_STATE_ENABLED);
                                              Log.d(tag, "System wifi state disabled" + wifiManager.WIFI_STATE_DISABLED);
                                              Log.d(tag, "comapre_battery: " + compare_battery(battery_level, batLevel));
                                              Log.d(tag, "battery LEel user supplied: " + incoming_user_battery);
                                              Log.d(tag, "compare Bluetooth:" + compare_bluetooth(bluetooth, bluetooth_state));
                                              Log.d(tag, "Comparet time: " + compare_time(user_hour_start, user_hour_end, user_minute_start, user_minute_end
                                                      , sys_hour_start, sys_minute_start));
                                              int sys_Wifi_Enabled = 3;
                                              int sys_Wifi_Disabled = 1;
                                              if (user_wifi_state == sys_Wifi_Enabled) {
                                                  Log.d(tag, "Supreme condtion met");
                                                  Log.d(tag, "user wifi status: " + user_wifi_state + " SYstem status: " + sys_Wifi_Enabled);
                                              } else {
                                                  Log.d(tag, "Supreme condition not met");
                                                  Log.d(tag, "user wifi status: " + user_wifi_state + " SYstem status: " + sys_Wifi_Enabled);
                                              }

                                             /* int check_state = 0;
                                              ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                              NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                                              if (networkInfo.isConnectedOrConnecting()) {
                                                  check_state = 7;
                                              } else if (!networkInfo.isConnected()) {
                                                  check_state = 5;
                                              }*/

                                              int headset_status = -1;
                                              AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                              if (audioManager.isWiredHeadsetOn()) {
                                                  headset_status = 1;
                                              } else {
                                                  headset_status = 0;
                                              }
                                              Log.d("----------->", "System Headset: Status " + audioManager.isWiredHeadsetOn());


                                              //---------------------------------Trigger Check------------------------------
                                              if (headset == headset_status
                                                      && compare_wifi(user_wifi_state, sys_Wifi_Enabled) &&
                                                      compare_time(user_hour_start, user_hour_end, user_minute_start, user_minute_end
                                                              , sys_hour_start, sys_minute_start)
                                                      && compare_bluetooth(bluetooth, bluetooth_state)
                                                      && compare_battery(incoming_user_battery, batLevel)) {
                                                  Log.d(tag, "After waiting 60sec perid The wifi Enabled trigger is fired ");/*
                                                  //Toastd.makeText(Headset.this, "Wifi is connected", //Toastd.LENGTH_SHORT).show();*/
                                                  createNotification(ssid, mac);
                                                 /* //Toastd.makeText(Headset.this, "You are connected to SSID: " + ssid + "Mac: " + mac, //Toastd.LENGTH_SHORT).show();
*/
                                                  //--------------------------------Perform actions--------------------------
                                                  //-------------- Analyzing user choices from the database
                                                  sd = openOrCreateDatabase("trigger3", Context.MODE_PRIVATE, null);
                                                  Cursor c = sd.rawQuery("select * from actions", null);
                                                  Log.e("---------->", "c size: " + c.getCount());
                                                  Log.e("********", "Checking the cursor factory");
                                                  if (c.getCount() != 0) {
                                                      c.moveToFirst();
                                                      do {
                                                          Log.d(tag, "Action wifi state: " + c.getString(0));
                                                          String temp = c.getString(0).toLowerCase();
                                                          if (!temp.equals("dnd")) {
                                                              if (temp.equals("on")) {/*Toggle the wifi on*/
                                                                  wifiManager.setWifiEnabled(true);
                                                              } else if (temp.equals("off")) {
                                                                  //turn off the wifi
                                                                  wifiManager.setWifiEnabled(false);
                                                              }
                                                          }


                                                          //Action 3
                                                          Log.d(tag, "Action 3 Play Music" + c.getString(2));
                                                          temp = c.getString(2).toLowerCase();
                                                          if (!temp.equals("dnd")) {

                                                              Intent samsung_music = getPackageManager().getLaunchIntentForPackage("com.sec.android.app.music");
                                                              if (samsung_music == null) {   //Checks for foodpanda installed or not
                                                                  Intent play_music = getPackageManager().getLaunchIntentForPackage("com.google.android.music");
                                                                  if (play_music == null) {
                                                                      Intent gaana = getPackageManager().getLaunchIntentForPackage("com.gaana");
                                                                      if (gaana == null) {  /*Rediercting to Playstore to install the application*/
                                                                          String response = "Oops ! Looks Like you dont have any Apps installed, But dont worry here you Go";
                                                                         /* //Toastd .makeText(Headset.this, response, //Toastd.LENGTH_SHORT).show();
                                                                         */
                                                                          startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.gaana")));
                                                                      } else {
                                                                          startActivity(gaana);
                                                                      }
                                                                  } else {
                                                                      startActivity(play_music);
                                                                  }
                                                              } else {
                                                                  startActivity(samsung_music);
                                                              } //Launch Samsung music

                                                          }

                                                          //Action 4
                                                          Log.d(tag, "Action 4 Bluetooth Staus: " + c.getString(3));
                                                          temp = c.getString(3).toLowerCase();
                                                          if (!temp.equals("dnd")) {
                                                              if (temp.equals("on")) {
                                                                  //turning on bluetooth
                                                                  BluetoothAdapter bluetoothAdpater = BluetoothAdapter.getDefaultAdapter();
                                                                  bluetoothAdapter.enable();
                                                              } else if (temp.equals("off")) {
                                                                  BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                                                  bluetoothAdapter.disable();
                                                              }
                                                          }

                                                          //Action 5
                                                          Log.d(tag, "Action 5: Timer: " + c.getString(4));
                                                          temp = c.getString(4);
                                                          if (!temp.equals("dnd")) {
                                                              //Settting the timer
                                                              int minutes = extractAlphaNumeric(temp);
                                                              Intent timer = new Intent(AlarmClock.ACTION_SET_TIMER);
                                                              timer.putExtra(AlarmClock.EXTRA_LENGTH, minutes);
                                                              timer.putExtra(AlarmClock.EXTRA_MESSAGE, "Times'up Boss");
                                                              timer.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                                                              startActivity(timer);
                                                              //Toastd.makeText(Headset.this, "Timer for " + minutes + " set Boss !! Brace yourself", //Toastd.LENGTH_SHORT).show();

                                                          }

                                                          //Action 6
                                                          Log.d(tag, "Action 6: ALarms: " + c.getString(5));
                                                          temp = c.getString(5);
                                                          if (!temp.equals("dnd")) {
                                                              int hour = extractAlphaNumeric(temp) / 60;
                                                              int minutes = extractAlphaNumeric(temp) % 60;
                                                              Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                                                              i.putExtra(AlarmClock.EXTRA_HOUR, hour);
                                                              i.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
                                                              i.putExtra(AlarmClock.EXTRA_MESSAGE, "Wake up Boss. Lets Rock !!! ");
                                                              i.putExtra(AlarmClock.EXTRA_VIBRATE, true);
                                                              i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                                                              //Toastd.makeText(Headset.this, "Alarm Set Boss  ....", //Toastd.LENGTH_SHORT).show();
                                                              if (hour == 24 || hour > 24) {
                                                                  //Toastd.makeText(Headset.this, "It seems the Hours are Incorrect", //Toastd.LENGTH_SHORT).show();
                                                              }
                                                              if (minutes > 60) {
                                                                  //Toastd.makeText(Headset.this, "Apparently Earth only considers 60 minutes !! ", //Toastd.LENGTH_SHORT).show();
                                                              }
                                                              startActivity(i);
                                                          }


                                                          //Action 2
                                                          Log.d(tag, "Action Sound Profile: " + c.getString(1));
                                                          temp = c.getString(1).toLowerCase();
                                                          if (!temp.equals("dnd")) {


                                                              NotificationManager notificationManager =
                                                                      (NotificationManager) Headset.this.getSystemService(Context.NOTIFICATION_SERVICE);

                                                              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                                                      && !notificationManager.isNotificationPolicyAccessGranted()) {

                                                                  Intent cintent = new Intent(
                                                                          android.provider.Settings
                                                                                  .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                                                                  startActivity(cintent);
                                                              }

                                                              if (temp.equals("vib")) {//Settingto vibration mode
                                                                  audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                                                  audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                                                                  audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                                              } else if (temp.equals("sil")) {
                                                                  audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                                                  audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                                                                  audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                                              } else if (temp.equals("max")) {
                                                                  audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                                                  int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                                                                  audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max, 0);
                                                                  audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                                                              }

                                                          }


                                                          Log.d("-------------->", "Time_start:  " + c.getString(0));
                                                          Log.d("-------------->", "Time_End:  " + c.getString(1));
                                                          Log.d("-------------->", "Bluetooth:  " + c.getString(2));
                                                          Log.d("-------------->", "Battery:  " + c.getString(3));
                                                          Log.d("-------------->", "Wifif state" + c.getString(4));

                                                      } while (c.moveToNext());
                                                  }


                                              }/* else if (user_wifi_state == sys_Wifi_Disabled
                                                      && compare_time(user_hour_start, user_hour_end, user_minute_start, user_minute_end
                                                      , sys_hour_start, sys_minute_start)
                                                      && compare_bluetooth(bluetooth, bluetooth_state)
                                                      && compare_battery(incoming_user_battery, batLevel)) {

                                                  Log.d(tag, "After waiting 60sec period the wifi disabled trigger is fired");
                                                  Log.d(tag, "Trigger 2 fired");
                                                  //Toastd.makeText(Headset.this, "Wifi is Disabled", //Toastd.LENGTH_SHORT).show();
                                                  createNotificationDisable("Wi-Fi is disabled");


                                                  sd = openOrCreateDatabase("trigger3", Context.MODE_PRIVATE, null);
                                                  Cursor c = sd.rawQuery("select * from actions", null);
                                                  Log.e("---------->", "c size: " + c.getCount());
                                                  Log.e("********", "Checking the cursor factory");
                                                  if (c.getCount() != 0) {
                                                      c.moveToFirst();
                                                      do {
                                                          Log.d(tag, "Action wifi state: " + c.getString(0));
                                                          String temp = c.getString(0).toLowerCase();
                                                          if (!temp.equals("dnd")) {
                                                              if (temp.equals("on")) {*//*Toggle the wifi on*//*
                                                                  wifiManager.setWifiEnabled(true);
                                                              } else if (temp.equals("off")) {
                                                                  //turn off the wifi
                                                                  wifiManager.setWifiEnabled(false);
                                                              }
                                                          }

                                                          //Action 2
                                                          Log.d(tag, "Action Sound Profile: " + c.getString(1));
                                                          temp = c.getString(1).toLowerCase();
                                                          if (!temp.equals("dnd")) {

                                                              NotificationManager notificationManager =
                                                                      (NotificationManager) Headset.this.getSystemService(Context.NOTIFICATION_SERVICE);

                                                              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                                                      && !notificationManager.isNotificationPolicyAccessGranted()) {

                                                                  Intent cintent = new Intent(
                                                                          android.provider.Settings
                                                                                  .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                                                                  startActivity(cintent);
                                                              }


                                                              if (temp.equals("vib")) {*//*Set max volume for the phone*//*} else if (temp.equals("vib")) {//Settingto vibration mode
                                                                  audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                                                  audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                                                                  audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                                              } else if (temp.equals("sil")) {
                                                                  audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                                                  audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                                                                  audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                                              } else if (temp.equals("max")) {
                                                                  audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                                                  int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                                                                  audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max, 0);
                                                                  audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                                                              }

                                                          }

                                                          //Action 3
                                                          Log.d(tag, "Action 3 Play Music" + c.getString(2));
                                                          temp = c.getString(2).toLowerCase();
                                                          if (!temp.equals("dnd")) {

                                                              Intent samsung_music = getPackageManager().getLaunchIntentForPackage("com.sec.android.app.music");
                                                              if (samsung_music == null) {   //Checks for foodpanda installed or not
                                                                  Intent play_music = getPackageManager().getLaunchIntentForPackage("com.google.android.music");
                                                                  if (play_music == null) {
                                                                      Intent gaana = getPackageManager().getLaunchIntentForPackage("com.gaana");
                                                                      if (gaana == null) {  *//*Rediercting to Playstore to install the application*//*
                                                                          String response = "Oops ! Looks Like you dont have any Apps installed, But dont worry here you Go";
                                                                          //Toastd.makeText(Headset.this, response, //Toastd.LENGTH_SHORT).show();
                                                                          startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.gaana")));
                                                                      } else {
                                                                          startActivity(gaana);
                                                                      }
                                                                  } else {
                                                                      startActivity(play_music);
                                                                  }
                                                              } else {
                                                                  startActivity(samsung_music);
                                                              } //Launch Samsung music

                                                          }

                                                          //Action 4
                                                          Log.d(tag, "Action 4 Bluetooth Staus: " + c.getString(3));
                                                          temp = c.getString(3).toLowerCase();
                                                          if (!temp.equals("dnd")) {
                                                              if (temp.equals("on")) {
                                                                  //turning on bluetooth
                                                                  BluetoothAdapter bluetoothAdpater = BluetoothAdapter.getDefaultAdapter();
                                                                  bluetoothAdapter.enable();
                                                              } else if (temp.equals("off")) {
                                                                  BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                                                  bluetoothAdapter.disable();
                                                              }
                                                          }

                                                          //Action 5
                                                          Log.d(tag, "Action 5: Timer: " + c.getString(4));
                                                          temp = c.getString(4);
                                                          if (!temp.equals("dnd")) {
                                                              //Settting the timer
                                                              int minutes = extractAlphaNumeric(temp);
                                                              Intent timer = new Intent(AlarmClock.ACTION_SET_TIMER);
                                                              timer.putExtra(AlarmClock.EXTRA_LENGTH, minutes);
                                                              timer.putExtra(AlarmClock.EXTRA_MESSAGE, "Times'up Boss");
                                                              timer.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                                                              startActivity(timer);
                                                              //Toastd.makeText(Headset.this, "Timer for " + minutes + " set Boss !! Brace yourself", //Toastd.LENGTH_SHORT).show();

                                                          }

                                                          //Action 6
                                                          Log.d(tag, "Action 6: ALarms: " + c.getString(5));
                                                          temp = c.getString(5);
                                                          if (!temp.equals("dnd")) {
                                                              int hour = extractAlphaNumeric(temp) / 60;
                                                              int minutes = extractAlphaNumeric(temp) % 60;
                                                              Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                                                              i.putExtra(AlarmClock.EXTRA_HOUR, hour);
                                                              i.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
                                                              i.putExtra(AlarmClock.EXTRA_MESSAGE, "Wake up Boss. Lets Rock !!! ");
                                                              i.putExtra(AlarmClock.EXTRA_VIBRATE, true);
                                                              i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                                                              //Toastd.makeText(Headset.this, "Alarm Set Boss  ....", //Toastd.LENGTH_SHORT).show();
                                                              if (hour == 24 || hour > 24) {
                                                                  //Toastd.makeText(Headset.this, "It seems the Hours are Incorrect", //Toastd.LENGTH_SHORT).show();
                                                              }
                                                              if (minutes > 60) {
                                                                  //Toastd.makeText(Headset.this, "Apparently Earth only considers 60 minutes !! ", //Toastd.LENGTH_SHORT).show();
                                                              }
                                                              startActivity(i);
                                                          }
                                                          Log.d("-------------->", "Time_start:  " + c.getString(0));
                                                          Log.d("-------------->", "Time_End:  " + c.getString(1));
                                                          Log.d("-------------->", "Bluetooth:  " + c.getString(2));
                                                          Log.d("-------------->", "Battery:  " + c.getString(3));
                                                          Log.d("-------------->", "Wifif state" + c.getString(4));

                                                      } while (c.moveToNext());
                                                  }


                                              }
*/                                          }
                                      }
                    , 100);

        }
        return START_NOT_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void createNotification(String ssid, String mac) {
        Notification n = new NotificationCompat.Builder(this)
                .setContentTitle("Wifi Connection")
                .setContentText("Connected to " + ssid)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("You're connected to " + ssid + " at " + mac))
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(0, n);
    }

    private void createNotificationDisable(String message) {
        Notification n = new NotificationCompat.Builder(this)
                .setContentTitle("Wifi Connection Disabled")
                .setContentText("message")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("You're Disconnected"))
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(0, n);
    }
}
