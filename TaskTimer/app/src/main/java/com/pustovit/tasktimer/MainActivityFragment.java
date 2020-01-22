package com.pustovit.tasktimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.security.InvalidParameterException;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, CursorRecyclerViewAdapter.OnTaskClickListener {
    private static final String TAG = "MainActivityFragTag";

    public static final int LOADER_ID = 0;

    private CursorRecyclerViewAdapter mAdapter;
    private Timing mCurrentTiming = null;

    public MainActivityFragment() {
        Log.d(TAG, "MainActivityFragment: starts");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        if (!(activity instanceof CursorRecyclerViewAdapter.OnTaskClickListener)) {
            if (activity != null) {
                throw new ClassCastException(activity.getClass().getSimpleName() +
                        " must implements CursorRecyclerViewAdapter.OnTaskClickListener interface");
            }
        }
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
        setTimingText(mCurrentTiming);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.tasks_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (mAdapter == null) {
            mAdapter = new CursorRecyclerViewAdapter(null, this);
        }
        recyclerView.setAdapter(mAdapter);
        return view;
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader: starts with id: " + id);

        switch (id) {
            case LOADER_ID:
                String[] projection = {TasksContract.Columns._ID, TasksContract.Columns.TASKS_NAME,
                        TasksContract.Columns.TASKS_DESCRIPTION, TasksContract.Columns.TASK_SORTORDER};

                String sortOrder = TasksContract.Columns.TASK_SORTORDER + ", " + TasksContract.Columns.TASKS_NAME + " COLLATE NOCASE";

                return new CursorLoader(getActivity(), TasksContract.CONTENT_URI, projection, null, null, sortOrder);

            default:
                throw new InvalidParameterException(TAG + " onCreateLoader() called with invalid loader id " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: starts");
        Cursor oldCursor = mAdapter.swapCursor(data);
        if (oldCursor != null) {
            oldCursor.close();
        }
        int count = mAdapter.getItemCount();
        Log.d(TAG, "onLoadFinished: count is " + count);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");
        Cursor oldCursor = mAdapter.swapCursor(null);
        if (oldCursor != null) {
            oldCursor.close();
        }

    }

    @Override
    public void onEditClick(@NonNull Task task) {
        Log.d(TAG, "onEditClick: called");
        CursorRecyclerViewAdapter.OnTaskClickListener listener = (CursorRecyclerViewAdapter.OnTaskClickListener) getActivity();
        if (listener != null) {
            listener.onEditClick(task);
        }
    }

    @Override
    public void onDeleteClick(@NonNull Task task) {
        Log.d(TAG, "onDeleteClick: called");
        CursorRecyclerViewAdapter.OnTaskClickListener listener = (CursorRecyclerViewAdapter.OnTaskClickListener) getActivity();
        if (listener != null) {
            listener.onDeleteClick(task);
        }
    }

    @Override
    public void onTaskLongClick(@NonNull Task task) {
        Log.d(TAG, "onTaskLongClick: called");
        if (mCurrentTiming != null) {
            if (task.getId() == mCurrentTiming.getTask().getId()) {
                //the current task was tapped a second time, so stop timing
                saveTiming(mCurrentTiming);
                mCurrentTiming = null;
                setTimingText(null);
            } else {
                // a new task is being timed, so stop the old one first.
                saveTiming(mCurrentTiming);
                mCurrentTiming = new Timing(task);
                setTimingText(mCurrentTiming);
            }
        } else {
            mCurrentTiming = new Timing(task);
            setTimingText(mCurrentTiming);
        }

    }

    private void saveTiming(@NonNull Timing currentTiming) {
        Log.d(TAG, "saveTiming: starts");
        currentTiming.setDuration();

        ContentResolver contentResolver = getActivity().getContentResolver();
        ContentValues contentValues = new ContentValues();
     //   contentValues.put(TimingsContract.Columns._ID, currentTiming.getId());
        contentValues.put(TimingsContract.Columns.TIMINGS_TASK_ID, currentTiming.getTask().getId());
        contentValues.put(TimingsContract.Columns.TIMINGS_START_TIME, currentTiming.getStartTime());
        contentValues.put(TimingsContract.Columns.TIMINGS_DURATION, currentTiming.getDuration());

        contentResolver.insert(TimingsContract.CONTENT_URI, contentValues);
        Log.d(TAG, "saveTiming: exit.");
    }

    private void setTimingText(Timing timing) {
        TextView tvTaskName = getActivity().findViewById(R.id.tv_current_task);
        if (timing != null) {
            tvTaskName.setText(getString(R.string.show_current_timing_name, mCurrentTiming.getTask().getName()));
        } else {
            tvTaskName.setText(R.string.no_task_message);
        }
    }

}
