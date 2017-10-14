package cassioyoshi.android.com.popmoviesstage2.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by cassioimamura on 10/5/17.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "cassioyoshi.android.com.popmoviesstage2";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_MOVIE = "movie";


    public static final class MovieEntry implements BaseColumns {

        // Content URI represents the base location for the table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_PLOT_SYNOPSIS = "plot_synopsis";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVG = "vote_avg  ";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POSTER_IMAGE = "poster_image";
        public static final String COLUMN_BACKDROP_IMAGE = "backdrop_image";
        public static final String COLUMN_REVIEW_RESULTS = "review_results";
        public static final String COLUMN_TRAILER_RESULTS = "trailer_results";


    }
}
