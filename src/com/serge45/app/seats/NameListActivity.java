package com.serge45.app.seats;

import java.util.ArrayList;
import java.util.List;

import com.serge45.app.seats.NameListActivity.ActivityMode;
import com.serge45.app.seats.R.integer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.AlteredCharSequence;
import android.util.SparseBooleanArray;
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
    private int nameListViewChoiceMode = ListView.CHOICE_MODE_NONE;
    
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
        
        nameListView.setChoiceMode(nameListViewChoiceMode);
    }

    protected void initViews() {
        nameListAdaptor = new NameListAdaptor(this);
        nameListView = (ListView) findViewById(R.id.name_list_view); 
        nameListView.setAdapter(nameListAdaptor);
        nameListView.setSelector(R.drawable.list_view_selector);
        toolbar = (Toolbar) findViewById(R.id.name_list_toolbar);
        toolbar.setBackgroundColor(Color.BLACK);
        setSupportActionBar(toolbar);
    }
    
    protected AlertDialog createDeleteAlertDialog(final List<Integer> delList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NameListActivity.this);

        builder.setTitle(R.string.information_dialog_title)
               .setMessage(R.string.information_dialog_delete_message)
               .setPositiveButton(R.string.information_dialog_yes, new DialogInterface.OnClickListener() {
                
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*Deletion operation.*/
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                StudentInfo info = null;
                List<StudentInfo> infoList = nameListAdaptor.getInfoList();

                for (int i = delList.size() - 1; i >= 0; --i) {
                    info = infoList.get(delList.get(i));
                    infoList.remove(delList.get(i).intValue());
                    dbHelper.deleteStudentData(info, db);
                }
                
                nameListAdaptor.notifyDataSetChanged();
                nameListView.clearChoices();
                switchActivityMode(ActivityMode.NORMAL);
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
        nameListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long id) {
                ActivityMode targetMode = ActivityMode.NORMAL;
                nameListViewChoiceMode = ListView.CHOICE_MODE_NONE;

                if (activityMode == ActivityMode.NORMAL) {
                    targetMode = ActivityMode.DELETE;
                    nameListViewChoiceMode = ListView.CHOICE_MODE_MULTIPLE;
                } else {
                    nameListView.clearChoices();
                }

                switchActivityMode(targetMode);
                
                final int p = position;
                
                nameListView.post(new Runnable() {
                    public void run() {
                        nameListView.setChoiceMode(nameListViewChoiceMode);
                        
                        if (activityMode == ActivityMode.DELETE) {
                            nameListView.setItemChecked(p, true);
                        }
                    }
                });
                
                return true;
            }
            
        });
        nameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                lastActiveItemIndex = position;
                final StudentInfo info = (StudentInfo) nameListAdaptor.getItem(position);
                
                if (activityMode == ActivityMode.NORMAL) {
                    createPopUpFragment(info, false, StudentDetailDialog.OpenState.UPDATE);
                }
            }
        });
        
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            
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
                        SparseBooleanArray checked = nameListView.getCheckedItemPositions();
                        
                        if (checked == null) {
                            return ret;
                        }
                        
                        List<Integer> deletionList = new ArrayList<Integer>();
                        
                        for (int i = 0; i < nameListView.getCount(); ++i) {
                            if (checked.get(i)) {
                                deletionList.add(i);
                            }
                        }
                        AlertDialog dialog = createDeleteAlertDialog(deletionList);
                        dialog.show();
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
    
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void switchActivityMode(ActivityMode mode) {
        activityMode = mode;
        View deleteView = findViewById(R.id.action_bar_name_list_delete);
        View addView = findViewById(R.id.action_bar_name_list_add);
        
        switch(activityMode) {
        case NORMAL: {
            addView.setEnabled(true);
            deleteView.setEnabled(false);

            int sdk = android.os.Build.VERSION.SDK_INT;

            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                deleteView.setBackgroundDrawable(orgDeleteActionBackground);
            } else {
                deleteView.setBackground(orgDeleteActionBackground);
            }
            
            break;
        }
        
        case DELETE: {
            addView.setEnabled(false);
            deleteView.setEnabled(true);
            deleteView.setBackgroundColor(Color.RED);
            activityMode = ActivityMode.DELETE;
            break;
        }
        default:
            break;
        }
    }
}
