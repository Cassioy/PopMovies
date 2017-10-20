package cassioyoshi.android.com.popmoviesstage2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cassioyoshi.android.com.popmoviesstage2.R;
import cassioyoshi.android.com.popmoviesstage2.data.model.ReviewList;

/**
 * Created by cassioimamura on 10/18/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private Context mContext;
    List<ReviewList> mReviewList;


    // Pass in the contact array into the constructor
    public ReviewsAdapter(Context context, List<ReviewList> reviewList) {
        mReviewList = reviewList;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView authorTextView;
        public TextView commentTextView;
        public View lineDividerView;


        public ViewHolder(View itemView) {

            super( itemView );

            authorTextView = (TextView) itemView.findViewById( R.id.review_title );
            commentTextView = (TextView) itemView.findViewById( R.id.review_comment );
            lineDividerView = itemView.findViewById( R.id.line_divider );
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.reviews_movies_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ReviewList reviewList = mReviewList.get( position );
        holder.authorTextView.setText( reviewList.getAuthor() );
        holder.commentTextView.setText( reviewList.getContent() );

    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

}
