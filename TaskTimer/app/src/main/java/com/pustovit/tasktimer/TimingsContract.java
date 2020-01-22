package com.pustovit.tasktimer;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


import static com.pustovit.tasktimer.AppProvider.CONTENT_AUTHORITY_URI;

/**
 * Created by Pustovit Vladimir on 31.08.2019.
 * vovapust1989@gmail.com
 */

public class TimingsContract {
    static final String TABLE_NAME = "Timings";

    /**
     * Uri to access Timings table.
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_URI + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_URI + "." + TABLE_NAME;

    public static Uri buildTimingUri(long timingId) {
        return ContentUris.withAppendedId(CONTENT_URI, timingId);
    }

    public static long getTimingId(Uri uri) {
        return ContentUris.parseId(uri);
    }


    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String TIMINGS_TASK_ID = "TaskId";
        public static final String TIMINGS_START_TIME = "StartTime";
        public static final String TIMINGS_DURATION = "Duration";

        private Columns() {
            //private constructor to prevent instantiation
        }
    }
}
