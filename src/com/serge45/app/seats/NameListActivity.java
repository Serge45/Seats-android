package com.serge45.app.seats;

import java.util.ArrayList;
import java.util.List;

import android.R.anim;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class NameListActivity extends Activity implements StudentDetailDialogDismissListener {
    final public static String DEFAULT_NAME_LIST_ASSET_NAME = "names.csv";
    final public static String TAG = "NameListActivity";

    private ListView nameListView;
    private NameListAdaptor nameListAdaptor;
    private StudentDataDbHelper dbHelper;
    private int lastActiveItemIndex = -1;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.name_list_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_name_list_add: {
                StudentInfo info = new StudentInfo();
                info.num = nameListAdaptor.getCount() + 1;//Temp setting.
                createPopUpFragment(info, true, StudentDetailDialog.OpenState.CREATE);
                break;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

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
        nameListView = (ListView) findViewById(R.id.name_list_view); 
        nameListView.setAdapter(nameListAdaptor);
    }
    
    protected void initListeners() {
        nameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                lastActiveItemIndex = position;
                StudentInfo info = (StudentInfo) nameListAdaptor.getItem(position);
                createPopUpFragment(info, false, StudentDetailDialog.OpenState.UPDATE);
            }
        });
    }
    
    private void createPopUpFragment(StudentInfo info, boolean edit, StudentDetailDialog.OpenState state) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("detail");

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        StudentDetailDialog newFragment = StudentDetailDialog.newInstance(info, edit);
        newFragment.setOnDismissListener(NameListActivity.this);
        newFragment.setOpenState(state);
        StudentDetailDialog.infoList = nameListAdaptor.getInfoList();
        newFragment.show(ft, "detail");
    }
    
    private boolean loadNameListFromSQLite() {
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

    @Override
    public void onDismiss(StudentInfo info) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        dbHelper.insertOrUpdateStudentData(info, db);
        db.close();
        
        if (lastActiveItemIndex > -1) {
            nameListAdaptor.getInfoList()
                           .get(lastActiveItemIndex)
                           .copyExceptPos(info);
        } else {
            nameListAdaptor.getInfoList()
                           .add(info);
        }
        nameListAdaptor.notifyDataSetChanged();
        lastActiveItemIndex = -1;
    }
}
