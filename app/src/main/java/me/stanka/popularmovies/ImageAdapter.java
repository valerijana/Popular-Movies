package me.stanka.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
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
    private ArrayList<Movie> mMovies;
    private LayoutInflater mLayoutInflater;

    public ImageAdapter(Context c) {
        mContext = c;
        mLayoutInflater = LayoutInflater.from(c);
    }

    public void setMovies(ArrayList<Movie> movies){
        mMovies = movies;
    }

    static class ViewHolder {
        private ImageView img;
    }

    public int getCount() {
        return mMovies.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.image_item, null);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(mContext)
                .load("https://image.tmdb.org/t/p/w185" + mMovies.get(position).getPoster_path())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(holder.img);

        return convertView;
    }

}
