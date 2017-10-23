package cassioyoshi.android.com.popmoviesstage2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import cassioyoshi.android.com.popmoviesstage2.adapter.ReviewsAdapter;
import cassioyoshi.android.com.popmoviesstage2.adapter.TrailerAdapter;
import cassioyoshi.android.com.popmoviesstage2.data.MovieContract;
import cassioyoshi.android.com.popmoviesstage2.data.model.Result;
import cassioyoshi.android.com.popmoviesstage2.data.model.ReviewData;
import cassioyoshi.android.com.popmoviesstage2.data.model.ReviewList;
import cassioyoshi.android.com.popmoviesstage2.data.model.Video;
import cassioyoshi.android.com.popmoviesstage2.retrofit.RetrofitStart;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static cassioyoshi.android.com.popmoviesstage2.R.id.plot_synopsis;
import static cassioyoshi.android.com.popmoviesstage2.R.id.release_date;

/**
 * Created by cassioimamura on 10/20/17.
 */

public class PopMoviesDetailsFragment extends Fragment {

    private RecyclerView mRecyclerViewTrailer;
    private RecyclerView mRecyclerViewReviews;
    private RecyclerView.Adapter mAdapter;
    private ReviewsAdapter mReviewsAdapter;
    private DividerItemDecoration mDividerItemDecoration;
    private List<Result> trailersList;
    private List<ReviewList> reviewDescription;
    private Context mContext;
    private String totalReviews;
    private int reviewNumber;
    private int id;
    private PopMovies favMovie;
    private TextView noTrailer;
    private TextView noInternet;
    private TextView noReview;
    private Button expandMoreLess;
    private Animation animationUp;
    private Animation animationDown;

    public LinearLayoutManager linearLayoutManager
            = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);

    public LinearLayoutManager verticalLayoutManager
            = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);

    public PopMoviesDetailsFragment(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach( context );
    }

    public static  PopMoviesDetailsFragment newInstance(int index){
        PopMoviesDetailsFragment f = new PopMoviesDetailsFragment();

        Bundle args = new Bundle();
        f.setArguments( args );

        return f;
    }

    public int getShownIndex(){
        return getArguments().getInt("index, 0");
    }
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        final View detailsView = inflater.inflate( R.layout.details_pop_movies, container, false );
        Bundle args = getArguments();
        mContext = detailsView.getContext();


        String thumbnailUrl = args.getString("posterImage");
        String imageUrl = args.getString("backdropImage");
        String title = args.getString("title");
        String synopsis = args.getString("plotSynopsis");
        String released = args.getString( "releaseDate" );
        String votes = args.getString( "voteAvg" );
        String video_id = args.getString( "id" );
        if(video_id != null) {
            id = Integer.parseInt( video_id );
        }

        detailsView.setBackgroundColor(0);

        favMovie = new PopMovies( thumbnailUrl, imageUrl, title, synopsis, votes, released, video_id );
        mRecyclerViewTrailer = (RecyclerView) detailsView.findViewById( R.id.horizontal_recycler_view );
        mRecyclerViewReviews = (RecyclerView) detailsView.findViewById( R.id.review_recycler_view );
        expandMoreLess = (Button) detailsView.findViewById( R.id.showMoreLess );
        noReview = (TextView) detailsView.findViewById( R.id.no_review );


        if(video_id == null) {
            Log.d( TAG, "onCreateView: " + favMovie);
            detailsView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            return detailsView;
        } else {
            Log.v( "verificando id", " " + id );



// language is set to en-US and page is set to 1, API_KEY is found on config.xml resource not included in this project

            Call<ReviewData> callReview = new RetrofitStart().getMovie()
                    .requestReviews( id, getString( R.string.API_KEY ), getString( R.string.LANGUAGE ), Integer.parseInt( getString( R.string.PAGE ) ) );
            callReview.enqueue( new Callback<ReviewData>() {
                @Override
                public void onResponse(Call<ReviewData> callReview, Response<ReviewData> responseReview) {

                    try {
                        int statusCode = responseReview.code();
                        ReviewData reviewData = responseReview.body();
                        reviewNumber = reviewData.getTotalResults();
                        reviewDescription = reviewData.getResults();

                        Button reviewBtn = (Button) getActivity().findViewById( R.id.review );

                        Log.v( "verificando", "Results Review" + statusCode );
                        Log.v( "verificando", "Total Resultados " + reviewNumber );


                        reviewBtn.setText( String.valueOf( reviewNumber ) );
                        mRecyclerViewReviews.setVisibility( View.VISIBLE );

                        mReviewsAdapter = new ReviewsAdapter( mContext, reviewDescription );

                        mRecyclerViewReviews.setHasFixedSize( true );
                        mRecyclerViewReviews.setLayoutManager( verticalLayoutManager );
                        mDividerItemDecoration = new DividerItemDecoration( mRecyclerViewReviews.getContext(),
                                verticalLayoutManager.getOrientation() );
                        mRecyclerViewReviews.addItemDecoration( mDividerItemDecoration );
                        mRecyclerViewReviews.setAdapter( mReviewsAdapter );
                        mRecyclerViewReviews.setNestedScrollingEnabled( false );

                        if (reviewNumber > 0) {
                            expandMoreLess.setVisibility( View.VISIBLE );
                            noReview.setVisibility( View.GONE );
                        }

                    } catch (Exception e) {
                        Log.e( "onFailure", "Requisicao detalhes vazia..." );

                    }
                }

                @Override
                public void onFailure(Call<ReviewData> callReview, Throwable t) {
                    Log.e( "onFailure", "Requisicao detalhes falhou... " );


                }

                ;
            } );

            Call<Video> call = new RetrofitStart().getMovie()
                    .requestVideos( id, getString( R.string.API_KEY ), getString( R.string.LANGUAGE ), Integer.parseInt( getString( R.string.PAGE ) ) );
            call.enqueue( new Callback<Video>() {
                @Override
                public void onResponse(Call<Video> call, Response<Video> response) {

                    try {
                        int statusCode = response.code();
                        Video selectedVideo = response.body();
                        trailersList = selectedVideo.getResults();

                        Log.v( "verificando", "Results " + statusCode );
                        Log.v( "VideoObject", "Results " + selectedVideo );
                        Log.v( "List Results", "Results " + trailersList );

                        mAdapter = new TrailerAdapter( mContext, trailersList );

                        mRecyclerViewTrailer.setHasFixedSize( true );

                        mRecyclerViewTrailer.setLayoutManager( linearLayoutManager );
                        mRecyclerViewTrailer.setAdapter( mAdapter );

                    } catch (Exception e) {
                        Log.e( "onFailure", "Requisicao detalhes vazia..." );

                    }
                }

                @Override
                public void onFailure(Call<Video> call, Throwable t) {
                    Log.e( "onFailure", "Requisicao detalhes falhou... " );
                    if (isInternetOn()) {

                    } else {

                        Handler mainHandler = new Handler( Looper.getMainLooper() );
                        mainHandler.post( new Runnable() {
                            @Override
                            public void run() {
                                // code to interact with UI
                                mRecyclerViewTrailer.setVisibility( View.GONE );
                                noInternet = (TextView) detailsView.findViewById( R.id.verify_internet );
                                noTrailer = (TextView) detailsView.findViewById( R.id.no_trailer );
                                noInternet.setVisibility( View.VISIBLE );
                            }
                        } );
                    }
                }
            } );


            CollapsingToolbarLayout collapsingToolbar =
                    (CollapsingToolbarLayout) detailsView.findViewById( R.id.collapsing_toolbar );
            collapsingToolbar.setTitle( title );

            final ImageButton fab = (ImageButton) detailsView.findViewById( R.id.fab );


            ImageView thumb = (ImageView) detailsView.findViewById( R.id.movie_info_thumbnail );
            Picasso.with( detailsView.getContext() ).load( thumbnailUrl ).into( thumb );


            loadBackdrop( imageUrl, detailsView );

            if (checkDatabase( title )) {
                fab.setSelected( true );
                fab.setImageResource( R.drawable.ic_grade_yellow_48px );

            } else {
                fab.setSelected( false );
                fab.setImageResource( R.drawable.ic_grade_white_48px );
            }


            TextView overview = (TextView) detailsView.findViewById( plot_synopsis );
            overview.setText( synopsis );

            TextView releaseDate = (TextView) detailsView.findViewById( release_date );
            releaseDate.setText( released );


            Drawable red = ContextCompat.getDrawable( detailsView.getContext(), R.drawable.circle_3 );
            Drawable yellow = ContextCompat.getDrawable( detailsView.getContext(), R.drawable.circle_2 );
            Drawable green = ContextCompat.getDrawable( detailsView.getContext(), R.drawable.circle );

            animationUp = AnimationUtils.loadAnimation( detailsView.getContext(), R.anim.slide_up );
            animationDown = AnimationUtils.loadAnimation( detailsView.getContext(), R.anim.slide_down );

            expandMoreLess.setSelected( true );
            expandMoreLess.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandMoreLess.isSelected()) {
                        TransitionManager.beginDelayedTransition( mRecyclerViewReviews, new TransitionSet()
                                .addTransition( new ChangeBounds() ) );
                        LinearLayout.LayoutParams paramsTrue = new LinearLayout
                                .LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
                        mRecyclerViewReviews.setLayoutParams( paramsTrue );
                        expandMoreLess.setSelected( false );
                        expandMoreLess.setText( getString( R.string.show_less ) );
                        mRecyclerViewReviews.startAnimation( animationDown );
                    } else {
                        TransitionManager.beginDelayedTransition( mRecyclerViewReviews, new TransitionSet()
                                .addTransition( new ChangeBounds() ) );
                        LinearLayout.LayoutParams paramsFalse = new LinearLayout
                                .LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, 500 );
                        mRecyclerViewReviews.setLayoutParams( paramsFalse );
                        expandMoreLess.setSelected( true );
                        expandMoreLess.setText( getString( R.string.show_more ) );
                        mRecyclerViewReviews.startAnimation( animationUp );


                    }
                }
            } );


            Button rating = (Button) detailsView.findViewById( R.id.rating_button );
            double votedAvg = Double.parseDouble( votes );
            if (votedAvg < 4) {
                rating.setBackground( red );
                rating.setText( votes );
            } else if (votedAvg < 7) {
                rating.setBackground( yellow );
                rating.setText( votes );
            } else if (votedAvg >= 7) {
                rating.setBackground( green );
                rating.setText( votes );
            }

//            Acesso a pagina de reviews TBD
//            Button reviews = (Button) findViewById(R.id.review);


            fab.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fab.setImageResource( fab.isSelected() ? R.drawable.ic_grade_white_48px : R.drawable.ic_grade_yellow_48px );
                    if (!fab.isSelected()) {
                        addFavoriteMovie( favMovie );
                        Log.d( "adding Favorite Movie", "name: " + favMovie.mTitle );
                        fab.setSelected( true );

                    } else {
                        removeFavoriteMovie( favMovie );
                        Log.d( "Remove Favorite", "Removing Favorite" );
                        fab.setSelected( false );
                    }
                }

            } );
        }


        return detailsView;
    }

    private void loadBackdrop(String url, View v) {
        ImageView imageView = (ImageView) v.findViewById(R.id.backdrop);
        Picasso.with(mContext).load(url).into(imageView);
    }

    public void addFavoriteMovie(PopMovies popmovies) {

        ContentValues cv = new ContentValues();
        cv.put( MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, popmovies.mTitle);
        cv.put( MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, popmovies.mPlotSynopsis);
        cv.put( MovieContract.MovieEntry.COLUMN_RELEASE_DATE, popmovies.mReleaseDate );
        cv.put( MovieContract.MovieEntry.COLUMN_VOTE_AVG, popmovies.mVoteAvg );
        cv.put( MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
        cv.put( MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE, popmovies.backdropSource);
        cv.put( MovieContract.MovieEntry.COLUMN_POSTER_IMAGE, popmovies.posterSource);

        getActivity().getApplicationContext().getContentResolver().insert( MovieContract.MovieEntry.CONTENT_URI, cv );

//        Cursor c = getContentResolver().query(CONTENT_URI, null, COLUMN_MOVIE_ID, null, null);
//        Log.d( "checking cursor", "addFavoriteMovie: " + c.getCount() );
//        if(c.getCount() == 0) {
//            // not found in database
//            Log.d( "no database", "not found on database" );
//        }
    }

    public void removeFavoriteMovie(PopMovies popmovies){

        String mSelectionClause = MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " LIKE ?";
        String arg = popmovies.mTitle;
        String[] mSelectionArgs = {arg};

        getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, mSelectionClause, mSelectionArgs);


//
//        Cursor r = getContentResolver().query(CONTENT_URI, null, COLUMN_MOVIE_ID, null, null);
//        Log.d( "checking cursor", "removeFavoriteMovie: " + r.getCount() );
//        if(r.getCount() == 0) {
//            // not found in database
//            Log.d( "no data", "cannot delete" );
//        }

    }

    //Check if movie title already exists in database
    public Boolean checkDatabase(String movieTitle){
        boolean d;
        String mCheckTitle = MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " LIKE ?";
        String[] checkTitleArgs = {movieTitle};

        Cursor c = getActivity().getContentResolver().query( MovieContract.MovieEntry.CONTENT_URI, null, mCheckTitle, checkTitleArgs, null );

        if(c.getCount() > 0) {
            d = true;
            c.close();
        }else{
            d = false;
            c.close();
        }

        return d;
    }


    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE );

        NetworkInfo activeNetwork = connec.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }



}
