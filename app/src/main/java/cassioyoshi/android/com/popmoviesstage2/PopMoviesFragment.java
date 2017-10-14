package cassioyoshi.android.com.popmoviesstage2;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
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
import cassioyoshi.android.com.popmoviesstage2.retrofit.RetrofitStart;
import cassioyoshi.android.com.popmoviesstage2.utilities.MovieJsonUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by cassioimamura on 2/4/17.
 */

public class PopMoviesFragment extends Fragment {

    private static final String LOG_TAG = PopMoviesFragment.class.getSimpleName();

    private PopMoviesAdapter adapter;
    private List<PopMovies> moviesArrayList;
    private RecyclerView mRecyclerView;
    private String category_chooser;
    public String jsonmovies;
    private SQLiteDatabase mDb;


    public static String temp;
    public Context mContext;
    public GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
    ;


    public PopMoviesFragment() {
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate( R.layout.fragment_main, container, false );

        if (temp == null) {
            category_chooser = "top_rated";
        }
        if (temp == "top_rated") {
            category_chooser = temp;
        }
        if (temp == "popular") {
            category_chooser = temp;
        }

        mContext = rootView.getContext();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movies_grid);
//        MovieDbHelper movieDbHelper = new MovieDbHelper( mContext );
//        mDb = movieDbHelper.getWritableDatabase();



// language is set to en-US and page is set to 1 and together with API_KEY are found on config.xml resource
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

        return rootView;

    }


    public void updateData(List<PopMovies> movieList) {
        moviesArrayList = movieList;
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
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
            hideProgressBar();
            Toast.makeText(getActivity(), "Not Connected, please verify your Internet Connection", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }


    private Cursor getAllMovies() {
        // COMPLETED (6) Inside, call query on mDb passing in the table name and projection String [] order by COLUMN_TIMESTAMP
        return mDb.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

}





