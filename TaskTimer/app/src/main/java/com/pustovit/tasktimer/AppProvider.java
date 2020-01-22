package com.pustovit.tasktimer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

/**
 * Created by Pustovit Vladimir on 15.08.2019.
 * vovapust1989@gmail.com
 * <p>
 * Provider for the TaskTimer app. The only class that knows about the {@link AppDatabase}.
 */

public class AppProvider extends ContentProvider {
    private static final String TAG = "AppProviderTag";

    private AppDatabase mOpenHelper;

    static final String CONTENT_AUTHORITY = "com.pustovit.tasktimer.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int TASKS = 100;
    private static final int TASKS_ID = 101;

    private static final int TIMINGS = 200;
    private static final int TIMINGS_ID = 201;

    /*  private static int TASK_TIMINGS = 300;
        private static int TASK_TIMINGS_ID=310;
    */
    private static final int TASK_DURATIONS = 400;
    private static final int TASK_DURATIONS_ID = 401;


    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        //eg. com.pustovit.tasktimer.provider/Tasks
        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME, TASKS);

        //eg. com.pustovit.tasktimer.provider/Tasks/8
        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME + "/#", TASKS_ID);


        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME, TIMINGS);
        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME + "/#", TIMINGS_ID);


        matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME, TASK_DURATIONS);
        matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME + "/#", TASK_DURATIONS_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate: starts");
        mOpenHelper = AppDatabase.getInstance(getContext());
        Log.d(TAG, "onCreate: ends");
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query: called with URI " + uri);

        int match = sUriMatcher.match(uri);
        Log.d(TAG, "query: match is " + match);


        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (match) {
            case TASKS:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                break;

            case TASKS_ID:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                long taskId = TasksContract.getTaskId(uri);
                queryBuilder.appendWhere(TasksContract.Columns._ID + " = " + taskId);
                break;

///////////////////////////////////////////////////////////////////////////////

            case TIMINGS:
                queryBuilder.setTables(TimingsContract.TABLE_NAME);
                break;

            case TIMINGS_ID:
                queryBuilder.setTables(TimingsContract.TABLE_NAME);
                long timingId = TimingsContract.getTimingId(uri);
                queryBuilder.appendWhere(TimingsContract.Columns._ID + " = " + timingId);
                break;
/////////////////////////////////////////////////////////////////////////////////////

            case TASK_DURATIONS:
                queryBuilder.setTables(DurationsContract.TABLE_NAME);
                break;

            case TASK_DURATIONS_ID:
                queryBuilder.setTables(DurationsContract.TABLE_NAME);
                long durationId = DurationsContract.getDurationId(uri);
                queryBuilder.appendWhere(DurationsContract.Columns._ID + " = " + durationId);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase database = mOpenHelper.getReadableDatabase();
        //return queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
        //noinspection ConstantConditions
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return TasksContract.CONTENT_TYPE;

            case TASKS_ID:
                return TasksContract.CONTENT_ITEM_TYPE;

            case TIMINGS:
                return TimingsContract.CONTENT_TYPE;

            case TIMINGS_ID:
                return TimingsContract.CONTENT_ITEM_TYPE;

            case TASK_DURATIONS:
                return DurationsContract.CONTENT_TYPE;

            case TASK_DURATIONS_ID:
                return DurationsContract.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }

    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        Log.d(TAG, "insert() starts with Uri: " + uri);
        int match = sUriMatcher.match(uri);
        Log.d(TAG, "match: " + match);

        SQLiteDatabase database;

        Uri returnUri;
        long recordId;

        switch (match) {
            case TASKS:
                database = mOpenHelper.getWritableDatabase();
                recordId = database.insert(TasksContract.TABLE_NAME, null, contentValues);
                if (recordId >= 0) {
                    returnUri = TasksContract.buildTaskUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            case TIMINGS:
                database = mOpenHelper.getWritableDatabase();
                recordId = database.insert(TimingsContract.TABLE_NAME, null, contentValues);
                if (recordId >= 0) {
                    returnUri = TimingsContract.buildTimingUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown Uri:" + uri);
        }

        //noinspection ConstantConditions
        if (database != null) {
            database.close();
        }

        //noinspection ConstantConditions
        if (recordId >= 0) {
            //something was inserted
            Log.d(TAG, "insert: Setting notifyChanged with uri: " + uri);
            //noinspection ConstantConditions
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "insert: nothing inserted");
        }
        Log.d(TAG, "insert() ends, return: " + returnUri);
        return returnUri;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete() starts with Uri:" + uri);
        int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        SQLiteDatabase database;
        int count;
        String selectionCriteria;
        switch (match) {
            case TASKS:
                database = mOpenHelper.getWritableDatabase();
                count = database.delete(TasksContract.TABLE_NAME, selection, selectionArgs);
                break;

            case TASKS_ID:
                database = mOpenHelper.getWritableDatabase();
                long taskId = TasksContract.getTaskId(uri);
                selectionCriteria = TasksContract.Columns._ID + "=" + taskId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = database.delete(TasksContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            case TIMINGS:
                database = mOpenHelper.getWritableDatabase();
                count = database.delete(TimingsContract.TABLE_NAME, selection, selectionArgs);
                break;

            case TIMINGS_ID:
                database = mOpenHelper.getWritableDatabase();
                long timingId = TimingsContract.getTimingId(uri);
                selectionCriteria = TimingsContract.Columns._ID + "=" + timingId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = database.delete(TimingsContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown Uri:" + uri);
        }

        if (count > 0) {
            Log.d(TAG, "delete: Setting notifyChanged with uri: " + uri);
            //noinspection ConstantConditions
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "delete: No rows was deleted");
        }
        Log.d(TAG, "delete() ends, return: " + count);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        Log.d(TAG, "update() starts with Uri:" + uri);
        int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        SQLiteDatabase database;
        int count;
        String selectionCriteria;
        switch (match) {
            case TASKS:
                database = mOpenHelper.getWritableDatabase();
                count = database.update(TasksContract.TABLE_NAME, contentValues, selection, selectionArgs);
                break;

            case TASKS_ID:
                database = mOpenHelper.getWritableDatabase();
                long taskId = TasksContract.getTaskId(uri);
                selectionCriteria = TasksContract.Columns._ID + "=" + taskId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = database.update(TasksContract.TABLE_NAME, contentValues, selectionCriteria, selectionArgs);
                break;

            case TIMINGS:
                database = mOpenHelper.getWritableDatabase();
                count = database.update(TimingsContract.TABLE_NAME, contentValues, selection, selectionArgs);
                break;

            case TIMINGS_ID:
                database = mOpenHelper.getWritableDatabase();
                long timingId = TimingsContract.getTimingId(uri);
                selectionCriteria = TimingsContract.Columns._ID + "=" + timingId;

                if (selection != null && selection.length() > 0) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = database.update(TimingsContract.TABLE_NAME, contentValues, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown Uri:" + uri);
        }

        if (count > 0) {
            Log.d(TAG, "update: Setting notifyChanged with uri: " + uri);
            //noinspection ConstantConditions
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "update: nothing updated");
        }
        Log.d(TAG, "update() ends, return: " + count);
        return count;
    }
}
