package com.pustovit.tasktimer;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.pustovit.tasktimer.AppProvider.CONTENT_AUTHORITY_URI;

/**
 * Created by Pustovit Vladimir on 08.08.2019.
 * vovapust1989@gmail.com
 */

public class TasksContract {
    static final String TABLE_NAME = "Tasks";

    //Tasks fields

    /**
     * Uri to access the Tasks table.
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_URI + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_URI + "." + TABLE_NAME;

    public static Uri buildTaskUri(long taskId) {
        return ContentUris.withAppendedId(CONTENT_URI, taskId);
    }

    public static long getTaskId(Uri uri) {
        return ContentUris.parseId(uri);
    }

    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String TASKS_NAME = "Name";
        public static final String TASKS_DESCRIPTION = "Description";
        public static final String TASK_SORTORDER = "SortOrder";

        private Columns() {
            //private constructor to prevent instantiation
        }
    }
}
