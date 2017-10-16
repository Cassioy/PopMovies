package cassioyoshi.android.com.popmoviesstage2.utilities;

import android.util.Log;

/**
 * Created by cassioimamura on 2/4/17.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String IMAGES_BASE_URL = "http://image.tmdb.org/t/p/w500";

    public static String buildImageUrl(String posterImage) {

        String url = null;
        try {
            url = new String( IMAGES_BASE_URL + posterImage );
        } catch (Exception e) {
            Log.e( TAG, "Problem building the URL ", e );
        }

        return url;
    }

}