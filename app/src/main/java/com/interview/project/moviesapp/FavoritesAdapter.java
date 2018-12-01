package com.interview.project.moviesapp;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesAdapterViewHolder>
{
    private final Context mContext;
    final private FavoritesAdapterOnClickHandler mClickHandler;
    private Cursor mCursor;

    /**
     * The interface that receives onClick messages.
     */
    public interface FavoritesAdapterOnClickHandler
    {
        void onClickFavoritesAdapter(int clickedItemIndex);
    }


    /**
     * Creates a FavoritesAdapter.
     *
     * @param context      Used to talk to the UI and app resources
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public FavoritesAdapter(@NonNull Context context, FavoritesAdapterOnClickHandler clickHandler)
    {
        mContext = context;
        mClickHandler = clickHandler;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (like ours does) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new FavoritesAdapterViewHolder that holds the View for each list item
     */
    @NonNull
    @Override
    public FavoritesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        int layoutId = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutId, viewGroup, false);
        view.setFocusable(true);

        return new FavoritesAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the movie
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param favoritesAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull FavoritesAdapterViewHolder favoritesAdapterViewHolder, int position)
    {
        mCursor.moveToPosition(position);

        Picasso.get()
                .load(mCursor.getString(MainScreenFragment.INDEX_POSTER_PATH))
                .placeholder(R.drawable.user_placeholder)
                .error(R.drawable.user_placeholder_error)
                .into(favoritesAdapterViewHolder.posterView);

    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount()
    {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }


    /**
     * Swaps the Movies used by the FavoritesAdapter for its movie data. This method is called by
     * MainActivity after a load has finished, as well as when the Loader responsible for loading
     * the movie data is reset. When this method is called, we assume we have a completely new
     * set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param newCursor the new cursor to use as ForecastAdapter's data source
     */
    void swapMovie(Cursor newCursor)
    {
        mCursor = newCursor;
        notifyDataSetChanged();
    }



    /**
     * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
     * a cache of the child views for a forecast item. It's also a convenient place to set an
     * OnClickListener, since it has access to the adapter and the views.
     */
    class FavoritesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final ImageView posterView;

        FavoritesAdapterViewHolder(View view)
        {
            super(view);

            posterView = view.findViewById(R.id.movie_image);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v the View that was clicked
         */
        @Override
        public void onClick(View v)
        {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClickFavoritesAdapter(adapterPosition);
        }
    }
}

