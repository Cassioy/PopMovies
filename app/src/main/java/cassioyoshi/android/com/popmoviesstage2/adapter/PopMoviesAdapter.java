package cassioyoshi.android.com.popmoviesstage2.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

import cassioyoshi.android.com.popmoviesstage2.PopMovies;
import cassioyoshi.android.com.popmoviesstage2.PopMoviesDetails;
import cassioyoshi.android.com.popmoviesstage2.R;

/**
 * Created by cassioimamura on 2/4/17.
 */

public class PopMoviesAdapter extends RecyclerView.Adapter<PopMoviesAdapter.Viewholder> implements Serializable{

    private static final String LOG_TAG = PopMoviesAdapter.class.getSimpleName();
    private static Context mContext;
    public List<PopMovies> mPopMoviesList;

    private static final int VIEW_TYPE_DATA = 1;
    private static final int VIEW_TYPE_EMPTY = 2;

    /** This is a custom constructor **/

    public PopMoviesAdapter(Context context, List<PopMovies> popMoviesList){

        this.mContext = context;
        this.mPopMoviesList = popMoviesList;

    }

    public class Viewholder extends RecyclerView.ViewHolder {
        public ImageView iconView;


        public Viewholder(View itemView) {
            super( itemView );
             iconView = (ImageView) itemView.findViewById( R.id.thumbnail_image);
        }
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.pop_movies_item_grid, parent, false);
        Viewholder holder = new Viewholder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(Viewholder holder, final int position) {

        String url = mPopMoviesList.get(position).getPosterSource();
        Picasso.with(mContext).load(url.toString()).into(holder.iconView);
        holder.iconView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailsIntent = new Intent( mContext, PopMoviesDetails.class );
                detailsIntent.putExtra( "backdropImage", mPopMoviesList.get( position ).backdropSource );
                detailsIntent.putExtra( "posterImage", mPopMoviesList.get( position ).posterSource );
                detailsIntent.putExtra( "title", mPopMoviesList.get( position ).mTitle );
                detailsIntent.putExtra( "plotSynopsis", mPopMoviesList.get( position ).mPlotSynopsis );
                detailsIntent.putExtra( "releaseDate", mPopMoviesList.get( position ).mReleaseDate );
                detailsIntent.putExtra( "voteAvg", mPopMoviesList.get( position ).mVoteAvg );
                detailsIntent.putExtra( "id", mPopMoviesList.get( position ).mId );
                mContext.startActivity( detailsIntent );
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPopMoviesList.size();
    }

}

