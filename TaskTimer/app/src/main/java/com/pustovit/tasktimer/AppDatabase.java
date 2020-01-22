package com.pustovit.tasktimer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Pustovit Vladimir on 08.08.2019.
 * vovapust1989@gmail.com
 * <p>
 * Basic database class for the application.
 * <p>
 * The only class that should use this is {@link AppProvider}.
 */

class AppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "AppDatabaseTag";
    public static final String DATABASE_NAME = "TaskTimer.db";
    public static final int DATABASE_VERSION = 3;

    //Implement AppDatabase as singleton.
    private static AppDatabase instance = null;

    private AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "AppDatabase: constructor");
    }

    /**
     * Get an instance of the app's singleton database helper object.
     *
     * @param context the content provider's context.
     * @return a SQLite database helper object.
     */
    static AppDatabase getInstance(Context context) {
        if (instance == null) {
            return new AppDatabase(context);
        }
        return instance;

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate: starts");
        String sSQL; //Use a string variable to facilitate logging;

        sSQL = "CREATE TABLE " + TasksContract.TABLE_NAME + " ("
                + TasksContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + TasksContract.Columns.TASKS_NAME + " TEXT NOT NULL, "
                + TasksContract.Columns.TASKS_DESCRIPTION + " TEXT, "
                + TasksContract.Columns.TASK_SORTORDER + " INTEGER);";
        Log.d(TAG, sSQL);
        sqLiteDatabase.execSQL(sSQL);

        //Added in version 2
        //______________________________________________________
        upgrade_DB_v1_to_v2(sqLiteDatabase);
        //______________________________________________________
        //Added in version 3
        upgrade_DB_v2_to_v3(sqLiteDatabase);
        //______________________________________________________

        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: starts");
        switch (oldVersion) {
            case 1:
                //upgrade logic from version 1 to version 2
                upgrade_DB_v1_to_v2(sqLiteDatabase);
            case 2:
                //upgrade logic from version 2 to version 3
                upgrade_DB_v2_to_v3(sqLiteDatabase);
                break;

            default:
                throw new IllegalStateException("Error in newVersion of database");
        }
        Log.d(TAG, "onUpgrade: ends");
    }

    /**
     * Method to upgrade database from version 1 to version 3.
     * Added Timings table and deleting trigger.
     */
    private void upgrade_DB_v1_to_v2(SQLiteDatabase sqLiteDatabase) {
        String sSQL = "CREATE TABLE " + TimingsContract.TABLE_NAME + " ("
                + TimingsContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + TimingsContract.Columns.TIMINGS_TASK_ID + " INTEGER, "
                + TimingsContract.Columns.TIMINGS_START_TIME + " INTEGER, "
                + TimingsContract.Columns.TIMINGS_DURATION + " INTEGER);";
        Log.d(TAG, sSQL);
        sqLiteDatabase.execSQL(sSQL);

        sSQL = "CREATE TRIGGER Remove_Task"
                + " AFTER DELETE ON " + TasksContract.TABLE_NAME
                + " FOR EACH ROW"
                + " BEGIN"
                + " DELETE FROM " + TimingsContract.TABLE_NAME
                + " WHERE " + TimingsContract.Columns.TIMINGS_TASK_ID
                + " = OLD." + TasksContract.Columns._ID + ";"
                + " END;";
        Log.d(TAG, sSQL);
        sqLiteDatabase.execSQL(sSQL);
    }

    /**
     * Method to upgrade database from version 2 to version 3.
     * Added Durations table view.
     */
    private void upgrade_DB_v2_to_v3(SQLiteDatabase sqLiteDatabase) {
          /*
         CREATE VIEW vwTaskDurations AS
         SELECT Timings._id,
         Tasks.Name,
         Tasks.Description,
         Timings.StartTime,
         DATE(Timings.StartTime, 'unixepoch') AS StartDate,
         SUM(Timings.Duration) AS Duration
         FROM Tasks INNER JOIN Timings
         ON Tasks._id = Timings.TaskId
         GROUP BY Tasks._id, StartDate;
         */

        String sSQL = "CREATE VIEW " + DurationsContract.TABLE_NAME
                + " AS SELECT " + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns._ID + ", "
                + TasksContract.TABLE_NAME + "." + TasksContract.Columns.TASKS_NAME + ", "
                + TasksContract.TABLE_NAME + "." + TasksContract.Columns.TASKS_DESCRIPTION + ", "
                + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_START_TIME + ","
                + " DATE(" + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_START_TIME + ", 'unixepoch')"
                + " AS " + DurationsContract.Columns.DURATIONS_START_DATE + ","
                + " SUM(" + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_DURATION + ")"
                + " AS " + DurationsContract.Columns.DURATIONS_DURATION
                + " FROM " + TasksContract.TABLE_NAME + " JOIN " + TimingsContract.TABLE_NAME
                + " ON " + TasksContract.TABLE_NAME + "." + TasksContract.Columns._ID + " = "
                + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMINGS_TASK_ID
                + " GROUP BY " + DurationsContract.Columns.DURATIONS_START_DATE + ", " + DurationsContract.Columns.DURATIONS_NAME
                + ";";
        Log.d(TAG, sSQL);
        sqLiteDatabase.execSQL(sSQL);
    }
}
