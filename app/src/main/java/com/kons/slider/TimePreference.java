package com.kons.slider;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;


/**
 * Created by Kons on 2017-06-11.
 */

public class TimePreference extends DialogPreference {

    private String hour;
    private String minute;
    private TimePicker picker = null;

    public TimePreference(Context ctxt) {
        this(ctxt, null);
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        this(ctxt, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public TimePreference(Context ctxt, AttributeSet attrs, int defStyle) {
        super(ctxt, attrs, defStyle);

        setPositiveButtonText(R.string.set);
        setNegativeButtonText(R.string.cancel);
        hour = "00";
        minute = "00";
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        return (picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setHour(Integer.valueOf(hour));
        picker.setMinute(Integer.valueOf(minute));
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            hour = getHour(picker);
            minute = getMinute(picker);

            String time = hour + ":" + minute;
            //setSummary(getSummary());
            if (callChangeListener(time)) {
                persistString(time);
                //notifyChanged();
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    public static String getHour(String time) {
        return time.split(":")[0];
    }

    public static String getHour(TimePicker picker) {
        String hour = String.valueOf(picker.getHour());
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        return hour;
    }

    public static String getMinute(String time) {
        return time.split(":")[1];
    }

    public static String getMinute(TimePicker picker) {
        String minute = String.valueOf(picker.getMinute());
        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        return minute;
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time = null;

        if (restoreValue) {
            if (defaultValue == null) {
                time = getPersistedString("00:00");
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else {
            time = defaultValue.toString();
        }

        hour = getHour(time);
        minute = getMinute(time);
    }

    @Override
    public CharSequence getSummary() {
        return hour + ":" + minute;
    }
}
