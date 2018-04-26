package com.apkglobal.keeden;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.apkglobal.keeden.about.About;

public class Triggers extends BaseActivity {


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_home) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (itemId == R.id.navigation_translate) {
            startActivity(new Intent(this, Translate.class));
        } else if (itemId == R.id.navigation_notes) {
            startActivity(new Intent(this, ListDesign.class));
        } else if (itemId == R.id.navigation_texts) {
            startActivity(new Intent(this, Send_Text.class));
        } else if (itemId == R.id.navigation_triggers) {
            startActivity(new Intent(this, Triggers.class));
        }
        finish();
        return true;
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_triggers;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_triggers;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triggers);
        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);


//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Triggers");

        Button btn_wifi = (Button) findViewById(R.id.trigger_wifi);
        Button btn_time = (Button) findViewById(R.id.trigger_time);
        Button btn_headset = (Button) findViewById(R.id.trigger_headset);

        btn_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Triggers.this, Trigger_wifi.class));
            }
        });

        btn_headset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Triggers.this, Trigger_headset.class));
            }
        });

        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Triggers.this, TimedActivity.class));
            }
        });
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
    }

}
