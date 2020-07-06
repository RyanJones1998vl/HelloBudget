package com.example.myapplication.db;

import android.content.Context;
import android.database.Cursor;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.myapplication.db.adapters.DatabaseAdapter;

public abstract class DatabaseCursorLoader  extends CursorLoader {
    private Cursor cursor=null;

    protected DatabaseAdapter databaseAdapter=null;

    protected final Loader.ForceLoadContentObserver observer;

    public DatabaseCursorLoader(Context context){
        super(context);
        observer=new ForceLoadContentObserver();
    }
    public abstract Cursor loadInBackground();
    protected void registerContentObserver(Cursor cursor){
        cursor.registerContentObserver(observer);
    }
    @Override
    public void deliverResult(Cursor data) {
        if (isReset()) {
            if (data != null) {
                onReleaseResources(data);
            }
            return;
        }

        Cursor oldCursor = cursor;
        cursor = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldCursor != null && oldCursor != data && !oldCursor.isClosed()) {
            onReleaseResources(oldCursor);
        }
    }

    @Override
    protected void onStartLoading() {
        if (cursor != null){
            deliverResult(cursor);
        }

        if (takeContentChanged() || cursor == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor data) {
        super.onCanceled(data);
        onReleaseResources(data);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();

        // At this point we can release the resources associated with 'cursor'
        // if needed.
        if (cursor != null && !cursor.isClosed()) {
            onReleaseResources(cursor);
        }
        cursor = null;
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     * @param c {@link Cursor} to be released
     */
    protected void onReleaseResources(Cursor c) {
        if (c != null)
            c.close();
    }

}
