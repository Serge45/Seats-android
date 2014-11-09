package com.serge45.app.seats;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;

public class SeatPreviewPreference extends Preference implements OnSharedPreferenceChangeListener {
    TableLayout tableLayout;

    public SeatPreviewPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        setWidgetLayoutResource(R.layout.preference_seat_preview);
        View view = super.onCreateView(parent);
        tableLayout = (TableLayout) view.findViewById(R.id.preference_preview_table_layout);
        getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        updatePreview();
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 256));
        return view;
    }
    
    private void generateButtons(int rowCount, int colCount) {
        LayoutGenerationUtils.generateTableLayout(tableLayout, getContext(), rowCount, colCount, "");
    }
    
    private void updatePreview() {
        SharedPreferences pref = getSharedPreferences();
        int r = pref.getInt("setting_row", 6);
        int c = pref.getInt("setting_col", 7);
        generateButtons(r, c);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        if (key.equals("setting_row") || key.equals("setting_col")) {
            updatePreview();
        }
    }

}
