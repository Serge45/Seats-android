package com.serge45.app.seats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.serge45.app.seats.StudentDataBase.StudentData;

import android.app.Activity;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class NameListActivity extends Activity {
    final public static String DEFAULT_NAME_LIST_ASSET_NAME = "names.csv";
    final public static String TAG = "NameListActivity";

    private Button importButton;
    private Button exportButton;
    private ListView nameListView;
    private NameListAdaptor nameListAdaptor;
    
    private StudentDataDbHelper dbHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_list_layout);
        initViews();
        initListeners();
        dbHelper = new StudentDataDbHelper(this);
    }

    protected void initViews() {
        nameListAdaptor = new NameListAdaptor(this);
        importButton = (Button) findViewById(R.id.name_list_import_button);
        exportButton = (Button) findViewById(R.id.name_list_export_button); 
        nameListView = (ListView) findViewById(R.id.name_list_view); 
        nameListView.setAdapter(nameListAdaptor);
    }
    
    protected void initListeners() {
        importButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                try {
                    loadDefaultNameListFromAsset();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nameListAdaptor.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        exportButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                exportNameListToDb();
            }
        });
    }
    
    private void loadDefaultNameListFromAsset() throws IOException {
        AssetManager assetManager = getAssets();

        InputStream assetInputStream = assetManager.open(DEFAULT_NAME_LIST_ASSET_NAME);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetInputStream));

        List<Pair<Integer, String> > numNameList = new ArrayList<Pair<Integer,String>>();
        
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            String[] elements = line.split(",");
            numNameList.add(Pair.create(Integer.parseInt(elements[0]), elements[1]));
        }
        nameListAdaptor.setNumToNameList(numNameList);
    }
    
    private void exportNameListToDb() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = null;
        
        for (Pair<Integer, String> p : nameListAdaptor.getNumToNameList()) {
            StudentInfo info = new StudentInfo();
            info.num = p.first;
            info.name = p.second;
            info.row = 0;
            info.col = 0;
            info.note = "";
            info.grade = 5.f;
            info.status = 0;

            values = new ContentValues();
            values.put(StudentData._ID, info.num);
            values.put(StudentData.COLUMN_STUDENT_NAME, info.name);
            values.put(StudentData.COLUMN_STUDENT_SEAT_ROW, info.row);
            values.put(StudentData.COLUMN_STUDENT_SEAT_COL, info.col);
            values.put(StudentData.COLUMN_STUDENT_GRADE, info.grade);
            values.put(StudentData.COLUMN_STUDENT_NOTE, info.note);
            values.put(StudentData.COLUMN_STUDENT_STATUS, info.status);
            long newRowId = -1;
            newRowId = db.insertWithOnConflict(StudentData.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            Log.v(TAG, String.valueOf(newRowId));
        }
    }
}