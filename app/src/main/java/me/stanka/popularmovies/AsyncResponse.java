package me.stanka.popularmovies;

import java.util.ArrayList;

/**
 * Created by Stanka on 03.5.2016.
 */
public interface AsyncResponse {
    void processFinish(ArrayList<Movie> output);
}
