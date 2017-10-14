package cassioyoshi.android.com.popmoviesstage2.services;

import com.google.gson.JsonObject;

import cassioyoshi.android.com.popmoviesstage2.data.model.ReviewData;
import cassioyoshi.android.com.popmoviesstage2.data.model.Video;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by cassioimamura on 8/30/17.
 */


// language is set to en-US and page is set to 1 and together with API_KEY are found on config.xml resource
public interface PopMoviesService {
    @GET("{category_chooser}")
    Call<JsonObject> requestMovies(@Path("category_chooser") String category_chooser, @Query("api_key") String API_KEY, @Query("language") String language, @Query("page" ) int page);

    @GET("{movieId}/reviews")
    Call<ReviewData> requestReviews(@Path("movieId") int id, @Query("api_key") String API_KEY, @Query("language") String language, @Query("page" ) int page);

    @GET("{movieId}/videos")
    Call<Video> requestVideos(@Path("movieId") int id, @Query("api_key") String API_KEY, @Query("language") String language, @Query("page" ) int page);
}
