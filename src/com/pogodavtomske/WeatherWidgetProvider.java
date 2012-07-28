package com.pogodavtomske;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by IntelliJ IDEA.
 * User: Artemiy
 * Date: 15.03.12
 * Time: 17:33
 * To change this template use File | Settings | File Templates.
 */
public class WeatherWidgetProvider extends AppWidgetProvider {
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        drawNullWeather(context);
        context.startService(new Intent(context, WeatherService.class));

        initWidget(context, appWidgetManager, appWidgetIds);

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        final String action = intent.getAction();
        if (WeatherService.ACTION_CURRENT_WEATHER.contains(action)) {
            CurrentWeather currentWeather = (CurrentWeather) intent.getParcelableExtra("current");
            drawCurrentWeather(context, currentWeather);
        }
        if (WeatherService.ACTION_ERROR_WEATHER.contains(action)) {
            drawNullWeather(context);
            Toast.makeText(context, intent.getStringExtra("msg"), Toast.LENGTH_LONG).show();
        }
    }

    private void drawCurrentWeather(final Context context, final CurrentWeather weather) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        remoteViews.setTextViewText(R.id.widget_temperature, weather.Temperature);
        remoteViews.setTextViewText(R.id.widget_wind, weather.Wind);
        remoteViews.setTextViewText(R.id.widget_pressure, weather.Pressure);
        remoteViews.setBitmap(R.id.widget_weather_image, "setImageBitmap", weather.ImageSrc);

        ComponentName comp = new ComponentName(context.getPackageName(), WeatherWidgetProvider.class.getName());
        AppWidgetManager.getInstance(context).updateAppWidget(comp, remoteViews);
    }

    private void drawNullWeather(final Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        remoteViews.setTextViewText(R.id.widget_temperature, "?");
        remoteViews.setTextViewText(R.id.widget_wind, "?");
        remoteViews.setTextViewText(R.id.widget_pressure, "?");
        remoteViews.setBitmap(
                R.id.widget_weather_image,
                "setImageBitmap",
                BitmapFactory.decodeResource(context.getResources(), R.drawable.undefine)
        );

        ComponentName comp = new ComponentName(context.getPackageName(), WeatherWidgetProvider.class.getName());
        AppWidgetManager.getInstance(context).updateAppWidget(comp, remoteViews);
    }

    private void initWidget(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            Intent intentRequestWeather = new Intent();
            intentRequestWeather.setAction(WeatherService.ACTION_REQUEST_WEATHER);
            context.sendBroadcast(intentRequestWeather);

            Intent intent = new Intent(context, WeatherActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
            views.setOnClickPendingIntent(R.id.weather_widget, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}

