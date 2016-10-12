/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.presentation.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * A {@link android.preference.Preference} which allows for String input which will be parsed to
 * float. If during parsing NumberFormatException occurred a {@link WarningDialog} will be shown
 * if set
 */
public class FloatEditTextPreference extends EditTextPreference {

    public interface WarningDialog {

        void showNumberFormatException(Context context);

    }

    private static final String FLOAT_FORMAT = ".%1f";

    private WarningDialog warningDialog;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FloatEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public FloatEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FloatEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatEditTextPreference(Context context) {
        super(context);
    }

    @Override
    protected boolean persistString(String value) {
        float f = 0;
        try {
            f = Float.parseFloat(value);
        } catch (NumberFormatException e) {
            if (warningDialog != null)
                warningDialog.showNumberFormatException(getContext());
            return false;
        }
        return super.persistFloat(f);
    }

    @Override
    protected String getPersistedString(String defaultReturnValue) {
        return String.format(FLOAT_FORMAT, getPersistedFloat(-1));
    }

    public WarningDialog getWarningDialog() {
        return warningDialog;
    }

    public void setWarningDialog(WarningDialog warningDialog) {
        this.warningDialog = warningDialog;
    }
}
