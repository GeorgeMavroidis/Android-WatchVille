package com.georgemavroidis.watchville;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by george on 14-11-18.
 */
public class ContentView extends Activity {

    private static final String TAG = "Main";
    private ProgressDialog progressBar;
    String link;
    String content;
    String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = this.getIntent().getExtras();
        if ( extras != null ) {
            if ( extras.containsKey("link") ) {
                link = extras.getString("link");
            }
            if ( extras.containsKey("content") ) {
                content = extras.getString("content");
            }
            if ( extras.containsKey("title") ) {
                title = extras.getString("title");
            }
        }

        setContentView(R.layout.content_layout);
        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.content_action_bar, null);

//        ImageButton imageButton = (ImageButton) mCustomView
//                .findViewById(R.id.imageButton);

        TextView close = (TextView) mCustomView.findViewById(R.id.escape);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                overridePendingTransition(R.anim.rtol, R.anim.rtol);
            }
        });
        ImageButton imageButton = (ImageButton) mCustomView
                .findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String message = "Test";
                try
                {
                    // Check if the Twitter app is installed on the phone.
                    getPackageManager().getPackageInfo("com.twitter.android", 0);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setClassName("com.twitter.android", "com.twitter.android.composer.ComposerActivity");
                    intent.setType("text/plain");
                    Log.d("d", title + " " + title.length());

                    if(title.length() >= 140){
                        title = title.substring(0, 125);
                    }
                    intent.putExtra(Intent.EXTRA_TEXT, title);
                    intent.putExtra(Intent.EXTRA_TEXT, title + "via @watchville");
                    startActivity(intent);

                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Twitter is not installed on this device", Toast.LENGTH_LONG).show();

                }
        }
        });

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        WebView w = (WebView) findViewById(R.id.content_webview);
        WebSettings settings = w.getSettings();

//        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);

        settings.setLoadsImagesAutomatically(true);
        settings.setDomStorageEnabled(true);
        w.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

//        progressBar = ProgressDialog.show(ContentView.this, "WebView Example", "Loading...");

        w.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "Processing webview url click...");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {

            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(TAG, "Error: " + description);

                alertDialog.setTitle("Error");
                alertDialog.setMessage(description);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialog.show();
            }
        });
        w.loadUrl(link);
//        w.loadData(content, "text/html", null);

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
