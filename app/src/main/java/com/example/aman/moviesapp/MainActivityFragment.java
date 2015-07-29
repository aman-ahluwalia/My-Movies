package com.example.aman.moviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    com.example.aman.moviesapp.gridImageAdapter mMoviesAdapter;

    //log defined for FetchMovieData class
    private static final String LOG_TAG = FetchMovieData.class.getSimpleName();

    //Constants for the value field for movie api
    //final String MOST_POPULAR = "popularity.desc";
    final String MY_KEY = "TO BE INSERTED BY USER";

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        UpdateTask();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //MenuInflater inflater = getMenuInflater();
        super.onCreateOptionsMenu(menu, inflater);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        mMoviesAdapter = new com.example.aman.moviesapp.gridImageAdapter(getActivity(),
                R.layout.grid_item_movies,
                R.id.grid_item_movies_imageview,
                new ArrayList<String>());
        gridView.setAdapter(mMoviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),DetailsActivity.class);
                intent.putExtra("MOVIE_INDEX",position);
                getActivity().startActivity(intent);
            }
        });
        return rootView;
    }

    private void UpdateTask() {
        FetchMovieData movieData = new FetchMovieData();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = prefs.getString(getString((R.string.pref_sort_key)),
                getString(R.string.pref_sort_popular));
        movieData.execute(sort);
    }



    private class FetchMovieData extends AsyncTask<String, Void, String[]> {

        private String[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {
            //  String[] movie_result = "";
            final String API_RESULTS = "results";
            final String API_POSTER_PATH = "poster_path";

            JSONObject jsonObject = new JSONObject(movieJsonStr);
            JSONArray moviesArray = jsonObject.getJSONArray(API_RESULTS);

            int num_movies = moviesArray.length();

            String[] movie_result = new String[num_movies];
            for(int i=0; i<num_movies; i++) {
                String poster = moviesArray.getJSONObject(i).getString(API_POSTER_PATH);
                //Log.v(LOG_TAG, "pathpos: " + poster);
                movie_result[i] = poster;
                // Log.v(LOG_TAG, "movie_res: " + movie_result);
            }
            Log.v(LOG_TAG, "Built URI: " + movie_result[6]);

            return movie_result;
        }

        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                //constants to be used as key in a key value pair in uri parsing
                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_MOVIES = "sort_by";
                final String KEY = "api_key";

                // Construct URL for Movie api
                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_MOVIES, params[0])
                        .appendQueryParameter(KEY,MY_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI: " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
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
                movieJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
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
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            Log.v(LOG_TAG, "check " + strings);
            if(strings != null){
                GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
                // FrameLayout frameLayout = (FrameLayout) rootView.findViewById(R.id.frameBackground);
                //Picasso.with(mContext).load(url).into();
                // frameLayout.setBackground();
               /* Log.v(LOG_TAG, "Built URI: " + weekForecast);
                mMoviesAdapter = new gridImageAdapter(getActivity(),
                        R.layout.grid_item_movies,
                        R.id.grid_item_movies_imageview,
                        weekForecast);
                gridView.setAdapter(mMoviesAdapter);
                Log.v(LOG_TAG, "check2 " + strings);*/
                mMoviesAdapter.clear();
                //ArrayList<String> movieData = new ArrayList<String>(Arrays.asList(strings));
                mMoviesAdapter.addAll(strings);
            }

        }
    }

}
