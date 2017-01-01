package com.example.cucko.compilekernelnow;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_CODE = 1;
    InputStream in;
    BufferedReader reader;
    String line;
    TextView text;
    TextView status;
    String out = "";
    boolean toCompile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        SharedPreferences prefs = getSharedPreferences("settings", 0);
//        long lastCompile = prefs.getLong("lastCompile", 0);
//        Log.d("lastCompile", DateUtils.formatDateTime(this, lastCompile, DateUtils.FORMAT_ABBREV_RELATIVE | DateUtils.FORMAT_SHOW_TIME));
//        Log.d("lastCompile", ""+DateUtils.getRelativeDateTimeString(this, lastCompile, DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0));
//        Log.d("lastCompile", "" + DateUtils.getRelativeTimeSpanString(lastCompile));

        text = (TextView) findViewById(R.id.tvOut);
        status = (TextView) findViewById(R.id.tvStatus);
        text.setMovementMethod(new ScrollingMovementMethod());
        setAlarm();

        if (getIntent() != null && "1" .equals(getIntent().getStringExtra("notification"))) {
            text.post(new Runnable() {
                @Override
                public void run() {
                    onCompile(null);
                }
            });
        }
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        if ("1" .equals(intent.getStringExtra("notification")))
//            onCompile(null);
//        super.onNewIntent(intent);
//    }

    public void readLine() {
        try {
            line = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (line != null) {
            out += "\n" + line;
            text.setText(out);
            final int scrollAmount = text.getLayout().getLineTop(text.getLineCount()) - text.getHeight();
            // if there is no need to scroll, scrollAmount will be <=0
            if (scrollAmount > 0)
                text.scrollTo(0, scrollAmount);
            else
                text.scrollTo(0, 0);

            text.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    text.scrollTo(0, Integer.MAX_VALUE);
                    readLine();
                }
            }, new Random().nextInt(2) * 4);
        } else {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            status.setVisibility(View.VISIBLE);
            text.setVisibility(View.GONE);
            NotificationManagerCompat.from(this)
                    .cancel(AlarmReceiver.NOTIFICATION_ID);

            SharedPreferences prefs = getSharedPreferences("settings", 0);
            SharedPreferences.Editor edit = prefs.edit();
            Calendar cal = Calendar.getInstance();
            edit.putLong("lastCompile", cal.getTimeInMillis());
            edit.apply();

            ((Button) findViewById(R.id.btnCompile)).setText("Compile Again");
        }

    }

    public void onCompile(View view) {
        try {
            text.setText("");
            out = "";
            ((Button) findViewById(R.id.btnCompile)).setText("Compiling ...");
            status.setVisibility(View.GONE);
            text.setVisibility(View.VISIBLE);
            in = this.getAssets().open("kernel" + (new Random().nextInt(5) + 1) + ".txt");
            reader = new BufferedReader(new InputStreamReader(in));
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line != null)
            out += line;

        text.setText(out);

        readLine();
    }

    private void setAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 500, pendingIntent);
    }
}
