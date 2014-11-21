package com.serge45.app.seats;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

public class NameListActivity extends ActionBarActivity implements StudentDetailDialogDismissListener {
    enum ActivityMode {
        NORMAL,
        DELETE
    };

    final public static String DEFAULT_NAME_LIST_ASSET_NAME = "names.csv";
    final public static String TAG = "NameListActivity";

    private ListView nameListView;
    private Toolbar toolbar;
    private NameListAdaptor nameListAdaptor;
    private StudentDataDbHelper dbHelper;
    private int lastActiveItemIndex = -1;
    private ActivityMode activityMode = ActivityMode.NORMAL;
    private Drawable orgDeleteActionBackground;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.name_list_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
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
        toolbar = (Toolbar) findViewById(R.id.name_list_toolbar);
        toolbar.setBackgroundColor(Color.BLACK);
        setSupportActionBar(toolbar);
    }
    
    protected AlertDialog createDeleteAlertDialog(final StudentInfo info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NameListActivity.this);

        builder.setTitle(R.string.information_dialog_title)
               .setMessage(R.string.information_dialog_delete_message)
               .setPositiveButton(R.string.information_dialog_yes, new DialogInterface.OnClickListener() {
                
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*Deletion operation.*/
                nameListAdaptor.getInfoList().remove(lastActiveItemIndex);
                nameListAdaptor.notifyDataSetChanged();

                SQLiteDatabase db = dbHelper.getReadableDatabase();
                dbHelper.deleteStudentData(info, db);
            }
        });
            
        builder.setNeutralButton(R.string.information_dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
               
        });
           
        return builder.create();
    }
    
    protected void initListeners() {
        nameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                lastActiveItemIndex = position;
                final StudentInfo info = (StudentInfo) nameListAdaptor.getItem(position);
                
                if (activityMode == ActivityMode.NORMAL) {
                    createPopUpFragment(info, false, StudentDetailDialog.OpenState.UPDATE);
                } else {
                    createDeleteAlertDialog(info).show();
                }
            }
        });
        
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            
            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                boolean ret = false;
                
                switch (item.getItemId()) {
                    case R.id.action_bar_name_list_add: {
                        StudentInfo info = new StudentInfo();
                        info.num = nameListAdaptor.getCount() + 1;//Temp setting.
                        createPopUpFragment(info, true, StudentDetailDialog.OpenState.CREATE);
                        ret = true;
                        break;
                    }
                    
                    case R.id.action_bar_name_list_delete: {
                        View view = findViewById(R.id.action_bar_name_list_delete);
                        if (activityMode == ActivityMode.NORMAL) {
                            view.setBackgroundColor(Color.RED);
                            activityMode = ActivityMode.DELETE;
                        } else {
                            int sdk = android.os.Build.VERSION.SDK_INT;

                            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                view.setBackgroundDrawable(orgDeleteActionBackground);
                            } else {
                                view.setBackground(orgDeleteActionBackground);
                            }
                            activityMode = ActivityMode.NORMAL;
                        }
                        ret = true;
                        break;
                    }
                    default:
                        break;
                }
                return ret;
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
