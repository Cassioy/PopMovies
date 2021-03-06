package cassioyoshi.android.com.popmoviesstage2.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import cassioyoshi.android.com.popmoviesstage2.PopMoviesDetails;
import cassioyoshi.android.com.popmoviesstage2.PopMoviesDetailsFragment;
import cassioyoshi.android.com.popmoviesstage2.PopMoviesFragment;
import cassioyoshi.android.com.popmoviesstage2.PopMoviesMainActivity;
import cassioyoshi.android.com.popmoviesstage2.R;

/**
 * Created by cassioimamura on 10/15/17.
 */

public class FavoritesCursorAdapter extends RecyclerView.Adapter<FavoritesCursorAdapter.FavoritesCursorAdapterViewHolder>{

    private final Context mContext;

    private Cursor mCursor;

    private FavoritesCursorAdapterViewHolder holder;

    private boolean mTwoPane;

    private static final String posterSource = "posterImage";
    private static final String backdropSource = "backdropImage";
    private static final String mTitle = "title";
    private static final String mPlotSynopsis = "plotSynopsis";
    private static final String mVoteAvg = "voteAvg";
    private static final String mReleaseDate = "releaseDate";
    private static final String mId = "id";


    class FavoritesCursorAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView favThumbView;


        FavoritesCursorAdapterViewHolder(View view, Context context) {
            super(view);

            favThumbView = (ImageView) itemView.findViewById( R.id.favorite_thumbnail);
            //Tag in sw600dp layouts for twoPane Layouts
            if(favThumbView.getTag() != null) {
                mTwoPane = true;
            }else{
                mTwoPane = false;
            }
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition( adapterPosition );

            final String movieTitle = mCursor.getString( PopMoviesFragment.INDEX_MOVIE_TITLE );
            final String synopsis = mCursor.getString( PopMoviesFragment.INDEX_PLOT_SYNOPSIS );
            final String releaseDate = mCursor.getString( PopMoviesFragment.INDEX_RELEASE_DATE );
            final String avgVote = mCursor.getString( PopMoviesFragment.INDEX_VOTE_AVG );
            final String movieId = mCursor.getString( PopMoviesFragment.INDEX_MOVIE_ID );
            final String backdropImage = mCursor.getString( PopMoviesFragment.INDEX_BACKDROP_IMAGE );
            final String posterImage = mCursor.getString( PopMoviesFragment.INDEX_POSTER_IMAGE );

            Bundle cursorBundle = new Bundle();
            cursorBundle.putString( mId, movieId );
            cursorBundle.putString( backdropSource, backdropImage );
            cursorBundle.putString( posterSource, posterImage );
            cursorBundle.putString( mTitle, movieTitle );
            cursorBundle.putString( mPlotSynopsis, synopsis );
            cursorBundle.putString( mReleaseDate, releaseDate );
            cursorBundle.putString( mVoteAvg, avgVote );

            if(mTwoPane == true) {

                PopMoviesDetailsFragment detailsFragment = new PopMoviesDetailsFragment();
                detailsFragment.setArguments( cursorBundle );

                FragmentManager fragmentManager = ((PopMoviesMainActivity) mContext).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace( R.id.frag_details, detailsFragment )
                        .commit();

            }else{

                Intent detailsIntent = new Intent( mContext, PopMoviesDetails.class );
                detailsIntent.putExtras( cursorBundle );
                mContext.startActivity( detailsIntent );
            }

        }
    }

    public FavoritesCursorAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
    }

    @Override
    public FavoritesCursorAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.favorites_item_grid, parent, false);


        holder = new FavoritesCursorAdapterViewHolder(v, mContext);
        v.setFocusable(true);

        return holder;    }

    @Override
    public void onBindViewHolder(final FavoritesCursorAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        final String posterImage;

        posterImage = mCursor.getString( PopMoviesFragment.INDEX_POSTER_IMAGE);

        Picasso.with(mContext).load(posterImage)
                .into(holder.favThumbView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d( "Sucesso", "Carregou imagem" );
                    }

                    @Override
                    public void onError() {
                        Log.d( "blehh", "Nao Carregou imagem" );


                    }
                });

    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }


    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }


}


