package me.stanka.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by staci on 4/27/16.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Movie> movies;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public void setMovies(ArrayList<Movie> movies){
        this.movies = movies;
    }

    public int getCount() {
        return movies.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext).load("https://image.tmdb.org/t/p/w185" + movies.get(position).getPoster_path()).into(imageView);

        return imageView;
    }

}
