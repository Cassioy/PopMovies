package cassioyoshi.android.com.popmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.List;

import cassioyoshi.android.com.popmoviesstage2.adapter.FavoritesCursorAdapter;
import cassioyoshi.android.com.popmoviesstage2.adapter.PopMoviesAdapter;
import cassioyoshi.android.com.popmoviesstage2.data.MovieContract;
import cassioyoshi.android.com.popmoviesstage2.data.MovieDbHelper;
import cassioyoshi.android.com.popmoviesstage2.retrofit.RetrofitStart;
import cassioyoshi.android.com.popmoviesstage2.utilities.MovieJsonUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static cassioyoshi.android.com.popmoviesstage2.data.MovieContract.MovieEntry.CONTENT_URI;

/**
 * Created by cassioimamura on 2/4/17.
 */

public class PopMoviesFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = PopMoviesFragment.class.getSimpleName();

    private PopMoviesAdapter adapter;
    private FavoritesCursorAdapter favoritesCursorAdapter;
    private List<PopMovies> moviesArrayList;
    private RecyclerView mRecyclerView;
    private TextView mNodata;
    private TextView mNofavorite;
    private Button mRetry;
    private String category_chooser;
    public String jsonmovies;
    public SQLiteDatabase mDb;

    private int mPosition = RecyclerView.NO_POSITION;

    public static String temp;
    public Context mContext;
    public GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);

    public CustomItemClickListener mCallback;

    private static final int ID_FAVORITES_LOADER = 44;
    public boolean mTwoPane;

    public static final int INDEX_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_PLOT_SYNOPSIS = 2;
    public static final int INDEX_RELEASE_DATE = 3;
    public static final int INDEX_VOTE_AVG = 4;
    public static final int INDEX_MOVIE_ID = 5;
    public static final int INDEX_BACKDROP_IMAGE = 6;
    public static final int INDEX_POSTER_IMAGE = 7;

    public static final String[] MAIN_FAVORITES = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVG,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE,
            MovieContract.MovieEntry.COLUMN_POSTER_IMAGE
    };

    private static final String MOVIE_POPULAR = "popular";
    private static final String MOVIE_TOP_RATED = "top_rated";
    private static final String MOVIE_FAVORITE = "favorites";

    private static final String posterSource = "posterImage";
    private static final String backdropSource = "backdropImage";
    private static final String mTitle = "title";
    private static final String mPlotSynopsis = "plotSynopsis";
    private static final String mVoteAvg = "voteAvg";
    private static final String mReleaseDate = "releaseDate";
    private static final String mId = "id";

    public PopMoviesFragment() {
//        setRetainInstance( true );
    }

    public interface CustomItemClickListener {
        void onItemClick(View v, int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach( context );

        try {
            mCallback = (CustomItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate( R.layout.fragment_main, container, false );

        //Default Request: Popular Movies on Loading
            if (temp == null) {
                category_chooser = MOVIE_POPULAR;
            }
            if (temp == MOVIE_TOP_RATED) {
                category_chooser = temp;
            }
            if (temp == MOVIE_POPULAR) {
                category_chooser = temp;
            }
            if (temp == MOVIE_FAVORITE){
                category_chooser = temp;
            };

            mContext = rootView.getContext();
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_main_recycler);
            mRecyclerView.setHasFixedSize( true );
            mRecyclerView.setLayoutManager( gridLayoutManager);
            mNodata = (TextView) rootView.findViewById( R.id.nointernet );
            mRetry = (Button) rootView.findViewById( R.id.retry_btn );
            mNofavorite = (TextView) rootView.findViewById( R.id.no_favorite );


        if(category_chooser == MOVIE_POPULAR || category_chooser == MOVIE_TOP_RATED) {

            //Return background to default
            mRecyclerView.setBackgroundColor(0);

            // language is set to en-US and page is set to 1, API_KEY is found on config.xml resource not included on github
            Call<JsonObject> call = new RetrofitStart()
                    .getMovie().requestMovies(category_chooser, getString(R.string.API_KEY), getString( R.string.LANGUAGE ), Integer.parseInt(getString(R.string.PAGE)));
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    try{
                        isInternetOn();
                        JsonObject ob = response.body();
                        jsonmovies = ob.toString();
                        //Log.d( "Json formado", jsonmovies );
                        moviesArrayList = MovieJsonUtils.getSimpleMovieStringsFromJson(mContext, jsonmovies);
                        adapter = new PopMoviesAdapter( mContext, moviesArrayList, new CustomItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
                                Log.d(TAG, "clicked position:" + position);
                                Bundle bundle = new Bundle();
                                bundle.putString( mId, moviesArrayList.get(position).mId );
                                bundle.putString( backdropSource, moviesArrayList.get( position ).backdropSource );
                                bundle.putString( posterSource, moviesArrayList.get( position ).posterSource );
                                bundle.putString( mTitle, moviesArrayList.get( position ).mTitle );
                                bundle.putString( mPlotSynopsis, moviesArrayList.get( position ).mPlotSynopsis );
                                bundle.putString( mReleaseDate, moviesArrayList.get( position ).mReleaseDate );
                                bundle.putString( mVoteAvg, moviesArrayList.get( position ).mVoteAvg );

                                //check if layout is more than 600 wide
                                if(getActivity().findViewById( R.id.frag_details ) != null){
                                    mTwoPane = true;
                                }else{
                                    mTwoPane = false;
                                }

                                //Apply a second fragment if screen is larger than 600dp
                                if(mTwoPane) {

                                    PopMoviesDetailsFragment detailsFragment = new PopMoviesDetailsFragment();
                                    detailsFragment.setArguments( bundle );
                                    FragmentManager fragmentManager = ((PopMoviesMainActivity) mContext).getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                                .replace( R.id.frag_details, detailsFragment, "details" )
                                                .commit();

                                }else {

                                        final Intent intent = new Intent(mContext, PopMoviesDetails.class);
                                        intent.putExtras(bundle);
                                        startActivity( intent );
                                    }
                            }
                        });

                        mRecyclerView.setAdapter( adapter );


                    }catch (Exception e){
                        Log.e("onFailure", "Requisicao vazia...");

                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("onFailure", "Requisicao falhou...");
                    if(isInternetOn()){

                    }else{
                        //if internet is off show retry screen
                        mRecyclerView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite) );
                        mNodata.setVisibility( View.VISIBLE );
                        mRetry.setVisibility( View.VISIBLE );
                        mRetry.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent( getActivity(), PopMoviesMainActivity.class );
                                mRecyclerView.setBackgroundColor(0);
                                startActivity( i );
                            }
                        } );

                    }

                }
            });


        }else if(category_chooser == MOVIE_FAVORITE){
            MovieDbHelper movieDbHelper = new MovieDbHelper( mContext );
            mDb = movieDbHelper.getWritableDatabase();
            getLoaderManager().initLoader(ID_FAVORITES_LOADER, null, this);

            mRecyclerView.setHasFixedSize( true );
            mRecyclerView.setLayoutManager( gridLayoutManager);
        }
        return rootView;
    }

    public void hideProgressBar() {

        ProgressBar progress = (ProgressBar) getActivity().findViewById( R.id.progressBarFetch );
        if(progress != null) {
            progress.setVisibility( View.GONE );
        }

    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        switch (loaderId) {

            case ID_FAVORITES_LOADER:

                Uri favoritesQueryUri = CONTENT_URI;
                String sortOrder = MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " ASC";


                return new android.support.v4.content.CursorLoader(mContext,
                        favoritesQueryUri,
                        MAIN_FAVORITES,
                        null,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, final Cursor data) {
        data.moveToFirst();
        favoritesCursorAdapter = new FavoritesCursorAdapter( mContext, data );
        favoritesCursorAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        mRecyclerView.setAdapter( favoritesCursorAdapter);


        if (data.getCount() != 0) {
            showFavoritesDataView();
        }else{
            showNoFavoritesMessage(mContext);
        }

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        favoritesCursorAdapter.swapCursor(null);
    }

    private void showFavoritesDataView() {
        /* First, hide the loading indicator */
        hideProgressBar();
        mRecyclerView.setVisibility( View.VISIBLE);
        mRecyclerView.setBackgroundColor( ContextCompat.getColor(mContext, R.color.colorWhite) );

    }

    private void showNoFavoritesMessage(final Context context){

                mRecyclerView.setBackgroundColor( ContextCompat.getColor(context, R.color.colorWhite) );
                mNodata.setVisibility( View.GONE );
                mRetry.setVisibility( View.GONE );
                mNofavorite.setVisibility( View.VISIBLE );

    }



    @Override
    public void onPause() {
        super.onPause();
        if(mDb != null) {
            mDb.close();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if(mDb != null) {
            mDb.close();
        }
    }

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE );

        NetworkInfo activeNetwork = connec.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onStart() {
        super.onStart();


    }
}






