package com.pogodavtomske;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by IntelliJ IDEA.
 * User: Artem
 * Date: 12.03.12
 * Time: 22:24
 * To change this template use File | Settings | File Templates.
 */
public class CurrentWeather  extends Object implements Parcelable
{
    public Bitmap ImageSrc;
    public String Temperature;
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
        dest.writeString( Temperature );
        dest.writeString( Wind );
        dest.writeString( Humanity );
        dest.writeString( Pressure );
        ImageSrc.writeToParcel( dest, flags);
    }

    public static final Parcelable.Creator<CurrentWeather> CREATOR
            = new Parcelable.Creator<CurrentWeather>()
    {
        public CurrentWeather createFromParcel(Parcel in)
        {
            return new CurrentWeather(in);
        }

        public CurrentWeather[] newArray(int size)
        {
            return new CurrentWeather[size];
        }
    };

    private CurrentWeather(Parcel in)
    {
        Temperature = in.readString();
        Wind = in.readString();
        Humanity = in.readString();
        Pressure = in.readString();
        ImageSrc = Bitmap.CREATOR.createFromParcel( in );
    }
    public CurrentWeather()
    {

    }
}
