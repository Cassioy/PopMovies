package cassioyoshi.android.com.popmoviesstage2;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.List;

import cassioyoshi.android.com.popmoviesstage2.data.MovieContract;
import cassioyoshi.android.com.popmoviesstage2.data.MovieDbHelper;
import cassioyoshi.android.com.popmoviesstage2.retrofit.RetrofitStart;
import cassioyoshi.android.com.popmoviesstage2.utilities.MovieJsonUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cassioyoshi.android.com.popmoviesstage2.data.MovieContract.MovieEntry.COLUMN_MOVIE_ID;
import static cassioyoshi.android.com.popmoviesstage2.data.MovieContract.MovieEntry.CONTENT_URI;

/**
 * Created by cassioimamura on 2/4/17.
 */

public class PopMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = PopMoviesFragment.class.getSimpleName();

    private PopMoviesAdapter adapter;
    private FavoritesCursorAdapter favoritesCursorAdapter;
    private List<PopMovies> moviesArrayList;
    private RecyclerView mRecyclerView;
    private String category_chooser;
    public String jsonmovies;
    public SQLiteDatabase mDb;

    private static final int ID_FAVORITES_LOADER = 44;

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

    public static final int INDEX_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_PLOT_SYNOPSIS = 2;
    public static final int INDEX_RELEASE_DATE = 3;
    public static final int INDEX_VOTE_AVG = 4;
    public static final int INDEX_MOVIE_ID = 5;
    public static final int INDEX_BACKDROP_IMAGE = 6;
    public static final int INDEX_POSTER_IMAGE = 7;


    private int mPosition = RecyclerView.NO_POSITION;

    public static String temp;
    public Context mContext;
    private Cursor m;
    public GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);



    public PopMoviesFragment() {
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate( R.layout.fragment_main, container, false );

        if (temp == null) {
            category_chooser = "popular";
        }
        if (temp == "top_rated") {
            category_chooser = temp;
        }
        if (temp == "popular") {
            category_chooser = temp;
        }

        mContext = rootView.getContext();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movies_grid);

        if(category_chooser == "popular" || category_chooser == "top_rated") {

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
                        Log.d( "Json formado", jsonmovies );
                        moviesArrayList = MovieJsonUtils.getSimpleMovieStringsFromJson(mContext, jsonmovies);
                        adapter = new PopMoviesAdapter( mContext, moviesArrayList );
                        mRecyclerView.setHasFixedSize( true );
                        mRecyclerView.setLayoutManager( gridLayoutManager);
                        mRecyclerView.setAdapter( adapter );
                        hideProgressBar();

                    }catch (Exception e){
                        Log.e("onFailure", "Requisicao vazia...");
                        isInternetOn();
                        hideProgressBar();

                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("onFailure", "Requisicao falhou...");
                    isInternetOn();

                }
            });


        }else {

            MovieDbHelper movieDbHelper = new MovieDbHelper( mContext );
            mDb = movieDbHelper.getWritableDatabase();
            getLoaderManager().initLoader(ID_FAVORITES_LOADER, null, this);
            getAllMovies();

            favoritesCursorAdapter = new FavoritesCursorAdapter( mContext, m );

            mRecyclerView.setHasFixedSize( true );
            mRecyclerView.setLayoutManager( gridLayoutManager);

            //clean Picasso Cache for Favorites
            mRecyclerView.setBackgroundColor( ContextCompat.getColor(mContext, R.color.colorWhite) );

            mRecyclerView.setAdapter( favoritesCursorAdapter );
            hideProgressBar();


        }



        return rootView;

    }

    public void hideProgressBar() {

        ProgressBar progress = (ProgressBar) getActivity().findViewById( R.id.progressBarFetch );
        progress.setVisibility( View.GONE );

    }


    public void getTemp(String t) {
        temp = t;
    }


    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager)getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE );

        // Check for network connections
        if ( connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.CONNECTED ) {

            return true;

        } else if (
                connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
            hideProgressBar();
            Toast.makeText(getActivity(), "Not Connected, please verify your Internet Connection", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }


    private Cursor getAllMovies() {
        m = getActivity().getContentResolver().query(CONTENT_URI, null, COLUMN_MOVIE_ID, null, null);
        return m;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        switch (loaderId) {

            case ID_FAVORITES_LOADER:

                Uri favoritesQueryUri = CONTENT_URI;
                String sortOrder = MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " ASC";


                return new CursorLoader(mContext,
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        favoritesCursorAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        mRecyclerView.setAdapter( favoritesCursorAdapter );

        if (data.getCount() != 0) showWeatherDataView();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        favoritesCursorAdapter.swapCursor(null);
    }

    private void showWeatherDataView() {
        /* First, hide the loading indicator */
        hideProgressBar();
        mRecyclerView.setVisibility( View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
            if(m != null) {
                m.close();
            }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(m != null) {
            m.close();
        }
    }
}





