package cassioyoshi.android.com.popmoviesstage2;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
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

import java.net.URL;
import java.util.List;

import cassioyoshi.android.com.popmoviesstage2.data.model.Result;
import cassioyoshi.android.com.popmoviesstage2.data.model.ReviewData;
import cassioyoshi.android.com.popmoviesstage2.data.model.ReviewList;
import cassioyoshi.android.com.popmoviesstage2.data.model.Video;
import cassioyoshi.android.com.popmoviesstage2.retrofit.RetrofitStart;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by cassioimamura on 2/4/17.
 */

public class PopMoviesDetails extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Result> trailersList;
    private List<ReviewList> reviewDescription;
    private Context mContext;
    private String totalReviews;
    private int id;

    public LinearLayoutManager linearLayoutManager
            = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);




    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView( R.layout.details_pop_movies);

        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

            URL u = (URL) getIntent().getSerializableExtra("backdropImage");
            URL t = (URL) getIntent().getSerializableExtra("posterImage");
            String imageUrl = u.toString();
            String thumbnailUrl = t.toString();
            String title = (String) getIntent().getSerializableExtra("title");
            String synopsis = (String) getIntent().getSerializableExtra("plotSynopsis");
            String released = (String) getIntent().getSerializableExtra( "releaseDate" );
            String votes = (String) getIntent().getSerializableExtra( "voteAvg" );
            String video_id = (String) getIntent().getSerializableExtra( "id" );
            id = Integer.parseInt( video_id );


        Log.v( "verificando id", " " + id);

        // language is set to en-US and page is set to 1 and together with API_KEY are found on config.xml resource

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


                        mAdapter = new VideoAdapter( mContext, trailersList);

                        mRecyclerView = (RecyclerView) findViewById( R.id.horizontal_recycler_view );
                        mRecyclerView.setHasFixedSize( true );

                        mRecyclerView.setLayoutManager(linearLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);



                    }catch (Exception e){
                        Log.e("onFailure", "Requisicao detalhes vazia...");

                    }
                }

                @Override
                public void onFailure(Call<Video> call, Throwable t) {
                    Log.e("onFailure", "Requisicao detalhes falhou... ");

                }
            });



            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            CollapsingToolbarLayout collapsingToolbar =
                    (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(title);

            TextView overview = (TextView) findViewById( R.id.plot_synopsis);
            overview.setText(synopsis);


            TextView releaseDate = (TextView) findViewById( R.id.release_date);
            releaseDate.setText(released);

            Drawable red = getResources().getDrawable(R.drawable.circle_3);
            Drawable yellow = getResources().getDrawable(R.drawable.circle_2);
            Drawable green = getResources().getDrawable(R.drawable.circle);


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


            ImageView thumb = (ImageView) findViewById( R.id.movie_info_thumbnail );
            Picasso.with(this).load(thumbnailUrl).into(thumb);


            loadBackdrop(imageUrl);

            final ImageButton fab = (ImageButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fab.setImageResource( fab.isSelected() ? R.drawable.ic_grade_white_48px : R.drawable.ic_grade_yellow_48px );
                    fab.setSelected( !fab.isSelected() );

                }

            });

        }


        private void loadBackdrop(String url) {
            final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
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

}



