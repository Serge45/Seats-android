package com.serge45.app.seats;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SeatSettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.preferences);
    }

}
