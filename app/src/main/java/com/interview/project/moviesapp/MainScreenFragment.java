package com.interview.project.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.interview.project.moviesapp.model.Movies;
import com.interview.project.moviesapp.model.MoviesContract;
import com.interview.project.moviesapp.utilities.InternetConnectionDetector;
import com.interview.project.moviesapp.utilities.JsonUtils;
import com.interview.project.moviesapp.utilities.NetworkUtils;
import com.interview.project.moviesapp.utilities.Utility;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks,
        MoviesAdapter.MoviesAdapterOnClickHandler,
        FavoritesAdapter.FavoritesAdapterOnClickHandler
{

    private static final String[] MAIN_MOVIES_PROJECTION =
    {
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MoviesContract.MovieEntry.COLUMN_POSTER_PATH,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW,
            MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH
    };

    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    private static final int INDEX_MOVIE_ID       = 0;
    private static final int INDEX_ORIGINAL_TITLE = 1;
    public  static final int INDEX_POSTER_PATH    = 2;
    private static final int INDEX_OVERVIEW       = 3;
    private static final int INDEX_VOTE_AVERAGE   = 4;
    private static final int INDEX_RELEASE_DATE   = 5;
    private static final int INDEX_BACKDROP_PATH  = 6;

    /*
     * This number will uniquely identify our Loader and is chosen arbitrarily.
     */

    private static final int THE_MOVIE_DB_LOADER = 22;
    private static final int ID_FAVORITES_LOADER = 44;

    private static final String ARG_PAGE  = "ARG_PAGE";
    private static final String STATE_KEY = "state_key";
    private static final String QUERY_URL = "query";

    private RecyclerView mRecyclerView;
    private TextView error_message_display;
    private ProgressBar pb_loading_indicator;

    private MoviesAdapter moviesAdapter;
    private FavoritesAdapter favoritesAdapter;
    private ArrayList<Movies> moviesList;
    private Cursor mCursor;
    private String SORT_PARAM;
    private int category_id = 0;
    private Context mContext;

    public static MainScreenFragment newInstance(int page)
    {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        MainScreenFragment fragment = new MainScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MainScreenFragment()
    { }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null)
        {
            category_id = getArguments().getInt(ARG_PAGE);
        }

        switch (category_id)
        {
            case 0 :
                SORT_PARAM = getString(R.string.pref_sort_popular_value);
                break;

            case 1 :
                SORT_PARAM = getString(R.string.pref_sort_top_rated_value);
                break;

            case 2 :
                SORT_PARAM = getString(R.string.pref_sort_favorites_value);
                break;

            case 3 :
                SORT_PARAM = getString(R.string.pref_sort_upcoming_value);
                break;

            case 4 :
                SORT_PARAM = getString(R.string.pref_sort_now_playing_value);
                break;

            default :
                SORT_PARAM = getString(R.string.pref_sort_popular_value);
                break;
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View mRootView = inflater.inflate(R.layout.fragment_main_movies, container, false);

        mRecyclerView         = mRootView.findViewById(R.id.recyclerview_movies);
        error_message_display = mRootView.findViewById(R.id.error_message_display);
        pb_loading_indicator  = mRootView.findViewById(R.id.pb_loading_indicator);

        mContext = getActivity();

        int mNoOfColumns = Utility.calculateNoOfColumns(mContext);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, mNoOfColumns);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);



        if (savedInstanceState != null && !SORT_PARAM.equals(getString(R.string.pref_sort_favorites_value)))
        {
            moviesAdapter = new MoviesAdapter(mContext, this);
            mRecyclerView.setAdapter(moviesAdapter);
            moviesList = savedInstanceState.getParcelableArrayList(STATE_KEY);
            moviesAdapter.swapMovie(moviesList);
        }
        else
        {
            makeTheMovieDbSearchQuery();
        }

        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_KEY, moviesList);
    }



    private void makeTheMovieDbSearchQuery()
    {
        if (SORT_PARAM.equals(getString(R.string.pref_sort_favorites_value)))
        {
            favoritesAdapter = new FavoritesAdapter(mContext, this);
            mRecyclerView.setAdapter(favoritesAdapter);
            getLoaderManager().initLoader(ID_FAVORITES_LOADER, null, this);
        }
        else
        {
            // creating connection detector class instance
            InternetConnectionDetector cd = new InternetConnectionDetector(mContext);
            Boolean isInternetPresent = cd.isConnectingToInternet();

            if(!isInternetPresent)
            {
                showErrorMessage();
                Toast.makeText(mContext, getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                return;
            }

            moviesAdapter = new MoviesAdapter(mContext, this);
            mRecyclerView.setAdapter(moviesAdapter);

            // Initialize the loader
            getLoaderManager().initLoader(THE_MOVIE_DB_LOADER, null, this);

            URL TheMovieDbSearchUrl = NetworkUtils.buildUrl(SORT_PARAM);
            Bundle queryBundle = new Bundle();
            queryBundle.putString(QUERY_URL, TheMovieDbSearchUrl.toString());

            LoaderManager loaderManager = getLoaderManager();
            Loader<String> theMovieDbLoader = loaderManager.getLoader(THE_MOVIE_DB_LOADER);
            if (theMovieDbLoader == null)
            {
                loaderManager.initLoader(THE_MOVIE_DB_LOADER, queryBundle, this);
            }
            else
            {
                loaderManager.restartLoader(THE_MOVIE_DB_LOADER, queryBundle, this);
            }
        }
    }


    private void showJsonDataView()
    {
        error_message_display.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    private void showErrorMessage()
    {
        mRecyclerView.setVisibility(View.INVISIBLE);
        error_message_display.setVisibility(View.VISIBLE);
    }


    @NonNull
    @Override
    public Loader onCreateLoader(int loaderId, final Bundle args)
    {
        switch (loaderId) {

            case THE_MOVIE_DB_LOADER:

                return new AsyncTaskLoader<String>(mContext)
                {
                    // Create a String member variable called mTheMovieDbJson that will store the raw JSON
                    String mTheMovieDbJson;

                    @Override
                    protected void onStartLoading()
                    {
                        /* If no arguments were passed, we don't have a query to perform. Simply return. */
                        if (args == null)
                        {
                            return;
                        }

                        /*
                         * When we initially begin loading in the background, we want to display the
                         * loading indicator to the user
                         */
                        pb_loading_indicator.setVisibility(View.VISIBLE);

                        // If mTheMovieDbJson is not null, deliver that result. Otherwise, force a load
                        /*
                         * If we already have cached results, just deliver them now. If we don't have any
                         * cached results, force a load.
                         */
                        if (mTheMovieDbJson != null)
                        {
                            deliverResult(mTheMovieDbJson);
                        }
                        else
                        {
                            forceLoad();
                        }
                    }

                    @Override
                    public String loadInBackground()
                    {
                        /* Extract the query from the args using our constant */
                        String searchQueryUrlString = args.getString(QUERY_URL);

                       /* Parse the URL from the passed in String and perform the search */
                        try
                        {
                            URL TheMovieDbUrl = new URL(searchQueryUrlString);
                            return NetworkUtils.getResponseFromHttpUrl(TheMovieDbUrl);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public void deliverResult(String TheMovieDbJson)
                    {
                        mTheMovieDbJson = TheMovieDbJson;
                        super.deliverResult(TheMovieDbJson);
                    }
                };

            case ID_FAVORITES_LOADER:
                /* URI for all rows of movie data in our movies table */
                Uri moviesQueryUri = MoviesContract.MovieEntry.CONTENT_URI;

                String sortOrder =  MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
                /*
                 * A SELECTION in SQL declares which rows you'd like to return. In our case, we
                 * want all movie data. We created a handy method to do that in our MoviesEntry class.
                 */

                return new CursorLoader(mContext,
                        moviesQueryUri,
                        MAIN_MOVIES_PROJECTION,
                        null,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }


    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data)
    {
        // When we finish loading, we want to hide the loading indicator from the user.
        pb_loading_indicator.setVisibility(View.INVISIBLE);

        //If the results are null, we assume an error has occurred.
        if (null == data)
        {
            showErrorMessage();
        }
        else
        {
            switch (loader.getId())
            {
                case THE_MOVIE_DB_LOADER:
                    showJsonDataView();
                    moviesList = JsonUtils.parseMoviesJson(data.toString());
                    moviesAdapter.swapMovie(moviesList);
                    break;

                case ID_FAVORITES_LOADER:
                    favoritesAdapter.swapMovie((Cursor) data);
                    if (((Cursor) data).getCount() != 0) showJsonDataView();
                    mCursor = (Cursor) data;
                    break;

                default:
                    throw new RuntimeException("Loader Not Implemented: " + loader.getId());
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader)
    {
        switch (loader.getId())
        {
            case THE_MOVIE_DB_LOADER:
                moviesAdapter.swapMovie(null);
                break;

            case ID_FAVORITES_LOADER:
                favoritesAdapter.swapMovie(null);
                break;

            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
    }


    @Override
    public void onClickMovieAdapter(int clickedItemIndex)
    {
        Movies movies = moviesList.get(clickedItemIndex);
        Intent intent = new Intent(mContext, MovieDetailActivity.class);
        intent.putExtra("movies",movies);
        startActivity(intent);
    }

    @Override
    public void onClickFavoritesAdapter(int clickedItemIndex)
    {
        mCursor.moveToPosition(clickedItemIndex);

        Movies mFilm = new Movies();
        mFilm.setMovieID(mCursor.getString(MainScreenFragment.INDEX_MOVIE_ID));
        mFilm.setOriginalTitle(mCursor.getString(MainScreenFragment.INDEX_ORIGINAL_TITLE));
        mFilm.setPosterPath(mCursor.getString(MainScreenFragment.INDEX_POSTER_PATH));
        mFilm.setOverview(mCursor.getString(MainScreenFragment.INDEX_OVERVIEW));
        mFilm.setVoteAverage(mCursor.getString(MainScreenFragment.INDEX_VOTE_AVERAGE));
        mFilm.setReleaseDate(mCursor.getString(MainScreenFragment.INDEX_RELEASE_DATE));
        mFilm.setBackdropPath(mCursor.getString(MainScreenFragment.INDEX_BACKDROP_PATH));

        Intent intent = new Intent(mContext, MovieDetailActivity.class);
        intent.putExtra("movies",mFilm);
        startActivity(intent);
    }
}