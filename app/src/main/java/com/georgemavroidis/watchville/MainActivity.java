package com.georgemavroidis.watchville;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener{
    ListView listView;
    WatchAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    JSONArray items = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
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

        ImageView time_view = (ImageView) findViewById(R.id.time_frag);
        time_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), TimeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);

            }
        });
        Tracker t = getTracker(TrackerName.APP_TRACKER);

        // Set screen name.
        t.setScreenName("Watchville - Android");

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(android.R.color.black,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        String tem = readFromFile();

        if(tem != ""){
            try {
                items = new JSONArray(tem);
                setAdapter();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            new GetContacts().execute();

        }

    }

    public void onRefresh() {

        new GetContacts().execute();

    }
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker("Watchville")
                    :analytics.newTracker(R.xml.global_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
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

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        private GetContacts(){

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();


            String posts = sh.makeServiceCall("http://thewotimes.com/Y/W/sorted.txt", ServiceHandler.GET);


            if (posts != null) {
                try {
                    String filename = "posts.txt";
                    String string = posts;
                    FileOutputStream outputStream;

                    try {
                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(string.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    JSONArray t = new JSONArray(posts);

                    items = t;


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                try {
                    items = new JSONArray(readFromFile());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            setAdapter();



        }


    }

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("posts.txt");


            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
    public void setAdapter(){

        ArrayList<WatchCell> arrayOfUsers = new ArrayList<WatchCell>();
        listView = (ListView)findViewById(R.id.mainListView);
        adapter = new WatchAdapter(getBaseContext(), arrayOfUsers);
        listView.setAdapter(adapter);
        listView.setClickable(true);

        Log.d("here","here");
        populateAdapter();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text or do whatever you need.
                JSONObject temp_items = null;
                try {
                    temp_items = items.getJSONObject(position);
                    String title = temp_items.getString("title");
                    String source = temp_items.getString("source");
                    String link = temp_items.getString("link");
                    String time = temp_items.getString("pubdate");
                    String main = temp_items.getString("main");
                    String content = temp_items.getString("content");

                    Intent intent = new Intent(getApplicationContext(), ContentView.class);
                    Bundle extras = new Bundle();
                    extras.putString("link", link);
                    extras.putString("content", content);
                    extras.putString("title", title);
                    intent.putExtras(extras);
                    startActivity(intent);
                    overridePendingTransition(R.anim.ltor, R.anim.ltor);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        mSwipeRefreshLayout.setRefreshing(false);
    }
    public void populateAdapter(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if(items != null) {
            for (int i = 0; i < items.length(); i++) {
                try {
                    JSONObject temp_items = items.getJSONObject(i);
                    String title = temp_items.getString("title");
                    String source = temp_items.getString("source");
                    String time = temp_items.getString("pubdate");
                    String main = temp_items.getString("main");


                    boolean value = prefs.getBoolean(source, true);
                    if (!value) {
                        // the key does not exist

                    } else {

                        if(main != "null") {
                            WatchCell temp = new WatchCell(title, source, time, main);
                            adapter.add(temp);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }





        }
    }
    public class WatchCell {
        public String theId;
        public String theImage;
        public String theTitle;
        public String theActualImage;

        public WatchCell(String theId, String theImage, String theTitle, String theActualImage) {
            this.theId = theId;
            this.theImage = theImage;
            this.theTitle = theTitle;
            this.theActualImage = theActualImage;
        }
    }

    public class WatchAdapter extends ArrayAdapter<WatchCell> {
        public WatchAdapter(Context context, ArrayList<WatchCell> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            WatchCell cell = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.mainlayout, parent, false);
            }
            // Lookup view for data population
            ImageView yimg = (ImageView)convertView.findViewById(R.id.main_image);
            TextView t = (TextView) convertView.findViewById(R.id.title);
            TextView a = (TextView) convertView.findViewById(R.id.source);
            TextView d = (TextView) convertView.findViewById(R.id.time);
            if(cell.theActualImage != "") {
                Picasso.with(getContext()).load(cell.theActualImage).placeholder(R.drawable.placeholder).into(yimg);
            }


            t.setText(cell.theId);
            a.setText(cell.theImage+" - ");
            d.setText(convertTime(cell.theTitle));


            return convertView;
        }
    }

    public String convertTime(String timestamp){

        long date = System.currentTimeMillis()/1000;

        long different = date - Long.parseLong(timestamp);
//        Log.d("a", date +" " + Long.parseLong(timestamp) +" "+different);

        long secondsInMilli = 1000;
        long minutesInMilli = 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weeksInMilli = daysInMilli * 7;

        long weeksBetweenDates = different/weeksInMilli;
        long daysBetweenDates = different / daysInMilli;
        long hoursBetweenDates = different / hoursInMilli;
        long minutesBetweenDates = different / minutesInMilli;
        long secondsBetweenDates = different;
//        Log.d("stiff", daysBetweenDates +" " + minutesBetweenDates + " " + secondsBetweenDates);

        if(secondsBetweenDates < 60){
            if(secondsBetweenDates == 1)
                timestamp = secondsBetweenDates+" second ago";
            else
                timestamp = secondsBetweenDates+" seconds ago";

        }else
        if(minutesBetweenDates < 60){
            if(minutesBetweenDates == 1)
                timestamp = minutesBetweenDates+" min ago";
            else
                timestamp = minutesBetweenDates +" mins ago";
        } else
        if(hoursBetweenDates < 24){
            if(hoursBetweenDates == 1)
                timestamp = hoursBetweenDates+" hour ago";
            else
                timestamp = hoursBetweenDates + " hours ago";
        }else
        if(daysBetweenDates < 7){
            if(daysBetweenDates == 1)
                timestamp = daysBetweenDates+" day ago";
            else
                timestamp = daysBetweenDates +" days ago";

        } else{
            if(weeksBetweenDates == 1)
                timestamp = weeksBetweenDates+" week ago";
            else
                timestamp = weeksBetweenDates +" weeks ago";
            //                            timestamp = weeksBetweenDates +"w";
        }




        return timestamp;


    }

}
