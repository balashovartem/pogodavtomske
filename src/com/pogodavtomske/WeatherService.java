package com.pogodavtomske;


import android.app.Service;
import android.content.*;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
    static String ACTION_CURRENT_WEATHER = "com.pogodavtomske.WeatherService.ActionCurrentWeather";
    static String ACTION_FORECAST_WEATHER = "com.pogodavtomske.WeatherService.ActionForecastWeather";
    static String ACTION_REQUEST_WEATHER = "com.pogodavtomske.WeatherService.ActionRequestWeather";
    static String ACTION_ERROR_WEATHER = "com.pogodavtomske.WeatherService.ActionErrorWeather";

    private final IBinder mBinder = new LocalBinder();
    private final Timer mTimer = new Timer("WeatherService", true);
    private CurrentWeather mLastCurrentWeather = null;
    private ArrayList<ForecastWeather> mLastForecastWeather = null;

    public class LocalBinder extends Binder {
        WeatherService getService() {
            return WeatherService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (WeatherService.ACTION_REQUEST_WEATHER.contains(action)) {
                sendWeather();
            }
        }
    };

    @Override
    public void onCreate() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        prefs.registerOnSharedPreferenceChangeListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(WeatherService.ACTION_REQUEST_WEATHER);
        registerReceiver(mReceiver, filter);

        restart();

        super.onCreate();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        restart();
    }

    private void restart() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int period = Integer.parseInt(prefs.getString("update_interval", "1"));
        period *= 60 * 1000;
        mTimer.purge();
        mTimer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        receiveWeather();
                        sendWeather();
                    }
                },
                0,
                period
        );
    }

    private void receiveWeather() {
        try {
            final WeatherParser weather = new WeatherParser(getApplicationContext(), new URL("http://m.pogodavtomske.ru/"), getCacheDir());

            mLastCurrentWeather = weather.current();
            mLastForecastWeather = weather.forecast();
        } catch (Exception e) {
            Intent intentCurrentWeather = new Intent();
            intentCurrentWeather.setAction(ACTION_ERROR_WEATHER);
            intentCurrentWeather.putExtra("msg", this.getResources().getString(R.string.recive_weather_error));
            sendBroadcast(intentCurrentWeather);
        }
    }

    private void sendWeather() {
        if (mLastCurrentWeather == null && mLastForecastWeather == null) {
            receiveWeather();
        }
        if (mLastCurrentWeather != null) {
            Intent intentCurrentWeather = new Intent();
            intentCurrentWeather.setAction(ACTION_CURRENT_WEATHER);
            intentCurrentWeather.putExtra("current", mLastCurrentWeather);
            sendBroadcast(intentCurrentWeather);
        }
        if (mLastForecastWeather != null) {
            Intent intentForecastWeather = new Intent();
            intentForecastWeather.setAction(ACTION_FORECAST_WEATHER);
            intentForecastWeather.putExtra("forecast", mLastForecastWeather);
            sendBroadcast(intentForecastWeather);
        }
    }
}
