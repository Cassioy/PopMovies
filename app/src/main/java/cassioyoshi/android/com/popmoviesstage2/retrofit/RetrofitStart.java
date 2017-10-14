package cassioyoshi.android.com.popmoviesstage2.retrofit;

import cassioyoshi.android.com.popmoviesstage2.services.PopMoviesService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by cassioimamura on 8/30/17.
 */

public class RetrofitStart {

    private final Retrofit retrofit;

    public RetrofitStart(){

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/movie/" )
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public PopMoviesService getMovie() {
            return retrofit.create(PopMoviesService.class);

    }
}
