package me.stanka.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private GridView gridView;
    private ImageAdapter imgAdapter;
    private ArrayList<Movie> movieList;
    public final static String API_KEY = "";  // insert API KEY from the Movie Database
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridMovies);
        imgAdapter = new ImageAdapter(getActivity());
        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")){
            updateImgs();
        } else{
            movieList = savedInstanceState.getParcelableArrayList("movies");
            imgAdapter.setMovies(movieList);
            gridView.setAdapter(imgAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Intent intent = new Intent(getActivity(), Details.class);
                    //intent.putExtra(Intent.EXTRA_TEXT, movieList.get(position).getId());
                    intent.putExtra(Intent.EXTRA_TEXT, movieList.get(position)); // sends parcable object
                    startActivity(intent);
                }
            });
        }
        return rootView;
    }

    private void updateImgs(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = sharedPref.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        new FetchMoviesTask().execute(sort);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private ArrayList<Movie> getMoviesDataFromJson(String moviesJsonStr)
                throws JSONException {

            final String MOV_RESULTS = "results";
            final String MOV_POSTER = "poster_path";
            final String MOV_ID = "id";
            final String MOV_TITLE = "original_title";
            final String MOV_OVERVIEW = "overview";
            final String MOV_VOTE = "vote_average";
            final String MOV_RELEASE = "release_date";

            JSONObject rootObject = new JSONObject(moviesJsonStr);
            JSONArray resultsArray = rootObject.getJSONArray(MOV_RESULTS);

            movieList = new ArrayList<Movie>();
            for(int i = 0; i < resultsArray.length(); i++) {
                JSONObject movie = resultsArray.getJSONObject(i);
                movieList.add(i, new Movie(movie.getString(MOV_ID), movie.getString(MOV_POSTER), movie.getString(MOV_TITLE), movie.getString(MOV_OVERVIEW), movie.getString(MOV_VOTE), movie.getString(MOV_RELEASE)));
            }
            return movieList;
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... param){
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                Uri builder = Uri.parse("http://api.themoviedb.org/3/movie").buildUpon()
                        .appendPath(param[0])
                        .appendQueryParameter("api_key", API_KEY)
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
        protected void onPostExecute(final ArrayList<Movie> results) {
            if(results != null){
                imgAdapter.setMovies(results);
                gridView.setAdapter(imgAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        Intent intent = new Intent(getActivity(), Details.class);
//                        intent.putExtra(Intent.EXTRA_TEXT, results.get(position).getId());
                        intent.putExtra(Intent.EXTRA_TEXT, results.get(position)); // sends parcable object
                        startActivity(intent);
                    }
                });
            }
        }

    }
}
