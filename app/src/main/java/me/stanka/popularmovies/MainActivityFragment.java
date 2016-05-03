package me.stanka.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private GridView mGridView;
    private ImageAdapter mImgAdapter;
    private ArrayList<Movie> mMovieList;
    public final static String API_KEY = "";  // insert API KEY from the Movie Database
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    public final static String PARCABLE_KEY = "movie";

    public MainActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(PARCABLE_KEY, mMovieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridMovies);
        mImgAdapter = new ImageAdapter(getActivity());
        mMovieList = new ArrayList<>();
        if(savedInstanceState == null || !savedInstanceState.containsKey(PARCABLE_KEY)){
            updateImgs();
        } else{
            mMovieList = savedInstanceState.getParcelableArrayList(PARCABLE_KEY);
            if(mMovieList.size() > 0){
                mImgAdapter.setMovies(mMovieList);
                mGridView.setAdapter(mImgAdapter);
                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        Intent intent = new Intent(getActivity(), Details.class);
                        intent.putExtra(Intent.EXTRA_TEXT, mMovieList.get(position)); // sends parcable object
                        startActivity(intent);
                    }
                });
            }
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mMovieList.size() == 0){
            updateImgs();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mMovieList.clear();
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateImgs(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = sharedPref.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        new FetchMoviesTask(new AsyncResponse() {
            @Override
            public void processFinish(final ArrayList<Movie> results) {
                if(results != null){
                    mMovieList = results;
                    mImgAdapter.setMovies(results);
                    mGridView.setAdapter(mImgAdapter);
                    mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {
                            Intent intent = new Intent(getActivity(), Details.class);
                            intent.putExtra(Intent.EXTRA_TEXT, results.get(position)); // sends parcable object
                            startActivity(intent);
                        }
                    });
                } else{
                    Toast toast = Toast.makeText(getActivity(), "Something went wrong, please check your internet connection and try again", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }).execute(sort);
    }

}
