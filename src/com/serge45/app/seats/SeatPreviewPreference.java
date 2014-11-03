package com.serge45.app.seats;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

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
        updatePreview();
        getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        return view;
    }
    
    private void generateButtons(int rowCount, int colCount) {
        tableLayout.removeAllViews();
        
        for (int j = 0; j < rowCount; ++j) {
            TableRow row = new TableRow(getContext());

            for (int i = 0; i < colCount; ++i) {
                Button btn = new Button(getContext());
                btn.setGravity(Gravity.CENTER);
                row.addView(btn, 
                            new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 
                                                      TableLayout.LayoutParams.MATCH_PARENT,
                                                      1.f)
                            );
            }

            tableLayout.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 
                                                                  TableLayout.LayoutParams.MATCH_PARENT,
                                                                  1.f)
                                );
        }
        
    }
    
    private void updatePreview() {
        SharedPreferences pref = getSharedPreferences();
        int r = pref.getInt("setting_row", 0);
        int c = pref.getInt("setting_col", 0);
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
