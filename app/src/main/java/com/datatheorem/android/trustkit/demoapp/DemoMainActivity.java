package com.datatheorem.android.trustkit.demoapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.datatheorem.android.trustkit.TrustKit;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;


public class DemoMainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "TrustKit-Demo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textView = (TextView) findViewById(R.id.textview);

        // Initialize TrustKit with the default path for the Network Security Configuration which is
        // res/xml/network_security_config.xml
        TrustKit.initializeWithNetworkSecurityConfiguration(this);
        // Connect to the URL with valid pins - this connection will succeed
        new DownloadWebpageTask().execute("https://www.datatheorem.com");

        // Connect to the URL with invalid pins - this connection will fail
        new DownloadWebpageTask().execute("https://www.google.com");

        textView.setText("Connection results are in the logs");
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection connection = null;
                connection = (HttpsURLConnection) url.openConnection();
                connection.setSSLSocketFactory(TrustKit.getInstance().getSSLSocketFactory(url.getHost()));
                InputStream inputStream = connection.getInputStream();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed to connect to: " + params[0];
            }
            return "Successfully connected to: " + params[0];
        }

        @Override
        protected void onPostExecute(String result) {
            // Log the response
            Log.i(DEBUG_TAG, result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_demo_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

