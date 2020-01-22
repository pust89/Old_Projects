package com.pustovit.tasktimer;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Pustovit Vladimir on 27.10.2019.
 * vovapust1989@gmail.com
 */

public class DurationsRVAdapter extends RecyclerView.Adapter<DurationsRVAdapter.ViewHolder> {

    private Cursor mCursor;
    private final java.text.DateFormat mDateFormat; //module level so we don't keep instantiating in bindView

    public DurationsRVAdapter(Cursor cursor) {
        this.mCursor = cursor;
        mDateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_durations_items, parent, false);
        return new DurationsRVAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if((mCursor != null)&& (mCursor.getCount() > 0)){
            if(!mCursor.moveToPosition(position)){
                throw new IllegalStateException("Couldn't move cursor to position "+ position);
            }

            String name = mCursor.getString(mCursor.getColumnIndex(DurationsContract.Columns.DURATIONS_NAME));
            String decription = mCursor.getString(mCursor.getColumnIndex(DurationsContract.Columns.DURATIONS_DESCRIPTION));
            Long startTime = mCursor.getLong(mCursor.getColumnIndex(DurationsContract.Columns.DURATIONS_START_TIME));
            long totalDuration = mCursor.getLong(mCursor.getColumnIndex(DurationsContract.Columns.DURATIONS_DURATION));

            holder.tvName.setText(name);
            if(holder.tvDescription != null){
                holder.tvDescription.setText(decription);
            }

            holder.tvStartDate.setText(mDateFormat.format(startTime*1000));
           // holder.tvStartDate.setText(startTime.toString());
            holder.tvDuration.setText(formatDuration(totalDuration));
        }

    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    private String formatDuration(long duration){
        //duration is in seconds, converte to h:m:s
        long h = duration/3600;
        long remainder = duration - (h*3600);
        long m = remainder/60;
        long s = remainder - (m*60);
        return String.format(Locale.getDefault(), "%02d:%02d:%02d",h,m,s);
    }
    /**
     * Swap in a new Cursor, returning old Cursor.
     * The returned old Cursor is <em>NOT</em> closed.
     *
     * @param newCursor The new Cursor to be used.
     * @return Returns the previously set Cursor, or null if there wasn't one.
     * If the given Cursor is the same instance as the previously set Cursor,
     * null is also returned.
     */
    Cursor swapCursor(Cursor newCursor) {
        if (mCursor == newCursor) {
            return null;
        }

        int numItems = getItemCount();//stored old item counts;

        final Cursor oldCursor = mCursor;
        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, numItems);
        }
        return oldCursor;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvDuration;
        TextView tvStartDate;
        TextView tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvName = itemView.findViewById(R.id.td_name);
            this.tvStartDate = itemView.findViewById(R.id.td_start);
            this.tvDuration = itemView.findViewById(R.id.td_duration);
            this.tvDescription = itemView.findViewById(R.id.td_description);

        }
    }


}
