package me.stanka.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;


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
 * Created by Stanka on 03.5.2016.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    public AsyncResponse delegate = null;

    public FetchMoviesTask(AsyncResponse asyncResponse){
        delegate = asyncResponse;
    }

    private ArrayList<Movie> getMoviesDataFromJson(String moviesJsonStr)
            throws JSONException {

        ArrayList<Movie> movieList = new ArrayList<Movie>();
        if(moviesJsonStr != null){
            final String MOV_RESULTS = "results";
            final String MOV_POSTER = "poster_path";
            final String MOV_ID = "id";
            final String MOV_TITLE = "original_title";
            final String MOV_OVERVIEW = "overview";
            final String MOV_VOTE = "vote_average";
            final String MOV_RELEASE = "release_date";

            JSONObject rootObject = new JSONObject(moviesJsonStr);
            JSONArray resultsArray = rootObject.getJSONArray(MOV_RESULTS);

            for(int i = 0; i < resultsArray.length(); i++) {
                JSONObject movie = resultsArray.getJSONObject(i);
                movieList.add(i, new Movie(movie.getString(MOV_ID), movie.getString(MOV_POSTER), movie.getString(MOV_TITLE), movie.getString(MOV_OVERVIEW), movie.getString(MOV_VOTE), movie.getString(MOV_RELEASE)));
            }
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
    protected void onPostExecute(final ArrayList<Movie> results) {
        delegate.processFinish(results);
    }

}
