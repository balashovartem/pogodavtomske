package com.pogodavtomske;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by IntelliJ IDEA.
 * User: Artemiy
 * Date: 13.03.12
 * Time: 9:51
 * To change this template use File | Settings | File Templates.
 */
public class ForecastWeather extends Object implements Parcelable
{
    public Bitmap ImageSrc;
    public String TemperatureDay;
    public String TemperatureNight;
    public String Wind;
    public String Humanity;
    public String Pressure;


    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString( TemperatureDay );
        dest.writeString( TemperatureNight );
        dest.writeString( Wind );
        dest.writeString( Humanity );
        dest.writeString( Pressure );
        ImageSrc.writeToParcel( dest, flags);
    }

    public static final Parcelable.Creator<ForecastWeather> CREATOR
            = new Parcelable.Creator<ForecastWeather>()
    {
        public ForecastWeather createFromParcel(Parcel in)
        {
            return new ForecastWeather(in);
        }

        public ForecastWeather[] newArray(int size)
        {
            return new ForecastWeather[size];
        }
    };

    private ForecastWeather(Parcel in)
    {
        TemperatureDay = in.readString();
        TemperatureNight = in.readString();
        Wind = in.readString();
        Humanity = in.readString();
        Pressure = in.readString();
        ImageSrc = Bitmap.CREATOR.createFromParcel( in );
    }
    public ForecastWeather()
    {

    }
}
