package com.interview.project.moviesapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.interview.project.moviesapp.model.Trailers;

import java.util.List;


public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder>
{
    private final Context mContext;
    final private TrailerAdapterOnClickHandler mClickHandler;
    private List<Trailers> mTrailers;

    /**
     * The interface that receives onClick messages.
     */
    public interface TrailerAdapterOnClickHandler
    {
        void onClickTrailer(int clickedItemIndex);
    }


    /**
     * Creates a TrailerAdapter.
     *
     * @param context      Used to talk to the UI and app resources
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public TrailerAdapter(@NonNull Context context, TrailerAdapterOnClickHandler clickHandler)
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
     * @return A new TrailerAdapterViewHolder that holds the View for each list item
     */
    @NonNull
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        int layoutId = R.layout.trailers_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutId, viewGroup, false);
        view.setFocusable(true);

        return new TrailerAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the movie
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param trailerAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull TrailerAdapterViewHolder trailerAdapterViewHolder, int position)
    {
      trailerAdapterViewHolder.trailerName.setText(mTrailers.get(position).getName());
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
        if (null == mTrailers) return 0;
        return mTrailers.size();
    }


    /**
     * Swaps the Movies used by the TrailerAdapter for its movie data. This method is called by
     * MainActivity after a load has finished, as well as when the Loader responsible for loading
     * the movie data is reset. When this method is called, we assume we have a completely new
     * set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param newTrailers the new cursor to use as ForecastAdapter's data source
     */
    void swapMovie(List<Trailers> newTrailers)
    {
        mTrailers = newTrailers;
        notifyDataSetChanged();
    }



    /**
     * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
     * a cache of the child views for a forecast item. It's also a convenient place to set an
     * OnClickListener, since it has access to the adapter and the views.
     */
    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final TextView trailerName;

        TrailerAdapterViewHolder(View view)
        {
            super(view);

            trailerName = view.findViewById(R.id.txt_trailer_name);
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
            mClickHandler.onClickTrailer(adapterPosition);
        }
    }
}

