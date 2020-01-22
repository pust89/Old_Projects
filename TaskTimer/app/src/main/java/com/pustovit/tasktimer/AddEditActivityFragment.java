package com.pustovit.tasktimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddEditActivityFragment extends Fragment{
    private static final String TAG = "AddEditActFragmentTag";

    private enum FragmentEditMode {ADD, EDIT}

    private FragmentEditMode mMode;

    private EditText etName;
    private EditText etDescription;
    private EditText etSortOrder;
    private OnSaveClicked mSaveListener = null;

    interface  OnSaveClicked{
        void onSaveClicked();
    }
    public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: constructor called");
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: starts");
        Activity activity = getActivity();
        if(!(activity instanceof OnSaveClicked)){
            if (activity != null) {
                throw new ClassCastException(activity.getClass().getSimpleName()+
                        " must implements AddEditActivityFragment.OnSaveClicked interface");
            }
        } else {
            mSaveListener = (OnSaveClicked) activity;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        @SuppressWarnings("ConstantConditions") ActionBar actionBar =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }



    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: starts");
        super.onDetach();
        mSaveListener = null;

        @SuppressWarnings("ConstantConditions") ActionBar actionBar =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);
        etName = view.findViewById(R.id.addedit_name);
        etDescription = view.findViewById(R.id.addedit_description);
        etSortOrder = view.findViewById(R.id.addedit_sortorder);
        Button btnSave = view.findViewById(R.id.addedit_btn_save);

        Bundle arguments = getArguments();

        final Task task;

        if (arguments != null) {
            Log.d(TAG, "onCreateView: retrieving task details");
            task = arguments.getParcelable(Task.class.getSimpleName());

            if (task != null) {
                Log.d(TAG, "onCreateView: Task details found, editing...");
                etName.setText(task.getName());
                etDescription.setText(task.getDescription());
                etSortOrder.setText(Integer.toString(task.getSortOrder()));
                mMode = FragmentEditMode.EDIT;
            } else {
                mMode = FragmentEditMode.ADD;
            }
        } else {
            task = null;
            Log.d(TAG, "onCreateView: No arguments, adding new record.");
            mMode = FragmentEditMode.ADD;
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int so;

                if (etSortOrder.length() > 0) {
                    so = Integer.parseInt(etSortOrder.getText().toString());
                } else {
                    so = 0;
                }

                @SuppressWarnings("ConstantConditions") ContentResolver contentResolver = getActivity().getContentResolver();
                ContentValues cv = new ContentValues();

                switch (mMode) {
                    case EDIT:
                        if(task == null){
                            // remove lint warnings, will never execute
                            break;
                        }
                        if (!etName.getText().toString().equals(task.getName())) {
                            cv.put(TasksContract.Columns.TASKS_NAME, etName.getText().toString());
                        }

                        if (!etDescription.getText().toString().equals(task.getDescription())) {
                            cv.put(TasksContract.Columns.TASKS_DESCRIPTION, etDescription.getText().toString());
                        }
                        if (so != task.getSortOrder()) {
                            cv.put(TasksContract.Columns.TASK_SORTORDER, so);
                        }

                        if (cv.size() != 0) {
                            Log.d(TAG, "onClick: updating date.");
                            contentResolver.update(TasksContract.buildTaskUri(task.getId()), cv, null, null);
                        }
                        break;

                    case ADD:
                        if (etName.length() > 0) {
                            Log.d(TAG, "onClick: adding new task");
                            cv.put(TasksContract.Columns.TASKS_NAME, etName.getText().toString());
                            cv.put(TasksContract.Columns.TASKS_DESCRIPTION, etDescription.getText().toString());
                            cv.put(TasksContract.Columns.TASK_SORTORDER, so);
                            contentResolver.insert(TasksContract.CONTENT_URI, cv);
                        }
                        break;
                }

                if(mSaveListener != null){
                    mSaveListener.onSaveClicked();
                }
                Log.d(TAG, "onClick: ends");
            }
        });
        Log.d(TAG, "onCreateView: exiting...");
        return view;
    }


    boolean canClose(){
        return false;//TODO stub!
    }



}
