package com.serge45.app.seats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    private Button seatsButton;
    private Button namesListButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        initViews();
        initListeners();
    }
    
    protected void initViews() {
        seatsButton = (Button) findViewById(R.id.seats_button); 
        namesListButton = (Button) findViewById(R.id.name_list_button); 
    }
    
    protected void initListeners() {
        seatsButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SeatsActivity.class);
                startActivity(intent);
            }
        });
        
        namesListButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, NameListActivity.class);
                startActivity(intent);
            }
        });
    }

}
