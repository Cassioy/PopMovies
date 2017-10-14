package cassioyoshi.android.com.popmoviesstage2.utilities;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import cassioyoshi.android.com.popmoviesstage2.PopMovies;

/**
 * Created by cassioimamura on 2/5/17.
 */

public class MovieJsonUtils {

    private static final String TAG = MovieJsonUtils.class.getSimpleName();


    public static ArrayList<PopMovies> getSimpleMovieStringsFromJson(Context context, String movieJsonStr)
            throws JSONException {

        /* Movie information. Each movie info is an element of the "movie" array */
        final String OWM_LIST = "results";
        final String POSTER_PATH = "poster_path";
        final String ORIGINAL_TITLE = "original_title";
        final String OVERVIEW = "overview";
        final String USER_RATING = "vote_average";
        final String BACKDROP_PATH = "backdrop_path";
        final String RELEASE_DATE = "release_date";
        final String MOVIE_ID = "id";

        final String OWM_MESSAGE_CODE = "status_code";


        /* String array to hold popular movies String */

        ArrayList<PopMovies> moviePopular = new ArrayList<>();

        JSONObject movieJson = new JSONObject(movieJsonStr);

        /* Is there an error? */
        if (movieJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray results = movieJson.getJSONArray(OWM_LIST);

        for (int i = 0; i < results.length(); i++) {
            JSONObject movieJsonObject = results.getJSONObject(i);


            String poster_image = movieJsonObject.getString(POSTER_PATH);
            String backdrop_image = movieJsonObject.getString(BACKDROP_PATH);
            String movie_title = movieJsonObject.getString(ORIGINAL_TITLE);
            String overview = movieJsonObject.getString(OVERVIEW);
            String user_rating = movieJsonObject.getString(USER_RATING);
            String release_date = movieJsonObject.getString(RELEASE_DATE);
            String m_id = movieJsonObject.getString(MOVIE_ID);

            URL buildImage = NetworkUtils.buildImageUrl(poster_image);
            URL buildBackdrop = NetworkUtils.buildImageUrl(backdrop_image);

            Log.v("MONTANDO O JSON", "VERIFICANDO O ID " + m_id);

            moviePopular.add(new PopMovies(buildImage, buildBackdrop, movie_title, overview, user_rating, release_date, m_id));
        }

        return moviePopular;
    }

}
