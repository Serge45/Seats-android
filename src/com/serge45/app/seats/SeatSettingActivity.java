package com.serge45.app.seats;

import android.app.Activity;
import android.os.Bundle;

public class SeatSettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, new SeatSettingFragment())
            .commit();
    }

}
