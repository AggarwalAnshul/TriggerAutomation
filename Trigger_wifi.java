package com.apkglobal.keeden;

import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Trigger_wifi extends AppCompatActivity {

    protected BottomNavigationView navigationView;
    SQLiteDatabase sd;
    int wifi_state = 0;

    private void createdatabase() {
        sd = openOrCreateDatabase("trigger2", Context.MODE_PRIVATE, null);
        sd.execSQL("create table if not exists conditions(time_start varchar(5), time_end varchar(5) , bluetooth varchar(5) , battery varchar(2) , wifi integer );");
    }

    private void createActions() {
        sd = openOrCreateDatabase("trigger2", Context.MODE_PRIVATE, null);
        sd.execSQL("create table if not exists actions(wifi varchar(3), sound_profile varchar(3)" +
                ", play_music varchar(3), bluetooth varchar(3), timer varchar(5)" +
                " ,alarm varchar(5) );");
    }

  /*  @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_trigger_wifi;
    }

*/
/*
    public void showNumberPicker()
    {

        final Dialog dialog = new Dialog(Trigger_wifi.this);
        dialog.setTitle("NumberPicker");
        dialog.setContentView(R.layout.dialog);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(100); // max value 100
        np.setMinValue(0);   // min value 0
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        et_battery.setText(String.valueOf(np.getValue())); //set the value to textview
        dialog.show();


    }*/


    /* For action bar options menu*/
   /* @Override
    int getContentViewId() {
        return R.layout.activity_trigger_wifi;
    }

    private void updateNavigationBarState() {
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    void selectBottomNavigationBarItem(int itemId) {
        Menu menu = navigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            boolean shouldBeChecked = item.getItemId() == itemId;
            if (shouldBeChecked) {
                item.setChecked(true);
                break;
            }
        }
    }*/

    /*  @Override
      public boolean onCreateOptionsMenu(Menu menu) {
          // Inflate the menu; this adds items to the action bar if it is present.
          getMenuInflater().inflate(R.menu.options_menu, menu);
          return true;
      }
  */
    /*@Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_home) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (itemId == R.id.navigation_translate) {
            startActivity(new Intent(this, Translate.class));
        } else if (itemId == R.id.navigation_notes) {
            startActivity(new Intent(this, ListDesign.class));
        } else if (itemId == R.id.navigation_trigger_wifi) {
            startActivity(new Intent(this, Trigger_Fi.class));
        } else if (itemId == R.id.navigation_trigger_time) {
            startActivity(new Intent(this, TimedActivity.class));
        }
        finish();
        return true;
    }
*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sd.close();
    }

    /*  @Override
          public boolean onOptionsItemSelected(MenuItem item) {
              switch (item.getItemId())
              {
                  case R.id.option_about:
                      startActivity(new Intent(this, About.class));
                      break;
                  case R.id.option_adaptive:
                      startActivity(new Intent(this, adapt.class));
                  case R.id.option_features:
                      startActivity(new Intent(this, Features.class));
                  case R.id.option_share:
                      Intent share = new Intent(Intent.ACTION_SEND);
                      share.putExtra(Intent.EXTRA_TEXT, "Hey I'm Using this new App Samsara. Check out Guys, Its so Cool !" + "https://play.google.com/store/apps/details?id=com.apkglobal.alice&hl=en");
                      share.setType("text/plain");
                      startActivity(Intent.createChooser(share, "Share App via"));

                  case R.id.options_feedback:
                      Intent email = new Intent(Intent.ACTION_SEND);
                      email.putExtra(Intent.EXTRA_EMAIL, new String[]{"anshul.aggarwal.sfd@gnail.com"});
                      email.setType("email/rfc822");
                      startActivity(Intent.createChooser(email, "Send Mail Via"));
              }
              return super.onOptionsItemSelected(item);
          }
      */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trigger_wifi);/*
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        getSupportActionBar().setTitle("Wi-Fi Trigger");
//        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
//        navigationView.setOnNavigationItemSelectedListener(this);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Registering the reciever*/
            /*Placing values into the database*//*
        final BroadcastReceiver broadcastReceiver = new MyService();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WIFI_STATE_CHANGED_ACTION);
        Trigger_wifi.this.registerReceiver(broadcastReceiver, intentFilter);
*/

        createdatabase();
        Button btn = (Button) findViewById(R.id.btn);
        Button btn_stop = (Button) findViewById(R.id.btn_stop);
        final EditText et_battery = (EditText) findViewById(R.id.et_battery);
        et_battery.setText("No Preference");
        final EditText et_time_start = (EditText) findViewById(R.id.et_time_start);
        et_time_start.setText("No Preference");
        final EditText et_time_end = (EditText) findViewById(R.id.et_time_end);
        et_time_end.setText("No Preference");
        final EditText et_bluetooth = (EditText) findViewById(R.id.et_bluetooth);
        et_bluetooth.setText("No Preference");



        /*Castnig popups for options */
        et_battery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final NumberPicker picker = new NumberPicker(Trigger_wifi.this);
                picker.setMinValue(1);
                picker.setMaxValue(100);

                final FrameLayout layout = new FrameLayout(Trigger_wifi.this);
                layout.addView(picker, new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER));

                new AlertDialog.Builder(Trigger_wifi.this)
                        .setView(layout)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.e("------------>", "value: " + picker.getValue());

                                {
                                }
                                et_battery.setText("" + picker.getValue());
                                // / do something with picker.getValue()
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });

        et_time_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Trigger_wifi.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        et_time_start.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        et_time_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Trigger_wifi.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        et_time_end.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });


        /*For bluetooth yes no*/
        final CharSequence options[] = new CharSequence[]{"On", "Off"};
        et_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Trigger_wifi.this
                );
                builder.setTitle("Pick Bluetooth state");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0)
                            et_bluetooth.setText("true");
                        else
                            et_bluetooth.setText("false");
                    }
                });
                builder.show();


            }
        });

        final EditText et_wifi = (EditText) findViewById(R.id.et_wifi);
        et_wifi.setText("No Preference");
        final CharSequence wifi_options[] = new CharSequence[]{"On", "Off", "Disconnected", "Connected"};
        et_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Trigger_wifi.this
                );
                builder.setTitle("Pick Wifi state");
                builder.setItems(wifi_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            et_wifi.setText("On");
                            wifi_state = 3;
                        } else if (which == 1) {
                            et_wifi.setText("Off");
                            wifi_state = 1;
                        } else if (which == 2) {
                            et_wifi.setText("Disconnected");
                            wifi_state = 5;
                        } else if (which == 3) {
                            wifi_state = 7;
                            et_wifi.setText("Connected");
                        }

                    }

                });
                builder.show();
            }
        });

       /*Configuring Actions here*/
       /*1. Wifi*/
        final EditText et_action_wifi = (EditText) findViewById(R.id.et_action_wifi);
        et_action_wifi.setText("Set wifi mode");
        final CharSequence action_wifi_options[] = new CharSequence[]{"On", "Off", "DND"};
        et_action_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Trigger_wifi.this
                );
                builder.setTitle("Pick Wifi state");
                builder.setItems(action_wifi_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                et_action_wifi.setText("On");
                                break;
                            case 1:
                                et_action_wifi.setText("Off");
                                break;
                            case 2:
                                et_action_wifi.setText("DND");
                                break;
                        }
                    }

                });
                builder.show();
            }
        });


        /*2. SOund Profiles*/
        final EditText et_action_soundProfile = (EditText) findViewById(R.id.et_action_soundProfile);
        final CharSequence action_soundProfile_options[] = new CharSequence[]{"Max", "Vib", "Sil", "DND"};
        et_action_soundProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Trigger_wifi.this
                );
                builder.setTitle("Pick a Sound state");
                builder.setItems(action_soundProfile_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                et_action_soundProfile.setText("Max");
                                break;
                            case 1:
                                et_action_soundProfile.setText("Vib");
                                break;
                            case 2:
                                et_action_soundProfile.setText("Sil");
                                break;
                            case 3:
                                et_action_soundProfile.setText("DND");
                                break;
                        }
                    }

                });
                builder.show();
            }
        });


                /*3. Play Music*/
        final EditText et_action_playMusic = (EditText) findViewById(R.id.et_action_playMusic);
        final CharSequence action_playMusic_options[] = new CharSequence[]{"On", "Off", "DND"};
        et_action_playMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Trigger_wifi.this
                );
                builder.setTitle("Pick a Musci option ");
                builder.setItems(action_playMusic_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                et_action_playMusic.setText("On");
                                break;
                            case 1:
                                et_action_playMusic.setText("Off");
                                break;
                            case 2:
                                et_action_playMusic.setText("DND");
                                break;
                        }
                    }

                });
                builder.show();
            }
        });


                        /*4.  Bluetooth*/
        final EditText et_action_bluetooth = (EditText) findViewById(R.id.et_action_bluetooth);
        final CharSequence action_bluetooth_options[] = new CharSequence[]{"On", "Off", "DND"};
        et_action_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Trigger_wifi.this
                );
                builder.setTitle("Pick a Bluetooth State ");
                builder.setItems(action_bluetooth_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                et_action_bluetooth.setText("On");
                                break;
                            case 1:
                                et_action_bluetooth.setText("Off");
                                break;
                            case 2:
                                et_action_bluetooth.setText("DND");
                                break;
                        }
                    }

                });
                builder.show();
            }
        });

        final CharSequence action_timer_options[] = new CharSequence[]{"Off", "SET"};
        final EditText et_action_timer = (EditText) findViewById(R.id.et_action_timer);
        et_action_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Displaying the dialog first
                AlertDialog.Builder builder = new AlertDialog.Builder(Trigger_wifi.this
                );
                builder.setTitle("Pick a Tmer State ");
                builder.setItems(action_timer_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            et_action_timer.setText("dnd");
                        } else {
                            //number picker eneterd here
                            final NumberPicker picker = new NumberPicker(Trigger_wifi.this);
                            picker.setMinValue(1);
                            picker.setMaxValue(1000);

                            final FrameLayout layout = new FrameLayout(Trigger_wifi.this);
                            layout.addView(picker, new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.WRAP_CONTENT,
                                    FrameLayout.LayoutParams.WRAP_CONTENT,
                                    Gravity.CENTER));

                            new AlertDialog.Builder(Trigger_wifi.this)
                                    .setView(layout)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Log.e("------------>", "value: " + picker.getValue());

                                            {
                                            }
                                            et_action_timer.setText("" + picker.getValue());
                                            // / do something with picker.getValue()
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, null)
                                    .show();


                        }
                    }

                });
                builder.show();

            }
        });


        final EditText et_action_alarm = (EditText) findViewById(R.id.et_action_alarm);
        final CharSequence et_alarm_options[] = new CharSequence[]{"Off", "SET"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Trigger_wifi.this);
        builder.setTitle("Select an alarm State ");
        et_action_alarm.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {


                //showing the alert builder here
                AlertDialog.Builder builder = new AlertDialog.Builder(Trigger_wifi.this);
                builder.setTitle("Pick an Alarm set State");
                builder.setItems(et_alarm_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            et_action_alarm.setText("dnd");
                        } else {
                            //inflating the time picker here

                            Calendar mcurrentTime = Calendar.getInstance();
                            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                            int minute = mcurrentTime.get(Calendar.MINUTE);
                            TimePickerDialog mTimePicker;
                            mTimePicker = new TimePickerDialog(Trigger_wifi.this, new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                    et_action_alarm.setText(selectedHour + ":" + selectedMinute);
                                }
                            }, hour, minute, true);//Yes 24 hour time
                            mTimePicker.setTitle("Select Time");
                            mTimePicker.show();

                            /**/
                        }
                    }

                });
                builder.show();

            }
        });


                    /*Placing action values into the database*/
        Button btn_set_action = (Button) findViewById(R.id.btn_set_actions);
        btn_set_action.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                createActions(); //action tables created
                sd = openOrCreateDatabase("trigger2", Context.MODE_PRIVATE, null);
                String tableName = "actions";
                Cursor cursor = sd.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        String insert_data = "insert into actions values('" + et_action_wifi.getText() + "','" + et_action_soundProfile.getText() + "','" + et_action_playMusic.getText()
                                + "','" + et_action_bluetooth.getText() + "','" + et_action_timer.getText() + "', '" + et_action_alarm.getText() + "')";
                        sd.execSQL(insert_data);

                        Toast.makeText(Trigger_wifi.this, "Actions inserted", Toast.LENGTH_SHORT).show();

                        cursor.close();
                    }
                    cursor.close();
                } else {
                    Toast.makeText(Trigger_wifi.this, "No Actions exists !", Toast.LENGTH_SHORT).show();
                }
            }

        });


        //Deleting the actoin
        Button btn_action_delete = (Button) findViewById(R.id.btn_delete_actions);
        btn_action_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sd = openOrCreateDatabase("trigger2", Context.MODE_PRIVATE, null);
                String tableName = "actions";
                Cursor cursor = sd.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        cursor.close();
                        String query = "drop table actions;";
                        sd.execSQL(query);
                        Toast.makeText(Trigger_wifi.this, "actions Deleted...", Toast.LENGTH_SHORT).show();
                    }
                    cursor.close();
                } else {
                    Toast.makeText(Trigger_wifi.this, "No Actions exists !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Displaying the actinos
        Button btn_display_action = (Button) findViewById(R.id.btn_display_actions);
        btn_display_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Displaying the actions
                sd = openOrCreateDatabase("trigger2", Context.MODE_PRIVATE, null);

                Cursor c = sd.rawQuery("select * from actions", null);
                Log.e("---------->", "c size: " + c.getCount());
                Log.e("********", "Checking the cursor factory");
                if (c.getCount() != 0)

                {
                    c.moveToFirst();
                    do {
                        Log.d("-------------->", "Wifi State:  " + c.getString(0));
                        Log.d("-------------->", "Sound Profiles:  " + c.getString(1));
                        Log.d("-------------->", "Play Music:  " + c.getString(2));
                        Log.d("-------------->", "Bluetooth Status:  " + c.getString(3));
                        Log.d("-------------->", "Timers: " + c.getString(4));
                        Log.d("-------------->", "Alarms: " + c.getString(5));

                    } while (c.moveToNext());
            /*Data fed to the arraylist note*/
                }

                if (c.getCount() == 0)

                {
                                /*No data present*/
                    Log.d("------------>", "Database is empty");
                }

        /**/


            }
        });
            /**/


        Button btn_empty = (Button) findViewById(R.id.btn_empty);
        btn_empty.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {


                sd = openOrCreateDatabase("trigger2", Context.MODE_PRIVATE, null);
                String tableName = "conditions";
                Cursor cursor = sd.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        cursor.close();
                        String query = "drop table conditions;";
                        sd.execSQL(query);
                        Toast.makeText(Trigger_wifi.this, "Conditions Deleted...", Toast.LENGTH_SHORT).show();
                    }
                    cursor.close();
                } else {
                    Toast.makeText(Trigger_wifi.this, "No Conditions exists !", Toast.LENGTH_SHORT).show();
                }


               /* sd.delete("conditions", null, null);
                Cursor c = sd.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
                do
                {
                    sd.delete(c.getString(0),null,null);
                }while (c.moveToNext());
*/
            }
        });


        Button btn_insert = (Button) findViewById(R.id.btn_insert);
        btn_insert.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {


                et_wifi.getText().toString();
                String np = "No Preference";
                String insert_et_time_start = (!et_time_start.getText().toString().equals("No Preference") ? et_time_start.getText().toString() : null);
                String insert_et_time_end = (!et_time_end.getText().toString().equals("No Preference") ? et_time_end.getText().toString() : null);
                String insert_et_battery = (!et_battery.getText().toString().equals("No Preference") ? et_battery.getText().toString() : null);
                String insert_et_bluetooth = (!et_bluetooth.getText().toString().equals("No Preference") ? et_bluetooth.getText().toString() : null);
                int insert_wifi = 0;
                if (et_wifi.getText().toString().equals("No Preference")) {
                    insert_wifi = 0;
                } else if (et_wifi.getText().toString().equals("On")) {
                    insert_wifi = 3;
                } else if (et_wifi.getText().toString().equals("Off")) {
                    insert_wifi = 1;
                } else if (et_wifi.getText().toString().equalsIgnoreCase("Connected")) {
                    insert_wifi = 7;
                } else if (et_wifi.getText().toString().equalsIgnoreCase("Disconnected")) {
                    insert_wifi = 5;
                }

                /*createdatabase();*/
                createdatabase();
                sd = openOrCreateDatabase("trigger2", Context.MODE_PRIVATE, null);
                String tableName = "conditions";
                Cursor cursor = sd.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        String insert_data = "insert into conditions values('" + insert_et_time_start + "','" + insert_et_time_end + "','" + insert_et_bluetooth + "','" + insert_et_battery + "','" + insert_wifi + "')";
                        sd.execSQL(insert_data);
                        Toast.makeText(Trigger_wifi.this, "Conditions set", Toast.LENGTH_SHORT).show();

                        cursor.close();
                    }
                    cursor.close();
                } else {
                    Toast.makeText(Trigger_wifi.this, "No Actions exists !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button wifi_btn_display = (Button) findViewById(R.id.wifi_btn_display);
        wifi_btn_display.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Log.d("---->", "Request Intercepted");
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
/*
                if (networkInfo.isConnectedOrConnecting()) {
                    Log.e("------------------>", "This is the wifi connected or connectin zone + 7");
                } if (!networkInfo.isConnected()) {
                    Log.e("---------------------->", "This si the wifi not connected / disconnected zone + 5");
                }*/

                sd = openOrCreateDatabase("trigger2", Context.MODE_PRIVATE, null);
                TextView wifi_tv_display = (TextView) findViewById(R.id.wifi_tv_display);
                TextView wifi_tv_display_action = (TextView) findViewById(R.id.wifi_tv_display_action);

               /*
              */
                String tableName = "conditions";
                Cursor cursor = sd.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
                if (cursor != null) {
                    if (cursor.getCount() > 0) {

                        Cursor c = sd.rawQuery("select * from conditions", null);
                        Log.d("---------->", "c size: " + c.getCount());
                        Log.d("********", "Checking the cursor factory");
                        if (c.getCount() != 0) {
                            c.moveToFirst();
                            do {

                                int id = 0;
                                String temp = wifi_tv_display.getText().toString();
                                wifi_tv_display.setText(temp + "\n" + "Trigger Id: " + id++ + "\n"
                                        + "----- conditions -----" + "\n"
                                        + "Time_start:  " + c.getString(0) + "\n"
                                        + "Time_end:  " + c.getString(1) + "\n"
                                        + "Bluetooth:  " + c.getString(2) + "\n"
                                        + "Battery:  " + c.getString(3) + "\n"
                                        + "Wi-Fi state: " + c.getString(4) + "\n");

                                Log.d("-------------->", "Time_start:  " + c.getString(0));
                                Log.d("-------------->", "Time_End:  " + c.getString(1));
                                Log.d("-------------->", "Bluetooth:  " + c.getString(2));
                                Log.d("-------------->", "Battery:  " + c.getString(3));
                                Log.d("-------------->", "WiFI state: " + c.getString(4));

                            } while (c.moveToNext());
            /*Data fed to the arraylist note*/
                        }

                        if (c.getCount() == 0) {
                                /*No data present*/
                            Log.d("------------>", "Database is empty");
                            wifi_tv_display.setText("No Triggers Set");

                        }


                        cursor.close();
                    }
                    cursor.close();
                } else {
                    Toast.makeText(Trigger_wifi.this, "No Conditions exists !", Toast.LENGTH_SHORT).show();
                }


                  /*tv_display.setText("\n" + "----- Displaying configured acitons -----");*/
                 sd = openOrCreateDatabase("trigger2", Context.MODE_PRIVATE, null);
                 tableName = "actions";
                 cursor = sd.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
                 if (cursor != null) {
                    if (cursor.getCount() > 0) {


                        Cursor c = sd.rawQuery("select * from actions", null);
                        Log.e("---------->", "c size: " + c.getCount());
                        Log.e("********", "Checking the cursor factory");
                        wifi_tv_display_action.setText("--------- Trigger Actions ---------" + "\n");
                        if (c.getCount() != 0)

                        {
                            c.moveToFirst();
                            do {
                                int action_id = 0;
                                String temp = wifi_tv_display_action.getText().toString();
                                wifi_tv_display_action.setText(temp + "\n" + "Action_trigger: " + action_id++ + "\n" +
                                        "Wifi State:  " + c.getString(0) + "\n"
                                        + "Sound Profiles:  " + c.getString(1) + "\n"
                                        + "Play Music:  " + c.getString(2) + "\n"
                                        + "Bluetooth Status:  " + c.getString(3) + "\n"
                                        + "Timers: " + c.getString(4) + "\n"
                                        + "Alarms: " + c.getString(5) + "\n");


                                Log.d("-------------->", "Wifi State:  " + c.getString(0));
                                Log.d("-------------->", "Sound Profiles:  " + c.getString(1));
                                Log.d("-------------->", "Play Music:  " + c.getString(2));
                                Log.d("-------------->", "Bluetooth Status:  " + c.getString(3));
                                Log.d("-------------->", "Timers: " + c.getString(4));
                                Log.d("-------------->", "Alarms: " + c.getString(5));

                            } while (c.moveToNext());
           /* Data fed to the arraylist note
          */
                        }

                        if (c.getCount() == 0)

                        {
                              /*  No data present*/
                            //tv_display.setText("No Triggers set");
                            Log.d("------------>", "Database is empty");
                            wifi_tv_display_action.setText("No Trigger actions defined");
                        }

                        cursor.close();
                    }
                    cursor.close();
                } else {
                    Toast.makeText(Trigger_wifi.this, "No Conditions exists !", Toast.LENGTH_SHORT).show();
                }
/*

                Cursor c = sd.rawQuery("select * from conditions", null);
                Log.d("---------->", "c size: " + c.getCount());
                Log.d("********", "Checking the cursor factory");
                if (c.getCount() != 0) {
                    c.moveToFirst();
                    do {

                        int id = 0;
                        String temp = wifi_tv_display.getText().toString();
                        wifi_tv_display.setText(temp + "\n" + "Trigger Id: " + id++ + "\n"
                                + "----- conditions -----" + "\n"
                                + "Time_start:  " + c.getString(0) + "\n"
                                + "Time_end:  " + c.getString(1) + "\n"
                                + "Bluetooth:  " + c.getString(2) + "\n"
                                + "Battery:  " + c.getString(3) + "\n"
                                + "Wi-Fi state: " + c.getString(4) + "\n");

                        Log.d("-------------->", "Time_start:  " + c.getString(0));
                        Log.d("-------------->", "Time_End:  " + c.getString(1));
                        Log.d("-------------->", "Bluetooth:  " + c.getString(2));
                        Log.d("-------------->", "Battery:  " + c.getString(3));
                        Log.d("-------------->", "WiFI state: " + c.getString(4));

                    } while (c.moveToNext());
            */
/*Data fed to the arraylist note*//*

                }

                if (c.getCount() == 0) {
                                */
/*No data present*//*

                    Log.d("------------>", "Database is empty");
                    wifi_tv_display.setText("No Triggers Set");

                }
*/

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ComponentName component = new ComponentName(Trigger_wifi.this, MyService.class);
                int status = Trigger_wifi.this.getPackageManager().getComponentEnabledSetting(component);
                if (status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                    Log.e("---------->", "receiver is enabled");
                } else if (status == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                    Log.e("------------>", "receiver is disabled");
                }
                Trigger_wifi.this.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                Toast.makeText(Trigger_wifi.this, "Trigger Activated", Toast.LENGTH_SHORT).show();

                /*BroadcastReceiver broadcastReceiver = new MyService();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
                intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
                registerReceiver(broadcastReceiver, intentFilter);*/
                Log.e("----->", "Registerd  the reciever");

            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ComponentName component = new ComponentName(Trigger_wifi.this, MyService.class);
                int status = Trigger_wifi.this.getPackageManager().getComponentEnabledSetting(component);
                if (status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                    Log.e("------------>", "receiver is enabled");
                } else if (status == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                    Log.e("----------------->", "receiver is disabled");
                }

                //Disable
                Trigger_wifi.this.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
/*
//Enable
                context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP);
*/

/*
                BroadcastReceiver broadcastReceiver = new MyService();
                unregisterReceiver(broadcastReceiver);
*/
            }
        });
      /*  btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(WIFI_STATE_CHANGED_ACTION);
                Trigger_wifi.this.registerReceiver(broadcastReceiver, intentFilter);
            }
        });*/


/*
        btn_stop.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                Trigger_wifi.this.unregisterReceiver(broadcastReceiver);
            }
        });*/


    }

}
