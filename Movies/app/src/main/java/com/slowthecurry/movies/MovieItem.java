package com.slowthecurry.movies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mycah on 5/9/16.
 */
public class MovieItem implements Parcelable {
    String title;
    String plot;
    String userRating;
    String releaseDate;
    String posterTag;

    public MovieItem() {
    }

    public MovieItem(String title, String plot, String userRating, String releaseDate, String posterTag) {
        this.title = title;
        this.plot = plot;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.posterTag = posterTag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterTag() {
        return posterTag;
    }

    public void setPosterTag(String posterTag) {
        this.posterTag = posterTag;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(plot);
        dest.writeString(userRating);
        dest.writeString(releaseDate);
        dest.writeString(posterTag);

    }

    public static final Creator<MovieItem> CREATOR
            = new Creator<MovieItem>() {
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    protected MovieItem(Parcel in) {
        title = in.readString();
        plot = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();
        posterTag = in.readString();
    }
}
