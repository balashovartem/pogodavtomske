package com.pogodavtomske;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeatherActivity extends Activity {
    private ProgressDialog mProgress = null;
    private boolean mIsEmpty = true;

    private ForecastWeatherAdapter adapter = null;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (WeatherService.ACTION_CURRENT_WEATHER.contains(action)) {
                CurrentWeather currentWeather = (CurrentWeather) intent.getParcelableExtra("current");
                drawCurrentWeather(currentWeather);
                mIsEmpty = false;
                if (mProgress != null)
                    mProgress.dismiss();
            }
            if (WeatherService.ACTION_FORECAST_WEATHER.contains(action)) {
                ArrayList<ForecastWeather> forecasts = intent.getParcelableArrayListExtra("forecast");
                drawForecastWeather(forecasts);
                mIsEmpty = false;
                if (mProgress != null)
                    mProgress.dismiss();
            }
            if (WeatherService.ACTION_ERROR_WEATHER.contains(action)) {
                drawNullWeather();
                mIsEmpty = true;
                if (mProgress != null)
                    mProgress.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        startService(new Intent(this, WeatherService.class));

        IntentFilter filter = new IntentFilter();
        filter.addAction(WeatherService.ACTION_FORECAST_WEATHER);
        filter.addAction(WeatherService.ACTION_CURRENT_WEATHER);
        filter.addAction(WeatherService.ACTION_ERROR_WEATHER);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsEmpty) {
            drawNullWeather();

            mProgress = ProgressDialog.show(this,
                    getResources().getText(R.string.loading_title),
                    getResources().getText(R.string.loading_description),
                    true,
                    true
            );
        }
        Intent intentRequestWeather = new Intent();
        intentRequestWeather.setAction(WeatherService.ACTION_REQUEST_WEATHER);
        sendBroadcast(intentRequestWeather);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void drawNullWeather() {
        final TextView temperatureView = (TextView) findViewById(R.id.current_temperature);
        temperatureView.setText("?");

        final TextView windView = (TextView) findViewById(R.id.current_wind);
        windView.setText("?");

        final TextView humanityView = (TextView) findViewById(R.id.current_humanity);
        humanityView.setText("?");

        final TextView pressureView = (TextView) findViewById(R.id.current_atmospheric_pressure);
        pressureView.setText("?");

        final ImageView weatherImg = (ImageView) findViewById(R.id.current_weather_image);
        weatherImg.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.undefine));
    }

    private void drawCurrentWeather(CurrentWeather weather) {
        final TextView temperatureView = (TextView) findViewById(R.id.current_temperature);
        temperatureView.setText(weather.Temperature);

        final TextView windView = (TextView) findViewById(R.id.current_wind);
        windView.setText(weather.Wind);

        final TextView humanityView = (TextView) findViewById(R.id.current_humanity);
        humanityView.setText(weather.Humanity);

        final TextView pressureView = (TextView) findViewById(R.id.current_atmospheric_pressure);
        pressureView.setText(weather.Pressure);

        final ImageView weatherImg = (ImageView) findViewById(R.id.current_weather_image);
        weatherImg.setImageBitmap(
                BitmapFactory.decodeResource(
                        getResources(),
                        getResources().getIdentifier(weather.ImageSrc, null, getPackageName())
                )
        );

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentDateandTime = sdf.format(new Date());

        setTitle(
                getResources().getString(getResources().getIdentifier("string/app_name", null, getPackageName())) +
                        " : " +
                        getResources().getString(getResources().getIdentifier("string/update", null, getPackageName())) +
                        " " +
                        currentDateandTime
        );
    }

    private void drawForecastWeather(List<ForecastWeather> forecasts) {
        if (adapter == null) {
            ForecastWeatherAdapter adapter = new ForecastWeatherAdapter(
                    WeatherActivity.this,
                    forecasts
            );

            ListView listView = (ListView) findViewById(R.id.forecast);
            listView.setAdapter(adapter);
            return;
        }

        adapter.clear();
        for (ForecastWeather forecast : forecasts) {
            adapter.add(forecast);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.preferences:
                Intent settingsActivity = new Intent(getBaseContext(), Preferences.class);
                startActivity(settingsActivity);
                return true;
            case R.id.refresh:
                stopService(new Intent(this, WeatherService.class));
                mIsEmpty = false;
                mProgress = ProgressDialog.show(this,
                        getResources().getText(R.string.loading_title),
                        getResources().getText(R.string.loading_description),
                        true,
                        true
                );
                startService(new Intent(this, WeatherService.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
