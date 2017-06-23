package com.kons.slider;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.icu.util.Calendar;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.BaseInputConnection;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

public class SlideActivity extends IOIOActivity {

    private static final String DEFAULT_PAGE = "http://i-windo.co.uk/ioio/parallax.html";
    public static final String PAGE_LOCATION_SETTING_NAME = "location";
    protected PowerManager.WakeLock mWakeLock;
    private final int RIGHTARROW_PIN = 34;
    private final int LEFTARROW_PIN = 35;
    private final int UPARROW_PIN = 36;
    private final int DOWNARROW_PIN = 37;
    private Timer timer;
    private TimerTask timerTask;
    private TimerTask refreshTimerTask;
    final Handler handlerWakeLock = new Handler();
    private Date lastRefresh = new Date();
    private Date lastUserAction = new Date();


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (KeyEvent.KEYCODE_SPACE == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction()) {
            if (getActionBar().isShowing()) {
                getActionBar().hide();
            } else {
                getActionBar().show();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  invalidateOptionsMenu();

        //Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        //requestWindowFeature(Window.FEATURE_ACTION_BAR);
        hideNavigation();

        setContentView(R.layout.activity_slide);



        /* This code together with the one in onDestroy()
         * will make the screen be always on until this Activity gets destroyed. */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
//        this.mWakeLock.acquire();

        final WebView wv = (WebView) findViewById(R.id.my_webview);
        wv.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

        });
        wv.getSettings().setJavaScriptEnabled(true);
        setWebViewUrl(wv);
        getActionBar().hide();
    }


    private void setWebViewUrl(WebView wv) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        wv.loadUrl(sharedPreferences.getString(PAGE_LOCATION_SETTING_NAME, DEFAULT_PAGE));
    }

    private void setWebViewUrl() {
        setWebViewUrl((WebView) findViewById(R.id.my_webview));
    }

    private Date getStartupTime() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String s = sharedPreferences.getString("startupTime", "09:00");
        System.out.println(s);
        return null;
    }


    @Override
    protected void onResume() {
        super.onResume();
        setWebViewUrl();
        hideNavigation();
        startTimers();
    }

    private void setFlagKeepScreenOn() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        if (params.screenBrightness == 0) {
            params.screenBrightness = -1;
            getWindow().setAttributes(params);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void clearFlagKeepScreenOn() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = 0;
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setKeepTheScreenOn() {
        if (keepTheScreenOn()) {
            setFlagKeepScreenOn();
        } else {
            clearFlagKeepScreenOn();
        }
    }

    private void refresh() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Integer refreshInterval = Integer.valueOf(sharedPreferences.getString("refreshInterval", "180"));
        Date now = new Date();
        if (getDifferenceInMinutes(now, lastRefresh) > refreshInterval && getDifferenceInMinutes(now, lastUserAction) > 5) {
            WebView webView = ((WebView) findViewById(R.id.my_webview));
            webView.loadUrl(webView.getUrl());
            lastRefresh = now;
        }
    }

    private long getDifferenceInMinutes(Date first, Date second) {
        return Math.abs(first.getTime() - second.getTime()) / 1000 / 60;
    }

    private boolean keepTheScreenOn() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String startupTime = sharedPreferences.getString("startupTime", "09:00");
        String shutdownTime = sharedPreferences.getString("shutdownTime", "21:00");

        int startupMinute = Integer.valueOf(TimePreference.getMinute(startupTime));
        int startupHour = Integer.valueOf(TimePreference.getHour(startupTime));
        int shutdownMinute = Integer.valueOf(TimePreference.getMinute(shutdownTime));
        int shutdownHour = Integer.valueOf(TimePreference.getHour(shutdownTime));

        Calendar now = Calendar.getInstance();
        Calendar startupCalendar = Calendar.getInstance();
        startupCalendar.set(Calendar.HOUR_OF_DAY, startupHour);
        startupCalendar.set(Calendar.MINUTE, startupMinute);
        Calendar shutdownCalendar = Calendar.getInstance();
        shutdownCalendar.set(Calendar.HOUR_OF_DAY, shutdownHour);
        shutdownCalendar.set(Calendar.MINUTE, shutdownMinute);

        return now.after(startupCalendar) && now.before(shutdownCalendar);
    }

    public void startTimers() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();
        initializeRefreshTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, 10000); //
        timer.schedule(refreshTimerTask, 5000, 1000);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handlerWakeLock.post(new Runnable() {
                    public void run() {
                        setKeepTheScreenOn();
                    }
                });
            }
        };
    }

    public void initializeRefreshTimerTask() {
        refreshTimerTask = new TimerTask() {
            public void run() {
                handlerWakeLock.post(new Runnable() {
                    public void run() {
                        refresh();
                    }
                });
            }
        };
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavigation();
    }

    public void hideNavigation() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class Looper extends BaseIOIOLooper {

        private DigitalInput rightArrow;
        private DigitalInput leftArrow;
        private DigitalInput upArrow;
        private DigitalInput downArrow;

        @Override
        protected void setup() throws ConnectionLostException, InterruptedException {
            try {
                rightArrow = ioio_.openDigitalInput(RIGHTARROW_PIN, DigitalInput.Spec.Mode.PULL_UP);
                leftArrow = ioio_.openDigitalInput(LEFTARROW_PIN, DigitalInput.Spec.Mode.PULL_UP);
                upArrow = ioio_.openDigitalInput(UPARROW_PIN, DigitalInput.Spec.Mode.PULL_UP);
                downArrow = ioio_.openDigitalInput(DOWNARROW_PIN, DigitalInput.Spec.Mode.PULL_UP);
            } catch (ConnectionLostException e) {
                throw e;
            }
        }

        @Override
        public void loop() throws ConnectionLostException, InterruptedException {
            try {

                final boolean rightArrowReading = rightArrow.read();
                final boolean leftArrowReading = leftArrow.read();
                final boolean upArrowReading = upArrow.read();
                final boolean downArrowReading = downArrow.read();

                View view = findViewById(android.R.id.content);

                if (!rightArrowReading) {
                    move(view, KeyEvent.KEYCODE_DPAD_RIGHT);
                }
                if (!leftArrowReading) {
                    move(view, KeyEvent.KEYCODE_DPAD_LEFT);
                }
                if (!upArrowReading) {
                    move(view, KeyEvent.KEYCODE_DPAD_UP);
                }
                if (!downArrowReading) {
                    move(view, KeyEvent.KEYCODE_DPAD_DOWN);
                }

                Thread.sleep(20);

            } catch (InterruptedException e) {
                ioio_.disconnect();
            } catch (ConnectionLostException e) {
                throw e;
            }
        }
    }


    private void move(View view, int direction) {
        BaseInputConnection baseInputConnection = new BaseInputConnection(view, true);
        baseInputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, direction));
        lastUserAction = new Date();
    }


    @Override
    protected IOIOLooper createIOIOLooper() {
        return new Looper();
    }


}