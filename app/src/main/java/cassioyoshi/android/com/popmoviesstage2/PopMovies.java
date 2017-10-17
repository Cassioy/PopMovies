package cassioyoshi.android.com.popmoviesstage2;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;

/**
 * Created by cassioimamura on 2/4/17.
 */

public class PopMovies implements Parcelable{

    public String posterSource;
    public String backdropSource;

    public String mTitle;
    public String mReleaseDate;
    public String mVoteAvg;
    public String mPlotSynopsis;
    public String mId;


    public PopMovies(String posterImage, String backdropImage, String title, String overview, String voteAvg, String releaseDate, String id) {
        this.posterSource = posterImage;
        this.backdropSource = backdropImage;
        this.mTitle = title;
        this.mPlotSynopsis = overview;
        this.mVoteAvg = voteAvg;
        this.mReleaseDate = releaseDate;
        this.mId = id;

    }

    public PopMovies(Parcel in) {
        mTitle = in.readString();
        mPlotSynopsis = in.readString();
        mVoteAvg = in.readString();
        mReleaseDate = in.readString();
        posterSource = in.readParcelable(URL.class.getClassLoader());
        backdropSource = in.readParcelable(URL.class.getClassLoader());
        mId = in.readString();


    }



    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mTitle);
        out.writeString( mPlotSynopsis );
        out.writeString( mVoteAvg );
        out.writeString( mReleaseDate );
        out.writeValue( posterSource );
        out.writeValue( backdropSource );
        out.writeString( mId );
    }

    public static final Parcelable.Creator<PopMovies> CREATOR
            = new Parcelable.Creator<PopMovies>() {

        public PopMovies createFromParcel(Parcel in) {
            return new PopMovies(in);
        }

        public PopMovies[] newArray(int size) {
            return new PopMovies[size];
        }
    };

    public String getPosterSource() {
        return posterSource;
    }


}
