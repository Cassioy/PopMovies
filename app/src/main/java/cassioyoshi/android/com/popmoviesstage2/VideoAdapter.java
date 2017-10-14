package cassioyoshi.android.com.popmoviesstage2;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import cassioyoshi.android.com.popmoviesstage2.data.model.Result;

/**
 * Created by cassioimamura on 9/21/17.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    List<Result> mHorizontalTrailers;
    private Context mContext;
    private static final String thumbUrl = "http://img.youtube.com/vi/";
    private static final String thumbSuffix = "/0.jpg";


    public VideoAdapter(Context context, List<Result> myHorizontalTrailers){
        this.mContext = context;
        this.mHorizontalTrailers = myHorizontalTrailers;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //public ImageView mTrailerImage;
        public ImageView youtubeView;
        public TextView mTrailerTitle;


        public ViewHolder(final View itemView) {
            super( itemView );

            youtubeView = (ImageView) itemView.findViewById( R.id.trailer_image );
            mTrailerTitle = (TextView) itemView.findViewById( R.id.trailer_title );

            if (youtubeView != null) {
                youtubeView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Result trailerResult = mHorizontalTrailers.get( getAdapterPosition() );
                        watchYoutubeVideo( itemView.getContext(), trailerResult.getKey() );

                    }
                } );

            }else{
                Snackbar snackbar = Snackbar
                        .make(itemView.getRootView(), "No internet connection!", Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }

                        });
                snackbar.show();
            }
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.trailer_row, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Result result = mHorizontalTrailers.get(position);
        holder.mTrailerTitle.setText(result.getName());


        Log.d( "check key", "onBindViewHolder: " + thumbUrl+result.getKey()+thumbSuffix);

        Picasso.with(mContext).load(thumbUrl+result.getKey()+thumbSuffix)
                .fit()
                .into(holder.youtubeView);
    }

    @Override
    public int getItemCount() {
        return mHorizontalTrailers.size();
    }


    public void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }
}
