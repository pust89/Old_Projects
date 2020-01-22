package com.pustovit.tasktimer;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.google.android.material.internal.NavigationMenu;

import static com.pustovit.tasktimer.AppProvider.CONTENT_AUTHORITY_URI;

/**
 * Created by Pustovit Vladimir on 31.08.2019.
 * vovapust1989@gmail.com
 */

public class DurationsContract {
    static final String TABLE_NAME = "vwTaskDurations";

    /**
     * Uri to access Durations view.
     */

    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);
    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd."+CONTENT_URI+"."+TABLE_NAME;
    static final String CONTENT_ITEM_TYPE="vnd.android.cursor.item/vnd."+CONTENT_URI+"."+TABLE_NAME;



    public static long getDurationId(Uri uri){
        return ContentUris.parseId(uri);
    }

    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String DURATIONS_NAME = TasksContract.Columns.TASKS_NAME;
        public static final String DURATIONS_DESCRIPTION = TasksContract.Columns.TASKS_DESCRIPTION;
        public static final String DURATIONS_START_TIME = TimingsContract.Columns.TIMINGS_START_TIME;
        public static final String DURATIONS_START_DATE = "StartDate";
        public static final String DURATIONS_DURATION = TimingsContract.Columns.TIMINGS_DURATION;

        private Columns(){
            //private constructor to prevent instantiation
        }
    }
}
