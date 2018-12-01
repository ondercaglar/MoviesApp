package com.interview.project.moviesapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.interview.project.moviesapp.model.Reviews;

import java.util.List;


public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder>
{
    private final Context mContext;
    final private ReviewsAdapterOnClickHandler mClickHandler;
    private List<Reviews> mReviews;

    /**
     * The interface that receives onClick messages.
     */
    public interface ReviewsAdapterOnClickHandler
    {
        void onClickReview(int clickedItemIndex);
    }


    /**
     * Creates a ReviewsAdapter.
     *
     * @param context      Used to talk to the UI and app resources
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public ReviewsAdapter(@NonNull Context context, ReviewsAdapterOnClickHandler clickHandler)
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
     * @return A new ReviewsAdapterViewHolder that holds the View for each list item
     */
    @NonNull
    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        int layoutId = R.layout.reviews_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutId, viewGroup, false);
        view.setFocusable(true);

        return new ReviewsAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the movie
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param reviewsAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapterViewHolder reviewsAdapterViewHolder, int position)
    {
      reviewsAdapterViewHolder.reviewAuthor.setText(mReviews.get(position).getAuthor());
      reviewsAdapterViewHolder.reviewContent.setText(mReviews.get(position).getContent());
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
        if (null == mReviews) return 0;
        return mReviews.size();
    }


    /**
     * Swaps the Movies used by the ReviewsAdapter for its movie data. This method is called by
     * MainActivity after a load has finished, as well as when the Loader responsible for loading
     * the movie data is reset. When this method is called, we assume we have a completely new
     * set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param newReviews the new cursor to use as ForecastAdapter's data source
     */
    void swapMovie(List<Reviews> newReviews)
    {
        mReviews = newReviews;
        notifyDataSetChanged();
    }



    /**
     * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
     * a cache of the child views for a forecast item. It's also a convenient place to set an
     * OnClickListener, since it has access to the adapter and the views.
     */
    class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final TextView reviewAuthor;
        final TextView reviewContent;

        ReviewsAdapterViewHolder(View view)
        {
            super(view);

            reviewAuthor  = view.findViewById(R.id.txt_review_author);
            reviewContent = view.findViewById(R.id.txt_review_content);
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
            mClickHandler.onClickReview(adapterPosition);
        }
    }
}

