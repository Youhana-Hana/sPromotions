package com.MobiSeeker.PrescriptionWatcher.Fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

public class TimePickerFragment extends DialogFragment {

    public TimePickerFragment() {
        }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int hour = getArguments().getInt("hour");
        int minute = getArguments().getInt("minute");

        return new TimePickerDialog(getActivity(), null, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }
}
