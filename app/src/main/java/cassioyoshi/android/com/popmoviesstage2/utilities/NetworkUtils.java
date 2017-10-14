package cassioyoshi.android.com.popmoviesstage2.utilities;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by cassioimamura on 2/4/17.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String IMAGES_BASE_URL = "http://image.tmdb.org/t/p/w500/";

    public static URL buildImageUrl(String posterImage) {

        URL url = null;
        try {
            url = new URL( IMAGES_BASE_URL + posterImage );
        } catch (MalformedURLException e) {
            Log.e( TAG, "Problem building the URL ", e );
        }

        return url;
    }

}