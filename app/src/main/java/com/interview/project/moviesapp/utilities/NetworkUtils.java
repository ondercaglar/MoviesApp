package com.interview.project.moviesapp.utilities;

import android.net.Uri;
import android.util.Log;

import com.interview.project.moviesapp.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils
{
    private final static String THEMOVIEDB_URL = "https://api.themoviedb.org/3/movie/";
    private final static String PARAM_API_KEY  = "api_key";

    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();



    public static URL buildUrl(String paramType)
    {
        if(API_KEY.length() == 0 || API_KEY.isEmpty())
        {
            Log.e(LOG_TAG, " Please enter API KEY!");
        }

        Uri builtUri = Uri.parse(THEMOVIEDB_URL).buildUpon()
                .appendPath(paramType)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        URL url = null;
        try
        {
            url = new URL(builtUri.toString());
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        return url;
    }




    public static URL buildUrlVideosReviews(String movieID, String paramType)
    {
        if(API_KEY.length() == 0 || API_KEY.isEmpty())
        {
            Log.e(LOG_TAG, " Please enter API KEY!");
        }

        Uri builtUri = Uri.parse(THEMOVIEDB_URL).buildUpon()
                .appendPath(movieID)
                .appendPath(paramType)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        URL url = null;
        try
        {
            url = new URL(builtUri.toString());
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        return url;
    }



    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException
    {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try
        {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput)
            {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally
        {
            urlConnection.disconnect();
        }
    }
}