package cassioyoshi.android.com.popmoviesstage2;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


/**
 * Created by cassioimamura on 2/4/17.
 */

public class PopMoviesDetails extends AppCompatActivity {



    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView( R.layout.details_fragment);

        //overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

        if(savedInstanceState == null) {


            PopMoviesDetailsFragment popDetailsFragment = new PopMoviesDetailsFragment();

//            String thumbnailUrl = getIntent().getStringExtra( "posterImage" );
//            String imageUrl = getIntent().getStringExtra( "backdropImage" );
//            String title = getIntent().getStringExtra( "title" );
//            String synopsis = getIntent().getStringExtra( "plotSynopsis" );
//            String released = getIntent().getStringExtra( "releaseDate" );
//            String votes = getIntent().getStringExtra( "voteAvg" );
//            String video_id = getIntent().getStringExtra( "id" );
//
            popDetailsFragment.setArguments( getIntent().getExtras() );

            FragmentManager fragmentManager = getSupportFragmentManager();
            //Handle when activity is recreated like on orientation Change
            fragmentManager.beginTransaction()
                    .replace( R.id.fragment_details_child, popDetailsFragment )
                    .commit();

        }

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

//        PopMoviesDetails.this.overridePendingTransition(R.anim.trans_left_out,
//                R.anim.trans_left_in);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}



