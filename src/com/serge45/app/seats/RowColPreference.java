package com.serge45.app.seats;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;


public class RowColPreference extends DialogPreference {
    NumberPicker numberPicker;
    int value;

    public RowColPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }
    
    protected void initViews() {
        setDialogLayoutResource(R.layout.preference_row_col);
        setPositiveButtonText(R.string.action_detail_ok);
        setNegativeButtonText(R.string.action_detail_cancel);
    }
    
    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);

        value = getPersistedInt(7);
        setSummary(getContext().getResources().getString(R.string.setting_current_value) + value);
        return view;
    }

    protected void initListeners() {
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                value = newVal;
            }
        });
    }

    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();
        numberPicker = (NumberPicker) view.findViewById(R.id.preferemce_number_picker);
        initListeners();
        numberPicker.setMinValue(2);
        numberPicker.setMaxValue(10);
        numberPicker.setValue(value);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(value);
            setSummary(getContext().getResources().getString(R.string.setting_current_value) + value);
        }
    }
    

}
