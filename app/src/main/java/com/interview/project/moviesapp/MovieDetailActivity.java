package com.interview.project.moviesapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.interview.project.moviesapp.databinding.ActivityDetailBinding;
import com.interview.project.moviesapp.model.Movies;
import com.interview.project.moviesapp.model.MoviesContract;
import com.interview.project.moviesapp.model.Reviews;
import com.interview.project.moviesapp.model.Trailers;
import com.interview.project.moviesapp.utilities.InternetConnectionDetector;
import com.interview.project.moviesapp.utilities.JsonUtils;
import com.interview.project.moviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks,
        TrailerAdapter.TrailerAdapterOnClickHandler, ReviewsAdapter.ReviewsAdapterOnClickHandler
{
    private ActivityDetailBinding detailBinding;
    private TrailerAdapter trailerAdapter;
    private ReviewsAdapter reviewsAdapter;


    private static final String QUERY_URL_VIDEOS  = "query_videos";
    private static final String QUERY_URL_REVIEWS = "query_reviews";
    private static final String YOUTUBE = "https://www.youtube.com/watch?v=";

    /*
     * This number will uniquely identify our Loader and is chosen arbitrarily.
     */
    private static final int THE_TRAILER_LOADER = 22;
    private static final int THE_REVIEW_LOADER = 20;
    private static final int ID_FAVORITES_LOADER = 67;
    private ArrayList<Trailers> trailersList;
    private ArrayList<Reviews> reviewsList;

    private  Movies movies;
    private Cursor mCursor;
    private Toast mToast;
    private int mCursorSize = 0;


    private static final String STATE_KEY_TRAILERS = "state_key_trailers";
    private static final String STATE_KEY_REVIEWS  = "state_key_reviews";
    private static final String STATE_KEY_MOVIES   = "state_key_movies";
    private static final String STATE_KEY_FAVORITE = "state_key_favorite";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        detailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        final Toolbar toolbar = detailBinding.toolbar;
        setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();

        // Set the action bar back button to look like an up button
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        detailBinding.imgbtnFavorite.setOnClickListener(favoritesButtonListener);
        detailBinding.imgbtnShare.setOnClickListener(shareButtonListener);
        detailBinding.floatingButton.setOnClickListener(fab);


        LinearLayoutManager layoutManager    = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager newLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        detailBinding.trailersInfo.recyclerviewTrailers.setLayoutManager(layoutManager);
        detailBinding.trailersInfo.recyclerviewTrailers.setHasFixedSize(true);

        detailBinding.reviewsInfo.recyclerviewRevies.setLayoutManager(newLayoutManager);
        detailBinding.reviewsInfo.recyclerviewRevies.setHasFixedSize(true);


        if (savedInstanceState != null)
        {
            trailersList = savedInstanceState.getParcelableArrayList(STATE_KEY_TRAILERS);
            reviewsList  = savedInstanceState.getParcelableArrayList(STATE_KEY_REVIEWS);
            movies       = savedInstanceState.getParcelable(STATE_KEY_MOVIES);
            mCursorSize  = savedInstanceState.getInt(STATE_KEY_FAVORITE);

            configureFavoriteButton();

            trailerAdapter = new TrailerAdapter(this, this);
            detailBinding.trailersInfo.recyclerviewTrailers.setAdapter(trailerAdapter);


            if (trailersList.size() == 0)
            {
                detailBinding.trailersInfo.txtTrailerLabel.setVisibility(View.GONE);
                detailBinding.trailersInfo.recyclerviewTrailers.setVisibility(View.GONE);
            }
            else
            {
                detailBinding.imgbtnShare.setVisibility(View.VISIBLE);
                trailerAdapter.swapMovie(trailersList);
            }

            reviewsAdapter = new ReviewsAdapter(this, this);
            detailBinding.reviewsInfo.recyclerviewRevies.setAdapter(reviewsAdapter);
            if (reviewsList.size() == 0)
            {
                detailBinding.reviewsInfo.txtReviewsLabel.setVisibility(View.GONE);
                detailBinding.reviewsInfo.recyclerviewRevies.setVisibility(View.GONE);
            }
            else
            {
                reviewsAdapter.swapMovie(reviewsList);
            }
        }
        else
        {
            movies = getIntent().getParcelableExtra("movies");
            makeTheMovieDbSearchQuery();
        }



        if(movies!=null)
        {
            detailBinding.collapsingToolbar.setTitle(movies.getOriginalTitle());
            detailBinding.voteAverageTv.setText(movies.getVoteAverage() + "/10");
            detailBinding.releaseDateTv.setText(movies.getReleaseDate());
            detailBinding.overviewTv.setText(movies.getOverview());


            Picasso.get()
                    .load(movies.getBackdropPath())
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder_error)
                    .into(detailBinding.backdrop);

            Picasso.get()
                    .load(movies.getPosterPath())
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder_error)
                    .into(detailBinding.imageIv);
        }

    }



    private final View.OnClickListener favoritesButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (mCursorSize!= 0)
            {
                ContentResolver favoriteContentResolver = getContentResolver();
                int numRowsDeleted =  favoriteContentResolver.delete(MoviesContract.MovieEntry.
                                buildMovieUriWithID(Long.parseLong(movies.getMovieID())),
                        null, null);

                if (numRowsDeleted != 0)
                {
                    detailBinding.imgbtnFavorite.setImageResource(R.drawable.ic_favorite_border_black_48dp);
                    detailBinding.floatingButton.setImageResource(R.drawable.ic_favorite_border_black_48dp);
                    mCursorSize = 0;
                    showToast(getResources().getString(R.string.remove_favr));
                }
            }
            else
            {
                // Insert new favorite movie data via a ContentResolver
                ContentValues contentValues = new ContentValues();
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID,       movies.getMovieID());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movies.getOriginalTitle());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH,    movies.getPosterPath());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW,       movies.getOverview());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,   movies.getVoteAverage());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,   movies.getReleaseDate());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH,  movies.getBackdropPath());
                // Insert the content values via a ContentResolver
              Uri resultsUri = getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, contentValues);

                if(resultsUri != null)
                {
                    detailBinding.imgbtnFavorite.setImageResource(R.drawable.ic_favorite_red_500_48dp);
                    detailBinding.floatingButton.setImageResource(R.drawable.ic_favorite_red_500_48dp);
                    mCursorSize = 1;
                    showToast(getResources().getString(R.string.add_favr));
                }
            }
        }
    };


    private final View.OnClickListener fab = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (mCursorSize!= 0)
            {
                ContentResolver favoriteContentResolver = getContentResolver();
                int numRowsDeleted =  favoriteContentResolver.delete(MoviesContract.MovieEntry.
                                buildMovieUriWithID(Long.parseLong(movies.getMovieID())),
                        null, null);

                if (numRowsDeleted != 0)
                {
                    detailBinding.imgbtnFavorite.setImageResource(R.drawable.ic_favorite_border_black_48dp);
                    detailBinding.floatingButton.setImageResource(R.drawable.ic_favorite_border_black_48dp);
                    mCursorSize = 0;
                    showToast(getResources().getString(R.string.remove_favr));
                }
            }
            else
            {
                // Insert new favorite movie data via a ContentResolver
                ContentValues contentValues = new ContentValues();
                contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID,       movies.getMovieID());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movies.getOriginalTitle());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH,    movies.getPosterPath());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW,       movies.getOverview());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,   movies.getVoteAverage());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,   movies.getReleaseDate());
                contentValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH,  movies.getBackdropPath());
                // Insert the content values via a ContentResolver
                Uri resultsUri = getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, contentValues);

                if(resultsUri != null)
                {
                    detailBinding.imgbtnFavorite.setImageResource(R.drawable.ic_favorite_red_500_48dp);
                    detailBinding.floatingButton.setImageResource(R.drawable.ic_favorite_red_500_48dp);
                    mCursorSize = 1;
                    showToast(getResources().getString(R.string.add_favr));
                }
            }
        }
    };



    private void configureFavoriteButton()
    {
        if (  mCursorSize != 0)
        {
            detailBinding.imgbtnFavorite.setImageResource(R.drawable.ic_favorite_red_500_48dp);
            detailBinding.floatingButton.setImageResource(R.drawable.ic_favorite_red_500_48dp);
        }
        else
        {
            detailBinding.imgbtnFavorite.setImageResource(R.drawable.ic_favorite_border_black_48dp);
            detailBinding.floatingButton.setImageResource(R.drawable.ic_favorite_border_black_48dp);
        }
    }



    private final View.OnClickListener shareButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent shareIntent = createShareMovieIntent();
            startActivity(shareIntent);
        }
    };



    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing.  All we need
     * to do is set the type, text and the NEW_DOCUMENT flag so it treats our share as a new task.
     */
    private Intent createShareMovieIntent()
    {
        Trailers trailers = trailersList.get(0);
        String mUrl = YOUTUBE + trailers.getKey();

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setSubject(getResources().getString(R.string.txt_trailers_label))
                .setText(mUrl)
                .getIntent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }
        return shareIntent;
    }



    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_KEY_TRAILERS, trailersList);
        outState.putParcelableArrayList(STATE_KEY_REVIEWS,  reviewsList);
        outState.putParcelable(STATE_KEY_MOVIES, movies);
        outState.putInt(STATE_KEY_FAVORITE, mCursorSize);
    }



    private void makeTheMovieDbSearchQuery()
    {

        getSupportLoaderManager().initLoader(ID_FAVORITES_LOADER, null, this);

        // creating connection detector class instance
        InternetConnectionDetector cd = new InternetConnectionDetector(getApplicationContext());
        Boolean isInternetPresent = cd.isConnectingToInternet();

        if(!isInternetPresent)
        {
            //Toast.makeText(this, getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
            detailBinding.trailersInfo.txtTrailerLabel.setVisibility(View.GONE);
            detailBinding.trailersInfo.recyclerviewTrailers.setVisibility(View.GONE);
            detailBinding.reviewsInfo.txtReviewsLabel.setVisibility(View.GONE);
            detailBinding.reviewsInfo.recyclerviewRevies.setVisibility(View.GONE);
            return;
        }

        trailerAdapter = new TrailerAdapter(this, this);
        detailBinding.trailersInfo.recyclerviewTrailers.setAdapter(trailerAdapter);


        reviewsAdapter = new ReviewsAdapter(this, this);
        detailBinding.reviewsInfo.recyclerviewRevies.setAdapter(reviewsAdapter);


        // Initialize the loader
        getSupportLoaderManager().initLoader(THE_TRAILER_LOADER,  null, this);
        getSupportLoaderManager().initLoader(THE_REVIEW_LOADER,   null, this);

        String PARAM_VIDEOS = "videos";
        URL TrailersUrl = NetworkUtils.buildUrlVideosReviews(movies.getMovieID(), PARAM_VIDEOS);
        Bundle queryVideosBundle = new Bundle();
        queryVideosBundle.putString(QUERY_URL_VIDEOS, TrailersUrl.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> trailerLoader = loaderManager.getLoader(THE_TRAILER_LOADER);
        if (trailerLoader == null)
        {
            loaderManager.initLoader(THE_TRAILER_LOADER, queryVideosBundle, this);
        }
        else
        {
            loaderManager.restartLoader(THE_TRAILER_LOADER, queryVideosBundle, this);
        }


        String PARAM_REVIEWS = "reviews";
        URL ReviewsUrl = NetworkUtils.buildUrlVideosReviews(movies.getMovieID(), PARAM_REVIEWS);
        Bundle queryReviewsBundle = new Bundle();
        queryReviewsBundle.putString(QUERY_URL_REVIEWS, ReviewsUrl.toString());

        Loader<String> reviewsLoader = loaderManager.getLoader(THE_REVIEW_LOADER);
        if (reviewsLoader == null)
        {
            loaderManager.initLoader(THE_REVIEW_LOADER, queryReviewsBundle, this);
        }
        else
        {
            loaderManager.restartLoader(THE_REVIEW_LOADER, queryReviewsBundle, this);
        }
    }


    @NonNull
    @Override
    public Loader onCreateLoader(int id, final Bundle args)
    {
        switch (id)
        {
            case ID_FAVORITES_LOADER:
                return new CursorLoader(this,
                        MoviesContract.MovieEntry.buildMovieUriWithID(Long.parseLong(movies.getMovieID())),
                                        null,
                                        null,
                                        null,
                                        null);

            case THE_TRAILER_LOADER:

                return new AsyncTaskLoader<String>(this)
                {
                    String mTrailersJson;

                    @Override
                    protected void onStartLoading()
                    {
                        if (args == null)
                        {
                            return;
                        }

                        if (mTrailersJson != null)
                        {
                            deliverResult(mTrailersJson);
                        }
                        else
                        {
                            forceLoad();
                        }
                    }

                    @Override
                    public String loadInBackground()
                    {
                        String searchQueryUrlString = args.getString(QUERY_URL_VIDEOS);
                        try
                        {
                            URL TrailersUrl = new URL(searchQueryUrlString);
                            return NetworkUtils.getResponseFromHttpUrl(TrailersUrl);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public void deliverResult(String TrailersJson)
                    {
                        mTrailersJson = TrailersJson;
                        super.deliverResult(TrailersJson);
                    }
                };

            case THE_REVIEW_LOADER:

                return new AsyncTaskLoader<String>(this)
                {
                    String mReviewsJson;

                    @Override
                    protected void onStartLoading()
                    {
                        if (args == null) {
                            return;
                        }

                        if (mReviewsJson != null)
                        {
                            deliverResult(mReviewsJson);
                        }
                        else
                        {
                            forceLoad();
                        }
                    }

                    @Override
                    public String loadInBackground()
                    {
                        String searchQueryUrlString = args.getString(QUERY_URL_REVIEWS);

                        try
                        {
                            URL ReviewsUrl = new URL(searchQueryUrlString);
                            return NetworkUtils.getResponseFromHttpUrl(ReviewsUrl);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public void deliverResult(String ReviewsJson)
                    {
                        mReviewsJson = ReviewsJson;
                        super.deliverResult(ReviewsJson);
                    }
                };

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data)
    {
        if (null == data)
        {
            //Toast.makeText(this, getResources().getString(R.string.error_message), Toast.LENGTH_LONG).show();
        }
        else
        {
            switch (loader.getId())
            {
                case ID_FAVORITES_LOADER:
                    mCursor = (Cursor) data;
                    mCursorSize = mCursor.getCount();
                    configureFavoriteButton();
                    break;

                case THE_TRAILER_LOADER:
                trailersList = JsonUtils.parseTrailersJson(data.toString());
                if (trailersList.size() == 0)
                {
                    detailBinding.trailersInfo.txtTrailerLabel.setVisibility(View.GONE);
                    detailBinding.trailersInfo.recyclerviewTrailers.setVisibility(View.GONE);
                }
                else
                {
                    detailBinding.imgbtnShare.setVisibility(View.VISIBLE);
                    trailerAdapter.swapMovie(trailersList);
                }
                break;

                case THE_REVIEW_LOADER:
                    reviewsList = JsonUtils.parseReviewsJson(data.toString());
                    if (reviewsList.size() == 0)
                    {
                        detailBinding.reviewsInfo.txtReviewsLabel.setVisibility(View.GONE);
                        detailBinding.reviewsInfo.recyclerviewRevies.setVisibility(View.GONE);
                    }
                    else
                    {
                        reviewsAdapter.swapMovie(reviewsList);
                    }
                    break;

                default:
                    throw new RuntimeException("Loader Not Implemented: " + loader.getId());
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader)
    {
        trailerAdapter.swapMovie(null);
        reviewsAdapter.swapMovie(null);
        mCursor = null;
        mCursorSize =0;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClickTrailer(int clickedItemIndex)
    {
        Trailers trailers = trailersList.get(clickedItemIndex);
        String url = YOUTUBE + trailers.getKey();
        openWebPage(url);
    }

    @Override
    public void onClickReview(int clickedItemIndex)
    {
        Reviews reviews = reviewsList.get(clickedItemIndex);
        String url = reviews.getUrl();
        openWebPage(url);
    }


    /**
     * This method fires off an implicit Intent to open a webpage.
     *
     * @param url Url of webpage to open.
     */
    private void openWebPage(String url)
    {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        /*
         * This is a check we perform with every implicit Intent that we launch. In some cases,
         * the device where this code is running might not have an Activity to perform the action
         * with the data we've specified. Without this check, in those cases your app would crash.
         */
        if (intent.resolveActivity(getPackageManager()) != null)
        {
            startActivity(intent);
        }
    }



    private void showToast(String message)
    {
        if (mToast != null)
        {
            mToast.cancel();
        }

        LayoutInflater inflater = getLayoutInflater();
        View view  = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView text = view.findViewById(R.id.text);
        text.setText(message);

        mToast = new Toast(getApplicationContext());
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setView(view);
        mToast.show();
    }


}
