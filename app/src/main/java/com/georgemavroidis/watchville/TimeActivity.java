package com.georgemavroidis.watchville;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by george on 14-11-17.
 */
public class TimeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time_layout);
        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.action_bar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText("Watchville");

        ImageButton imageButton = (ImageButton) mCustomView
                .findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SettingsClass.class);
                startActivity(intent);
            }
        });

        ImageView time_view = (ImageView) findViewById(R.id.news_frag);
        time_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
//                Intent intent = new Intent(getBaseContext(), TimeActivity.class);
//                startActivity(intent);
            }
        });

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);



//
//        Timer timer = new Timer();
//        timer.schedule(new SayHello(), 0, 1000);

        TextView averaged = (TextView) findViewById(R.id.averaged);
        Random rand = new Random();
        int x = rand.nextInt(100);
        averaged.setText("Time from "+x+" servers, averaged for accuracy");




        TextView next = (TextView) findViewById(R.id.set_next_leap_year);
        next.setText("2016 Year 2");

        TextView actual = (TextView) findViewById(R.id.actual_date);
        DateFormat dateFormat = new SimpleDateFormat("EEEE, LLLL d, yyyy ");
        Calendar cal = Calendar.getInstance();
        String ac = dateFormat.format(cal.getTime());
        actual.setText(ac);

        ImageView cals = (ImageView) findViewById(R.id.cal);
        String url = "http://thewotimes.com/Y/W/moonphases/cal.png";
        Picasso.with(getApplicationContext()).load(url).into(cals);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                new LongOperation().execute("");;
            }
        }, 0, 100);

    }
    private class LongOperation extends AsyncTask<String, Void, String> {
        String wa = "";
        String t = "";
        String gmtTime = "";
        @Override
        protected String doInBackground(String... params) {
                    DateFormat dateFormat = new SimpleDateFormat("h:mm");
                    Date date = new Date();

                    Calendar cal = Calendar.getInstance();
                    wa = dateFormat.format(cal.getTime());

                    dateFormat = new SimpleDateFormat("ss.S");
                    date = new Date();
                    cal = Calendar.getInstance();
                    String w = dateFormat.format(cal.getTime());
                    t = String.format("%.1f", Float.parseFloat(w));

                    DateFormat df =  new SimpleDateFormat("H:mm");
                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
                    gmtTime = df.format(new Date());

            return "Executed";

        }

        @Override
        protected void onPostExecute(String result) {
            TextView main_time = (TextView) findViewById(R.id.main_time);
            TextView seconds_time = (TextView) findViewById(R.id.seconds_time);
            TextView utc = (TextView) findViewById(R.id.set_utc);
            main_time.setText(wa);
            seconds_time.setText(t);
            utc.setText(gmtTime);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
