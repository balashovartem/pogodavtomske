package com.pogodavtomske;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.htmlcleaner.TagNode;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by IntelliJ IDEA.
 * User: Artemiy
 * Date: 12.03.12
 * Time: 12:08
 * To change this template use File | Settings | File Templates.
 */
public class WeatherParser
{
    private CurrentWeather currentWeather = new CurrentWeather();
    private ArrayList<ForecastWeather> forecastWeather = new ArrayList<ForecastWeather>();
    private HtmlHelper hh;
    private File cacheDir = null;
    private Context context = null;

    WeatherParser( Context aContext, URL url, File aCacheDir ) throws IOException
    {
        cacheDir = aCacheDir;
        context  = aContext;

        hh = new HtmlHelper( new URL("http://m.pogodavtomske.ru/") );
        parseCurrentWeather();

        List<TagNode> weatherTables = hh.getTableByClass("weather");
        parseForecastWeather( weatherTables, 1 );

        List<TagNode> forecast7Days = hh.getDivById("fullseven");
        weatherTables = hh.getTableByClass( forecast7Days.get(0), "fullweather");
        parseForecastWeather( weatherTables, 0 );
    }
    void parseCurrentWeather() throws IOException
    {
        List<TagNode> links = hh.getTableByClass("weather");
        TagNode table = links.get( 0 );

        TagNode img = (TagNode) table.getElementsByName("img",true)[0];
        currentWeather.ImageSrc = getBitmapFromURL( img.getAttributeByName("src") );

        List<TagNode> spanElements = table.getElementListByName("span", true);

        Pattern p = Pattern.compile("([+-]?\\d+\\.?\\d?).+");
        Matcher m = p.matcher( spanElements.get(0).getText() );
        while( m.find() )
        {
            currentWeather.Temperature = m.group(1);
        }

        m = p.matcher( spanElements.get(1).getText() );
        while( m.find() )
        {
            currentWeather.Wind = m.group(1);
        }

        m = p.matcher( spanElements.get(2).getText() );
        while( m.find() )
        {
            currentWeather.Humanity = m.group(1);
        }

        m = p.matcher(spanElements.get(3).getText());
        while( m.find() )
        {
            currentWeather.Pressure = m.group(1);
        }
    }
    void parseForecastWeather( List<TagNode> weatherTables, int beginIndex )
    {
        for( int i = beginIndex; i < weatherTables.size(); ++i )
        {
            ForecastWeather weather = new ForecastWeather();
            Pattern p;
            Matcher m;

            TagNode weatherTable = weatherTables.get(i);

            TagNode img = (TagNode) weatherTable.getElementsByName("img",true)[0];
            weather.ImageSrc = getBitmapFromURL( img.getAttributeByName("src") );

            List<TagNode> spanElements = weatherTable.getElementListByName("span", true);

            p = Pattern.compile("(-?[\\d\\.]+)[^-]+(-?[\\d\\.]+) &deg;c");
            m = p.matcher( spanElements.get(0).getText() );
            while( m.find() )
            {
                weather.TemperatureDay = m.group(1);
                weather.TemperatureNight = m.group(2);
            }
            p = Pattern.compile("(\\d+).+");
            m = p.matcher( spanElements.get(1).getText() );
            while( m.find() )
            {
                weather.Wind = m.group(1);
            }

            m = p.matcher( spanElements.get(2).getText() );
            while( m.find() )
            {
                weather.Pressure = m.group(1);
            }

            forecastWeather.add( weather );
        }
    }
    public Bitmap getBitmapFromURL(String src) {
            String srcFileName = null;
            Pattern p = Pattern.compile(".+/(.+).png");
            Matcher m = p.matcher( src );
            while( m.find() )
            {
                srcFileName = m.group(1);
            }
            if( srcFileName == null )
            {
                return null;
            }
            Resources res = context.getResources();
            int imageResource = res.getIdentifier( "drawable/cache_weather_"+ srcFileName, null, context.getPackageName());
            if( imageResource == 0 )
            {
                Bitmap myBitmap = BitmapFactory.decodeResource( context.getResources(), R.drawable.undefine );
                return myBitmap;
            }

            Bitmap myBitmap = BitmapFactory.decodeResource( context.getResources(), imageResource );
            return myBitmap;
    }

    CurrentWeather current()
    {
        return currentWeather;
    }
    ArrayList<ForecastWeather> forecast()
    {
        return forecastWeather;
    }
}
