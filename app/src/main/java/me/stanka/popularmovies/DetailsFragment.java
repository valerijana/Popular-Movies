package me.stanka.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsFragment extends Fragment {

    private Movie mMovie;

    public DetailsFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MainActivityFragment.PARCABLE_KEY, mMovie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        TextView title = (TextView) rootView.findViewById(R.id.title);
        ImageView poster = (ImageView) rootView.findViewById(R.id.poster);
        TextView release = (TextView) rootView.findViewById(R.id.release);
        TextView rating = (TextView) rootView.findViewById(R.id.rating);
        TextView overview = (TextView) rootView.findViewById(R.id.overview);
        if(savedInstanceState == null || !savedInstanceState.containsKey(MainActivityFragment.PARCABLE_KEY)) {
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                mMovie = intent.getExtras().getParcelable(Intent.EXTRA_TEXT);
            } else{
                mMovie = new Movie("","","","","","");
            }
        } else {
            mMovie = savedInstanceState.getParcelable("movie");
        }
        String vote = mMovie.getVote() + "/10";
        title.setText(mMovie.getTitle());
        Picasso.with(getActivity())
                .load("https://image.tmdb.org/t/p/w185" + mMovie.getPoster_path())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(poster);
        if(mMovie.getRelease().length() >= 4){
            release.setText(mMovie.getRelease().substring(0,4));
        }
        rating.setText(vote);
        overview.setText(mMovie.getOverview());
        return rootView;
    }
}
