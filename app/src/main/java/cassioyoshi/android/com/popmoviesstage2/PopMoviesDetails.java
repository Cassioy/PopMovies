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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

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

import static cassioyoshi.android.com.popmoviesstage2.R.id.plot_synopsis;
import static cassioyoshi.android.com.popmoviesstage2.R.id.release_date;
import static cassioyoshi.android.com.popmoviesstage2.data.MovieContract.MovieEntry.CONTENT_URI;


/**
 * Created by cassioimamura on 2/4/17.
 */

public class PopMoviesDetails extends AppCompatActivity {

    private RecyclerView mRecyclerViewTrailer;
    private RecyclerView.Adapter mAdapter;
    private List<Result> trailersList;
    private List<ReviewList> reviewDescription;
    private Context mContext;
    private String totalReviews;
    private int id;
    private PopMovies favMovie;
    private TextView noTrailer;
    private TextView noInternet;

    private Cursor cursor;


    public LinearLayoutManager linearLayoutManager
            = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);


    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView( R.layout.details_pop_movies);

        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

            String thumbnailUrl = (String) getIntent().getSerializableExtra("posterImage");
            String imageUrl = (String) getIntent().getSerializableExtra("backdropImage");
            String title = (String) getIntent().getSerializableExtra("title");
            String synopsis = (String) getIntent().getSerializableExtra("plotSynopsis");
            String released = (String) getIntent().getSerializableExtra( "releaseDate" );
            String votes = (String) getIntent().getSerializableExtra( "voteAvg" );
            String video_id = (String) getIntent().getSerializableExtra( "id" );
            id = Integer.parseInt( video_id );

            favMovie = new PopMovies( thumbnailUrl, imageUrl, title, synopsis, votes, released, video_id );

        Log.v( "verificando id", " " + id);
            mRecyclerViewTrailer = (RecyclerView) findViewById( R.id.horizontal_recycler_view );


// language is set to en-US and page is set to 1, API_KEY is found on config.xml resource not included in this project

        Call<ReviewData> callReview = new RetrofitStart().getMovie()
                    .requestReviews(id ,getString(R.string.API_KEY), getString( R.string.LANGUAGE ), Integer.parseInt(getString(R.string.PAGE)));
            callReview.enqueue(new Callback<ReviewData>() {
                @Override
                public void onResponse(Call<ReviewData> callReview, Response<ReviewData> responseReview) {

                    try{
                        int statusCode = responseReview.code();
                        ReviewData reviewData = responseReview.body();
                        totalReviews = reviewData.getTotalResults().toString();

                        Button reviewBtn = (Button) findViewById( R.id.review );

                        Log.v( "verificando", "Results Review" + statusCode);
                        Log.v( "verificando", "Total Resultados " + totalReviews);

                        reviewBtn.setText(totalReviews);



                    }catch (Exception e){
                        Log.e("onFailure", "Requisicao detalhes vazia...");

                    }
                }

                @Override
                public void onFailure(Call<ReviewData> callReview, Throwable t) {
                    Log.e("onFailure", "Requisicao detalhes falhou... ");
                }
            });

            Call<Video> call = new RetrofitStart().getMovie()
                    .requestVideos(id ,getString(R.string.API_KEY), getString( R.string.LANGUAGE ), Integer.parseInt(getString(R.string.PAGE)));
            call.enqueue(new Callback<Video>() {
                @Override
                public void onResponse(Call<Video> call, Response<Video> response) {

                    try{
                        int statusCode = response.code();
                        Video selectedVideo = response.body();
                        trailersList = selectedVideo.getResults();

                        Log.v( "verificando", "Results " + statusCode);
                        Log.v( "VideoObject", "Results " + selectedVideo);
                        Log.v( "List Results", "Results " + trailersList);

                        mAdapter = new TrailerAdapter(mContext, trailersList);

                        mRecyclerViewTrailer.setHasFixedSize( true );

                        mRecyclerViewTrailer.setLayoutManager(linearLayoutManager);
                        mRecyclerViewTrailer.setAdapter(mAdapter);

                    }catch (Exception e){
                        Log.e("onFailure", "Requisicao detalhes vazia...");

                    }
                }

                @Override
                public void onFailure(Call<Video> call, Throwable t) {
                    Log.e("onFailure", "Requisicao detalhes falhou... ");
                    if(isInternetOn()) {

                    }else{

                        Handler mainHandler = new Handler( Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // code to interact with UI
                                mRecyclerViewTrailer.setVisibility( View.GONE );
                                noInternet = (TextView) findViewById( R.id.verify_internet );
                                noTrailer = (TextView) findViewById( R.id.no_trailer );
                                noInternet.setVisibility( View.VISIBLE );
                            }
                        });
                    }
                }
            });

            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            CollapsingToolbarLayout collapsingToolbar =
                    (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(title);

        final ImageButton fab = (ImageButton) findViewById(R.id.fab);



        ImageView thumb = (ImageView) findViewById( R.id.movie_info_thumbnail );
        Picasso.with(this).load(thumbnailUrl).into(thumb);


        loadBackdrop(imageUrl);

        if (checkDatabase( title ) == true) {
            fab.setSelected( true );
            fab.setImageResource(R.drawable.ic_grade_yellow_48px );

        }else{
            fab.setSelected( false );
            fab.setImageResource(R.drawable.ic_grade_white_48px);
        }


            TextView overview = (TextView) findViewById( plot_synopsis);
            overview.setText(synopsis);

            TextView releaseDate = (TextView) findViewById( release_date);
            releaseDate.setText(released);


            Drawable red = ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_3);
            Drawable yellow = ContextCompat.getDrawable(getApplicationContext(),R.drawable.circle_2);
            Drawable green = ContextCompat.getDrawable(getApplicationContext(),R.drawable.circle);


            Button rating = (Button) findViewById( R.id.rating_button );
            double votedAvg = Double.parseDouble(votes);
            if (votedAvg < 4){
                rating.setBackground(red);
                rating.setText(votes);
            }else if(votedAvg < 7){
                rating.setBackground(yellow);
                rating.setText(votes);
            }else if(votedAvg >= 7){
                rating.setBackground(green);
                rating.setText(votes);
            }

//            Acesso a pagina de reviews TBD
//            Button reviews = (Button) findViewById(R.id.review);


        fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fab.setImageResource( fab.isSelected() ? R.drawable.ic_grade_white_48px : R.drawable.ic_grade_yellow_48px );
                    if(!fab.isSelected()){
                        addFavoriteMovie(favMovie);
                        Log.d( "adding Favorite Movie", "name: " + favMovie.mTitle);
                        fab.setSelected(true);

                    }else{
                        removeFavoriteMovie(favMovie);
                        Log.d( "Remove Favorite", "Removing Favorite" );
                        fab.setSelected(false);
                    }
                }

            });
        }


        private void loadBackdrop(String url) {
            ImageView imageView = (ImageView) findViewById(R.id.backdrop);
            Picasso.with(this).load(url).into(imageView);

        }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // API 5+ solution
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

        PopMoviesDetails.this.overridePendingTransition(R.anim.trans_left_out,
                R.anim.trans_left_in);
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

          getApplicationContext().getContentResolver().insert( CONTENT_URI, cv );

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

        getContentResolver().delete(CONTENT_URI, mSelectionClause, mSelectionArgs);


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

        Cursor c = getContentResolver().query( CONTENT_URI, null, mCheckTitle, checkTitleArgs, null );

        if(c.getCount() > 0) {
            d = true;
            c.close();
        }else{
            d = false;
            c.close();
        }

        return d;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE );

        NetworkInfo activeNetwork = connec.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }
}



