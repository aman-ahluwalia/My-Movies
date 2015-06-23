package com.example.aman.moviesapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by aman on 20/06/15.
 */
public class gridImageAdapter extends BaseAdapter{

    private static final String LOG_TAG = gridImageAdapter.class.getSimpleName();

    private ArrayList<String> mObjects;
    private int mResource;
    private int mFieldId = 0;
    private boolean mNotifyOnChange = true;
    private final Object mLock = new Object();
    private Context mContext;
    private LayoutInflater mInflater;
    // private ArrayList<String> mOriginalValues;
    //private ArrayFilter mFilter;

    final String POSTER_PATH_CONSTANT = "http://image.tmdb.org/t/p/";
    final String POSTER_SIZE = "w185/";

    public gridImageAdapter(Context context, int resource, int imageViewResourceId, ArrayList<String> objects){
        mContext = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;
        mObjects = objects;
        mFieldId = imageViewResourceId;
        Log.v(LOG_TAG, "check inside const " );

    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    public void add(String object) {
        synchronized (mLock) {
            mObjects.add(object);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void addAll(String ... items) {
        synchronized (mLock) {
            Collections.addAll(mObjects,items);
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        synchronized (mLock) {
            mObjects.clear();
        }
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mNotifyOnChange = true;
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v(LOG_TAG, "check inside getview " );
        View view;
        if (convertView == null) {
            view = new View(mContext);
            view = mInflater.inflate(mResource, null);

        } else {
            view = (View) convertView;
        }
        Log.v(LOG_TAG, "checkjui");
        ImageView image = (ImageView) view.findViewById(mFieldId);
        String url = POSTER_PATH_CONSTANT + POSTER_SIZE + getItem(position).toString();
        Log.v(LOG_TAG,"aman: " + url);
        Picasso.with(mContext).load(url).into(image);
        Log.v(LOG_TAG,"anshi");
        return view;
    }
}
