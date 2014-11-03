package com.serge45.app.seats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.serge45.app.seats.StudentDataBase.StudentData;

import android.app.Activity;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
        loadNameListFromSQLite();
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

        List<StudentInfo> numNameList = new ArrayList<StudentInfo>();
        
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            String[] elements = line.split(",");
            StudentInfo info = new StudentInfo();
            info.name = elements[0];
            info.num = Integer.parseInt(elements[1]);
            numNameList.add(info);
        }
        nameListAdaptor.setInfoList(numNameList);
    }
    
    private void exportNameListToDb() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        for (StudentInfo p : nameListAdaptor.getInfoList()) {
            dbHelper.insertOrUpdateStudentData(p, db);
        }
    }
    
    boolean loadNameListFromSQLite() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<StudentInfo> all = dbHelper.getAllRow(db);
        db.close();
        List<StudentInfo> infoList = new ArrayList<StudentInfo>();
        
        if (all.size() > 0) {
            for (StudentInfo info : all) {
                if (info.num > 0) {
                    infoList.add(info);
                }
            }
        
            nameListAdaptor.setInfoList(infoList);
            return true;
        } else {
            all = null;
            return false;
        }
        
    }
}
