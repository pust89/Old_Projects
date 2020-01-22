package com.pustovit.tasktimer;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Pustovit Vladimir on 06.10.2019.
 * vovapust1989@gmail.com
 */

class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.TaskViewHolder> {
    private static final String TAG = "CurRecViewAdapterTag";
    private Cursor mCursor;
    private OnTaskClickListener mListener;


    interface OnTaskClickListener {
        void onEditClick(@NonNull Task task);

        void onDeleteClick(@NonNull Task task);

        void onTaskLongClick(@NonNull Task task);
    }

    public CursorRecyclerViewAdapter(Cursor cursor, CursorRecyclerViewAdapter.OnTaskClickListener listener) {
        Log.d(TAG, "CursorRecyclerViewAdapter: constructor()");
        mCursor = cursor;
        mListener = listener;
    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //    Log.d(TAG, "onCreateViewHolder: starts.");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, parent, false);
        TaskViewHolder taskViewHolder = new TaskViewHolder(view);
        return taskViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
//        Log.d(TAG, "onBindViewHolder: starts");
        if (mCursor == null || (mCursor.getCount() == 0)) {
//            Log.d(TAG, "onBindViewHolder: providing instructions");
            holder.name.setText(R.string.instructions_heading);
            holder.description.setText(R.string.instructions);
            holder.delete.setVisibility(View.GONE);
            holder.edit.setVisibility(View.GONE);
        } else {
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Could not move cursor to position " + position);
            }

            final Task task = new Task(mCursor.getLong(mCursor.getColumnIndex(TasksContract.Columns._ID)),
                    mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASKS_NAME)),
                    mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASKS_DESCRIPTION)),
                    mCursor.getInt(mCursor.getColumnIndex(TasksContract.Columns.TASK_SORTORDER)));

            holder.name.setText(task.getName());
            holder.description.setText(task.getDescription());
            holder.edit.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.VISIBLE);


            View.OnClickListener buttonListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.item_btn_edit:
                            if (mListener != null) {
                                mListener.onEditClick(task);
                            }
                            break;
                        case R.id.item_btn_delete:
                            if (mListener != null) {
                                mListener.onDeleteClick(task);
                            }
                            break;
                        default:
                            break;
                    }
                }
            };

            View.OnLongClickListener buttonLongClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mListener != null) {
                        mListener.onTaskLongClick(task);
                        return true;
                    }
                    return false;
                }
            };

        holder.edit.setOnClickListener(buttonListener);
        holder.delete.setOnClickListener(buttonListener);
        holder.itemView.setOnLongClickListener(buttonLongClickListener);

    }
}

    @Override
    public int getItemCount() {
        if (mCursor == null || mCursor.getCount() == 0) {
            return 1;
        } else {
            return mCursor.getCount();
        }
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

static class TaskViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "TaskViewHolderTag";
    TextView name;
    TextView description;
    ImageButton edit;
    ImageButton delete;
    View itemView;

    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);
        //        Log.d(TAG, "TaskViewHolder: constructor()");
        this.name = (TextView) itemView.findViewById(R.id.item_tv_name);
        this.description = (TextView) itemView.findViewById(R.id.item_tv_description);
        this.edit = (ImageButton) itemView.findViewById(R.id.item_btn_edit);
        this.delete = (ImageButton) itemView.findViewById(R.id.item_btn_delete);
        this.itemView = itemView;
    }
}
}
