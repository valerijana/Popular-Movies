package me.stanka.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsFragment extends Fragment {

    private TextView title;
    private ImageView poster;
    private TextView release;
    private TextView rating;
    private TextView overview;
    private Movie movie;

    public DetailsFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movie", movie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        title = (TextView) rootView.findViewById(R.id.title);
        poster = (ImageView) rootView.findViewById(R.id.poster);
        release = (TextView) rootView.findViewById(R.id.release);
        rating = (TextView) rootView.findViewById(R.id.rating);
        overview = (TextView) rootView.findViewById(R.id.overview);
        if(savedInstanceState == null || !savedInstanceState.containsKey("movie")) {
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
//                String id = intent.getStringExtra(Intent.EXTRA_TEXT);
//                new FetchMoviesTask().execute(id);
                movie = intent.getExtras().getParcelable(Intent.EXTRA_TEXT);
                String vote = movie.getVote() + "/10";
                title.setText(movie.getTitle());
                Picasso.with(getActivity()).load("https://image.tmdb.org/t/p/w185" + movie.getPoster_path()).into(poster);
                release.setText(movie.getRelease().substring(0,4));
                rating.setText(vote);
                overview.setText(movie.getOverview());
            }
        } else {
            movie = savedInstanceState.getParcelable("movie");
            final String vote = movie.getVote() + "/10";
            title.setText(movie.getTitle());
            Picasso.with(getActivity()).load("https://image.tmdb.org/t/p/w185" + movie.getPoster_path()).into(poster);
            release.setText(movie.getRelease().substring(0,4));
            rating.setText(vote);
            overview.setText(movie.getOverview());
        }
        return rootView;
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private Movie getMoviesDataFromJson(String moviesJsonStr)
                throws JSONException {

            final String MOV_POSTER = "poster_path";
            final String MOV_TITLE = "original_title";
            final String MOV_OVERVIEW = "overview";
            final String MOV_VOTE = "vote_average";
            final String MOV_RELEASE = "release_date";

            JSONObject mov = new JSONObject(moviesJsonStr);

            movie = new Movie("", mov.getString(MOV_POSTER), mov.getString(MOV_TITLE), mov.getString(MOV_OVERVIEW), mov.getString(MOV_VOTE), mov.getString(MOV_RELEASE));
            return movie;
        }

        @Override
        protected Movie doInBackground(String... param){
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                Uri builder = Uri.parse("http://api.themoviedb.org/3/movie").buildUpon()
                    .appendPath(param[0])
                    .appendQueryParameter("api_key", MainActivityFragment.API_KEY)
                    .build();
                URL url = new URL(builder.toString());

                // Create the request to TheMovieDb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Movies JSON string: " + moviesJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getMoviesDataFromJson(moviesJsonStr);
            }catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie results) {
            if(results != null){
                String vote = results.getVote() + "/10";
                title.setText(results.getTitle());
                Picasso.with(getActivity()).load("https://image.tmdb.org/t/p/w185" + results.getPoster_path()).into(poster);
                release.setText(results.getRelease().substring(0,4));
                rating.setText(vote);
                overview.setText(results.getOverview());
            }
        }

    }
}
