package com.pustovit.tasktimer;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DurationReport extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        DatePickerDialog.OnDateSetListener, AppDialog.DialogEvents {
    private static final String TAG = "DurationReportTag";
    private static final int LOADER_ID = 1;

    public static final int DIALOG_FILTER = 1;
    public static final int DIALOG_DELETE = 2;

    private static final String SELECTION_PARAM = "SELECTION";
    private static final String SELECTION_ARGS_PARAM = "SELECTION_ARGS";
    private static final String SORT_ORDER_PARAM = "SORT_ORDER";

    public static final String DELETION_DATE = "DELETION_DATE";

    public static final String CURRENT_DATE = "CURRENT_DATE";
    public static final String DISPLAY_WEEK = "DISPLAY_WEEK";

    private Bundle mArgs = new Bundle();
    private boolean mDisplayWeek = true;

    private DurationsRVAdapter mAdapter;
    private final GregorianCalendar mCalendar = new GregorianCalendar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duration_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        applyFilter();

        RecyclerView recyclerView = findViewById(R.id.td_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //create an empty adapter
        if (mAdapter == null) {
            mAdapter = new DurationsRVAdapter(null);
        }
        recyclerView.setAdapter(mAdapter);


        LoaderManager.getInstance(this).initLoader(LOADER_ID, mArgs, this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.rm_filter_period:
                mDisplayWeek = !mDisplayWeek;// was showing a week, so now show a day or vice-versa
                applyFilter();
                invalidateOptionsMenu();//force call to onPrepareOptionsMenu
                LoaderManager.getInstance(this).restartLoader(LOADER_ID, mArgs, this);
                return true;

            case R.id.rm_filter_date:
                showDatePickerDialog("Select date for report", DIALOG_FILTER); // The actual filtering is done in onDateSet();
                return true;

            case R.id.rm_delete:
               showDatePickerDialog("Select date to delete up to", DIALOG_FILTER); // The actual deleting is done in onDateSet();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemFilter = menu.findItem(R.id.rm_filter_period);
        if (mDisplayWeek) {
            itemFilter.setIcon(R.drawable.ic_filter_7_black_24dp);
            itemFilter.setTitle(R.string.show_week);
        } else {
            itemFilter.setTitle(R.string.show_day);

            itemFilter.setIcon(R.drawable.ic_filter_1_black_24dp);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void showDatePickerDialog(String title, int dialogId) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt(DatePickerFragment.DATE_PICKER_ID, dialogId);
        args.putString(DatePickerFragment.DATE_PICKER_TITLE, title);
        args.putSerializable(DatePickerFragment.DATE_PICKER_DATE, mCalendar.getTime());

        datePickerFragment.setArguments(args);

        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void applyFilter() {
        Log.d(TAG, "applyFilter: starts");
        if (mDisplayWeek) {
            Date currentCalendarDate = mCalendar.getTime();

            int dayOfWeek = mCalendar.get(GregorianCalendar.DAY_OF_WEEK);
            int weekStart = mCalendar.getFirstDayOfWeek();
            Log.d(TAG, "applyFilter: first day of calendar week is " + weekStart);
            Log.d(TAG, "applyFilter: day of week is " + dayOfWeek);
            Log.d(TAG, "applyFilter: date is " + mCalendar.getTime());

            // calculate week start and end dates.
            mCalendar.set(GregorianCalendar.DAY_OF_WEEK, weekStart);
            String startDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    mCalendar.get(GregorianCalendar.YEAR),
                    mCalendar.get(GregorianCalendar.MONTH) + 1,
                    mCalendar.get(GregorianCalendar.DAY_OF_MONTH));

            mCalendar.add(GregorianCalendar.DAY_OF_MONTH, 6);//move forward 6 days to get the last day of the week

            String endDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    mCalendar.get(GregorianCalendar.YEAR),
                    mCalendar.get(GregorianCalendar.MONTH) + 1,
                    mCalendar.get(GregorianCalendar.DAY_OF_MONTH));

            String[] selectionArgs = new String[]{startDate, endDate};
            // put the calendar back to where it was before we started jumping back and forth.
            mCalendar.setTime(currentCalendarDate);
            Log.d(TAG, "In applyFilter(7) Start date is " + startDate + " End date is " + endDate);
            mArgs.putString(SELECTION_PARAM, "StartDate Between ? AND ?");
            mArgs.putStringArray(SELECTION_ARGS_PARAM, selectionArgs);

        } else {
            String startDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    mCalendar.get(GregorianCalendar.YEAR),
                    mCalendar.get(GregorianCalendar.MONTH) + 1,
                    mCalendar.get(GregorianCalendar.DAY_OF_MONTH));

            String[] selectionArgs = new String[]{startDate};
            Log.d(TAG, "In applyFilter(1), Start date is " + startDate);
            mArgs.putString(SELECTION_PARAM, "StartDate = ?");
            mArgs.putStringArray(SELECTION_ARGS_PARAM, selectionArgs);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        switch (id) {
            case LOADER_ID:
                String[] projection = {DurationsContract.Columns._ID,
                        DurationsContract.Columns.DURATIONS_NAME,
                        DurationsContract.Columns.DURATIONS_DESCRIPTION,
                        DurationsContract.Columns.DURATIONS_START_TIME,
                        DurationsContract.Columns.DURATIONS_START_DATE,
                        DurationsContract.Columns.DURATIONS_DURATION
                };
                String selection = null;
                String[] selectionArgs = null;
                String sortOrder = null;

                if (args != null) {
                    selection = args.getString(SELECTION_PARAM);
                    selectionArgs = args.getStringArray(SELECTION_ARGS_PARAM);
                    sortOrder = args.getString(SORT_ORDER_PARAM);
                }
                return new CursorLoader(this, DurationsContract.CONTENT_URI, projection, selection, selectionArgs, sortOrder);

            default:
                throw new InvalidParameterException(TAG + " onCreateLoader() called with invalid loader id " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Cursor oldCursor = mAdapter.swapCursor(data);
        if (oldCursor != null) {
            oldCursor.close();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Cursor oldCursor = mAdapter.swapCursor(null);
        if (oldCursor != null) {
            oldCursor.close();
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Toast.makeText(this,"SELECT: "+year+"-"+month+"-"+dayOfMonth,Toast.LENGTH_LONG).show();
        int dialogId = (int) view.getTag();
        switch (dialogId) {
            case DIALOG_FILTER:
                mCalendar.set(year, month, dayOfMonth, 0, 0, 0);
                applyFilter();
                LoaderManager.getInstance(this).restartLoader(LOADER_ID, mArgs, this);
                break;
            case DIALOG_DELETE:
                mCalendar.set(year,month,dayOfMonth,0,0,0);
                String fromDate =DateFormat.getDateInstance().format(mCalendar.getTimeInMillis());
                AppDialog dialog = new AppDialog();
                Bundle args = new Bundle();
                args.putInt(AppDialog.DIALOG_ID,1);
                args.putString(AppDialog.DIALOG_MESSAGE, "Are you sure you want to delete all timings before "+fromDate +"?");
                args.putLong(DELETION_DATE,mCalendar.getTimeInMillis());
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(),null);

                break;
            default:
                throw new IllegalArgumentException("Invalid mode when receiving DatePickerDialog result");
        }
    }

    private void deleteRecords(long timInMillis){
        long longDate = timInMillis/1000;
        String[] selectionArgs = new String[]{Long.toString(longDate)};
        String selection = TimingsContract.Columns.TIMINGS_START_TIME +" < ?";

        ContentResolver contentResolver = getContentResolver();
        contentResolver.delete(TimingsContract.CONTENT_URI, selection, selectionArgs);
        applyFilter();
        LoaderManager.getInstance(this).restartLoader(LOADER_ID,mArgs,this);


    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        long deleteDate = args.getLong(DELETION_DATE);
        deleteRecords(deleteDate);
        LoaderManager.getInstance(this).restartLoader(LOADER_ID,mArgs,this);
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {

    }

    @Override
    public void onDialogCancelled(int dialogId) {

    }
}
