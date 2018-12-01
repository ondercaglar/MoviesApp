package com.interview.project.moviesapp.utilities;


import com.interview.project.moviesapp.model.Movies;
import com.interview.project.moviesapp.model.Reviews;
import com.interview.project.moviesapp.model.Trailers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class JsonUtils
{

    private static final String JSON_RESULTS         = "results";
    private static final String JSON_MOVIE_ID        = "id";
    private static final String JSON_ORIGINAL_TITLE  = "original_title";
    private static final String JSON_POSTER_IMAGE    = "poster_path";
    private static final String JSON_OVERWIEV        = "overview";
    private static final String JSON_VOTE_AVERAGE    = "vote_average";
    private static final String JSON_RELEASE_DATE    = "release_date";
    private static final String JSON_BACKDROP        = "backdrop_path";


    private static final String JSON_TRAILER_NAME   = "name";
    private static final String JSON_TRAILER_KEY    = "key";
    private static final String JSON_TRAILER_TYPE   = "type";

    private static final String JSON_REVIEW_AUTHOR   = "author";
    private static final String JSON_REVIEW_CONTENT  = "content";
    private static final String JSON_REVIEW_URL      = "url";


    private final static String IMAGE_URL = "http://image.tmdb.org/t/p/w185/";


    public static ArrayList<Movies> parseMoviesJson(String json)
    {

        ArrayList<Movies> mMovies = new ArrayList();

        try
        {
            JSONObject readerObject = new JSONObject(json);

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = readerObject.optJSONArray(JSON_RESULTS);

            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonArray.length(); i++)
            {
                Movies mFilm = new Movies();
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String movie_id   = jsonObject.optString(JSON_MOVIE_ID);
                mFilm.setMovieID(movie_id);

                String original_title   = jsonObject.optString(JSON_ORIGINAL_TITLE);
                mFilm.setOriginalTitle(original_title);

                String poster_path = jsonObject.optString(JSON_POSTER_IMAGE);
                mFilm.setPosterPath(IMAGE_URL + poster_path);

                String overview = jsonObject.optString(JSON_OVERWIEV);
                mFilm.setOverview(overview);

                String vote_average = jsonObject.optString(JSON_VOTE_AVERAGE);
                mFilm.setVoteAverage(vote_average);

                String release_date = jsonObject.optString(JSON_RELEASE_DATE);
                mFilm.setReleaseDate(release_date);

                String backdrop_path = jsonObject.optString(JSON_BACKDROP);
                mFilm.setBackdropPath(IMAGE_URL + backdrop_path);


                mMovies.add(mFilm);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return mMovies;
    }




    public static ArrayList<Trailers> parseTrailersJson(String json)
    {

        ArrayList<Trailers> mTrailers = new ArrayList();

        try
        {
            JSONObject readerObject = new JSONObject(json);

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = readerObject.optJSONArray(JSON_RESULTS);

            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonArray.length(); i++)
            {
                Trailers mTrailer = new Trailers();
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String name   = jsonObject.optString(JSON_TRAILER_NAME);
                mTrailer.setName(name);

                String key   = jsonObject.optString(JSON_TRAILER_KEY);
                mTrailer.setKey(key);

                String type = jsonObject.optString(JSON_TRAILER_TYPE);
                mTrailer.setType(type);


                if (type.equals("Trailer"))
                {
                    mTrailers.add(mTrailer);
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return mTrailers;
    }



    public static ArrayList<Reviews> parseReviewsJson(String json)
    {

        ArrayList<Reviews> mReviews = new ArrayList();

        try
        {
            JSONObject readerObject = new JSONObject(json);

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = readerObject.optJSONArray(JSON_RESULTS);

            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonArray.length(); i++)
            {
                Reviews mReview = new Reviews();
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String author = jsonObject.optString(JSON_REVIEW_AUTHOR);
                mReview.setAuthor(author);

                String content = jsonObject.optString(JSON_REVIEW_CONTENT);
                mReview.setContent(content);

                String url = jsonObject.optString(JSON_REVIEW_URL);
                mReview.setUrl(url);

                mReviews.add(mReview);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return mReviews;
    }



}
