package com.samurai.smartoffloading;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

import com.samurai.common.logger.Log;
import com.samurai.common.logger.LogFragment;
import com.samurai.common.logger.LogWrapper;
import com.samurai.common.logger.MessageOnlyLogFilter;

/**
 * Name: MainActivity
 * Author: Cheng ZHANG
 * Date: 2016-06-12
 * Description: The main activity for smart offloading
 */

public class MainActivity extends FragmentActivity {

    public static final String TAG = "Basic Network Demo";
    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;

    // Reference to the fragment showing events, so we can clear it with a button
    // as necessary.
    private LogFragment mLogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);

        // Initialize text fragment that displays intro text.
        SimpleTextFragment introFragment = (SimpleTextFragment)
                getSupportFragmentManager().findFragmentById(R.id.intro_fragment);
        introFragment.setText(R.string.intro_message);
        introFragment.getTextView().setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f);

        // Initialize the logging framework.
        initializeLogging();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info_action:
                Intent intent = new Intent(this, TestConnectionActivity.class);
                this.startActivity(intent);
                return true;
            // When the user clicks TEST, display the connection status.
            case R.id.test_action:
                checkNetworkConnection();
                return true;
            // Clear the log view fragment.
            case R.id.clear_action:
                mLogFragment.getLogView().setText("");
                return true;
        }
        return false;
    }

    /**
     * Check whether the device is connected, and if so, whether the connection
     * is wifi or mobile (it could be something else).
     */
    private void checkNetworkConnection() {
        // BEGIN_INCLUDE(connect)
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if(wifiConnected) {
                Log.i(TAG, getString(R.string.wifi_connection));
            } else if (mobileConnected){
                Log.i(TAG, getString(R.string.mobile_connection));
            }
        } else {
            Log.i(TAG, getString(R.string.no_wifi_or_mobile));
        }
        // END_INCLUDE(connect)
    }

    /** Create a chain of targets that will receive log data */
    public void initializeLogging() {

        // Using Log, front-end to the logging chain, emulates
        // android.util.log method signatures.

        // Wraps Android's native log framework
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);

        // A filter that strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        mLogFragment =
                (LogFragment) getSupportFragmentManager().findFragmentById(R.id.log_fragment);
        msgFilter.setNext(mLogFragment.getLogView());
    }
}
