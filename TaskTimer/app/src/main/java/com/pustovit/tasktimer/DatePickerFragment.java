package com.pustovit.tasktimer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Pustovit Vladimir on 28.10.2019.
 * vovapust1989@gmail.com
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "DatePickerFragment";

    public static final String DATE_PICKER_ID = "ID";
    public static final String DATE_PICKER_TITLE = "TITLE";
    public static final String DATE_PICKER_DATE = "DATE";

    int mDialogId = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final GregorianCalendar cal = new GregorianCalendar();
        String title = null;

        Bundle arguments = getArguments();
        if (arguments != null) {
            mDialogId = arguments.getInt(DATE_PICKER_ID);
            title = arguments.getString(DATE_PICKER_TITLE);

            Date givenDAte = (Date) arguments.getSerializable(DATE_PICKER_DATE);
            if (givenDAte != null) {
                cal.setTime(givenDAte);
                Log.d(TAG, "onCreateDialog: retrieved date = " + givenDAte);
            }
        }

        int year = cal.get(GregorianCalendar.YEAR);
        int month = cal.get(GregorianCalendar.MONTH);
        int day = cal.get(GregorianCalendar.DAY_OF_MONTH);

        @SuppressWarnings("ConstantConditions") DatePickerDialog dialog = new DatePickerDialog(getContext(), this, year, month, day);
        if (title != null) {
            dialog.setTitle(title);
        }
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(!(context instanceof DatePickerDialog.OnDateSetListener)){
            throw new ClassCastException(context.toString()+ " not implements DatePickerDialog.OnDateSetListener interface.");
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity();
        if (listener != null) {
            view.setTag(mDialogId);
            listener.onDateSet(view, year, month, dayOfMonth);
        }
    }

}
