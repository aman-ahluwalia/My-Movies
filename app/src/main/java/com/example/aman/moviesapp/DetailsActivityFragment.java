package com.example.aman.moviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
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
public class DetailsActivityFragment extends Fragment {

    private static final String LOG_TAG = FetchMovie.class.getSimpleName();

    View rootView;

    final String MY_KEY = "88a4d266df2378ba4d48f78c4c8e0a48";

    private int movie_num = 0;

    //constants for loading the image
    final String IMAGE_PATH_CONSTANT = "http://image.tmdb.org/t/p/";
    final String POSTER_SIZE = "w185/";
    final String BACKDROP_SIZE = "w500/";

    //values of different atrributes
    String backdrop = "";
    String poster = "";
    String name = "";
    String synopsis = "";
    String popularity = "";
    String releaseDate = "";

    public DetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        Intent intent = getActivity().getIntent();
        Log.v(LOG_TAG, "in onCreateView 1" );
        if(intent != null) {
             movie_num = intent.getIntExtra("MOVIE_INDEX",0);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sort = prefs.getString(getString(R.string.pref_sort_key),
                                getString(R.string.pref_sort_popular));
             FetchMovie movieDetails = new FetchMovie();
            Log.v(LOG_TAG, "in onCreateView ");
            movieDetails.execute(sort);
        }
        return rootView;
    }

    public int updateAfterAsync() {
        final RelativeLayout relativeLayoutBackdrop = (RelativeLayout) rootView.findViewById(R.id.fragment_detail_back);
        Picasso.with(getActivity()).load(backdrop).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    relativeLayoutBackdrop.setBackgroundDrawable(new BitmapDrawable(bitmap));
                } else {
                    relativeLayoutBackdrop.setBackground(new BitmapDrawable(getResources(), bitmap));
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Toast.makeText(getActivity(), "Failed Loading", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Toast.makeText(getActivity(), "Start Loading", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView imageViewPoster = (ImageView) rootView.findViewById(R.id.imageView);
        Picasso.with(getActivity()).load(poster).into(imageViewPoster);

        TextView textViewName = (TextView) rootView.findViewById(R.id.title);
        textViewName.setText(name);

        TextView textViewSynopsis = (TextView) rootView.findViewById(R.id.synopsis);
        textViewSynopsis.setText(synopsis);

        TextView textViewPopularity = (TextView) rootView.findViewById(R.id.rating_value);
        textViewPopularity.setText(popularity);

        TextView textViewDate = (TextView) rootView.findViewById(R.id.release_year_value);
        textViewDate.setText(releaseDate);
        return 0;
    }

    private class FetchMovie extends AsyncTask<String, Void, Void> {

        private int getMovieDataFromJson(String movieJsonStr)
                throws JSONException {
            //  String[] movie_result = "";
            Log.v(LOG_TAG, "aman " );
            final String API_RESULTS = "results";
            final String API_POSTER_PATH = "poster_path";
            final String API_BACKDROP_PATH = "backdrop_path";
            final String API_ORIGINAL_TITLE = "original_title";
            final String API_SYNOPSIS = "overview";
            final String API_POPULARITY = "vote_average";
            final String API_RELEASE_DATE = "release_date";

            JSONObject jsonObject = new JSONObject(movieJsonStr);
            JSONArray moviesArray = jsonObject.getJSONArray(API_RESULTS);

            backdrop = moviesArray.getJSONObject(movie_num).getString(API_BACKDROP_PATH);
            poster = moviesArray.getJSONObject(movie_num).getString(API_POSTER_PATH);
            name = moviesArray.getJSONObject(movie_num).getString(API_ORIGINAL_TITLE);
            synopsis = moviesArray.getJSONObject(movie_num).getString(API_SYNOPSIS);
            popularity = moviesArray.getJSONObject(movie_num).getString(API_POPULARITY);
            releaseDate = moviesArray.getJSONObject(movie_num).getString(API_RELEASE_DATE);

            //Full image URL
            backdrop = IMAGE_PATH_CONSTANT + BACKDROP_SIZE + backdrop;
            poster = IMAGE_PATH_CONSTANT + POSTER_SIZE + poster;
            Log.v(LOG_TAG, "check detail");
            return 1;
        }

        @Override
        protected Void doInBackground(String... params) {
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
                    getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateAfterAsync();
        }
    }
}
