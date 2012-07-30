package com.pogodavtomske;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Artemiy
 * Date: 13.03.12
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */
public class ForecastWeatherAdapter extends ArrayAdapter<ForecastWeather> {
    private final Context context;
    private final List<ForecastWeather> values;

    public ForecastWeatherAdapter(Context context, List<ForecastWeather> values) {
        super(context, R.layout.weather_forecast_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.weather_forecast_item, parent, false);


        ImageView weatherImg = (ImageView) rowView.findViewById(R.id.forecast_weather_image);
        TextView alias = (TextView) rowView.findViewById(R.id.forecast_alias);
        TextView date = (TextView) rowView.findViewById(R.id.forecast_date);
        TextView dayTemperature = (TextView) rowView.findViewById(R.id.forecast_temperature_day);
        TextView nightTemperature = (TextView) rowView.findViewById(R.id.forecast_temperature_night);
        TextView wind = (TextView) rowView.findViewById(R.id.forecast_wind);
        TextView pressure = (TextView) rowView.findViewById(R.id.forecast_atmospheric_pressure);

        Calendar day = Calendar.getInstance();
        day.set(
                Calendar.DAY_OF_YEAR,
                day.get(Calendar.DAY_OF_YEAR) + position
        );

        weatherImg.setImageBitmap(
                BitmapFactory.decodeResource(
                        context.getResources(),
                        context.getResources().getIdentifier(values.get(position).ImageSrc, null, context.getPackageName())
                )
        );
        alias.setText(DateFormat.format("EEEE", day));
        date.setText(DateFormat.format("d MMMM", day));
        dayTemperature.setText(values.get(position).TemperatureDay);
        nightTemperature.setText(values.get(position).TemperatureNight);
        wind.setText(values.get(position).Wind);
        pressure.setText(values.get(position).Pressure);

        switch (position) {
            case 0:
                alias.setText(context.getString(R.string.forecast_today));
                break;
            case 1:
                alias.setText(context.getString(R.string.forecast_tomorrow));
                break;
        }


        return rowView;
    }
}
