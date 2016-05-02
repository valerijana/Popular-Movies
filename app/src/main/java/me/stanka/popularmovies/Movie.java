package me.stanka.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stanka on 01.5.2016.
 */
public class Movie implements Parcelable{
    private String id;
    private String poster_path;
    private String title;
    private String overview;
    private String vote;
    private String release;

    public Movie(String id, String poster_path){
        this.id = id;
        this.poster_path = poster_path;
        title = overview = vote = release = "";
    }

    public Movie(String id, String poster_path, String title, String overview, String vote, String release){
        this.id = id;
        this.poster_path = poster_path;
        this.title = title;
        this.overview = overview;
        this.vote = vote;
        this.release = release;
    }

    private Movie(Parcel in){
        id = in.readString();
        poster_path = in.readString();
        title = in.readString();
        overview = in.readString();
        vote = in.readString();
        release = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(poster_path);
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeString(vote);
        parcel.writeString(release);
    }

    public void setId(String id){
        this.id = id;
    }

    public void setPoster_path(String poster_path){
        this.poster_path = poster_path;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setOverview(String overview){
        this.overview = overview;
    }

    public void setVote(String vote){
        this.vote = vote;
    }

    public void setRelease(String release){
        this.release = release;
    }

    public String getId(){
        return id;
    }

    public String getPoster_path(){
        return poster_path;
    }

    public String getTitle(){
        return title;
    }

    public String getOverview(){
        return overview;
    }

    public String getVote(){
        return vote;
    }

    public String getRelease(){
        return release;
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };
}
