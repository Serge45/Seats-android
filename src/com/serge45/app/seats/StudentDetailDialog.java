package com.serge45.app.seats;

import java.util.List;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.ToggleButton;

public class StudentDetailDialog extends DialogFragment {
    enum OpenState {
        CREATE,
        UPDATE
    };
    public static List<StudentInfo> infoList; 
    private RatingBar ratingBar;
    private EditText nameEdit;
    private EditText numEdit;
    private EditText noteEdit;
    private Button okButton;
    private Button cancelButton;
    private ToggleButton editToggleButton;
    private StudentDetailDialogDismissListener dismissListener = null;
    private StudentInfo localInfo;
    private boolean editable = false;
    private OpenState openState = OpenState.UPDATE;

    static StudentDetailDialog newInstance(StudentInfo info, boolean edit) {
        StudentDetailDialog f = new StudentDetailDialog();
        
        Bundle args = new Bundle();
        args.putString("name", info.name);
        args.putString("note", info.note);
        args.putInt("num", info.num);
        args.putInt("row", info.row);
        args.putInt("col", info.col);
        args.putFloat("grade", info.grade);
        args.putInt("status", info.status);
        args.putBoolean("editable", edit);
        f.setArguments(args);
        return f;
    }
    
    public void setEditable(boolean edit) {
        editable = edit;
        nameEdit.setEnabled(editable);
        numEdit.setEnabled(editable);
    }
    
    public void setOpenState(OpenState state) {
        openState = state;
    }
    
    private void initListeners() {
        okButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                localInfo.name = nameEdit.getText().toString();
                localInfo.note = noteEdit.getText().toString();
                localInfo.num = Integer.parseInt(numEdit.getText().toString());
                localInfo.grade = ratingBar.getRating();
                
                if (checkValidInfo() == false && openState == OpenState.CREATE) {
                    Toast.makeText(getActivity(), "Invalid information, repeating number", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (dismissListener != null) {
                    dismissListener.onDismiss(localInfo);
                }
                getDialog().dismiss();
            }
        });
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        
        editToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setEditable(isChecked);
            }
        });
    }
    
    public void setOnDismissListener(StudentDetailDialogDismissListener l) {
        dismissListener = l;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Dialog;
        setStyle(style, theme);
        localInfo = new StudentInfo();
        localInfo.name = getArguments().getString("name");
        localInfo.num = getArguments().getInt("num");
        localInfo.note = getArguments().getString("note");
        localInfo.row = getArguments().getInt("row");
        localInfo.col = getArguments().getInt("col");
        localInfo.grade = getArguments().getFloat("grade");
        localInfo.status = getArguments().getInt("status");
        
        editable = getArguments().getBoolean("editable");
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.student_detail_view, container);
        ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
        nameEdit = (EditText) view.findViewById(R.id.name_edit);
        numEdit = (EditText) view.findViewById(R.id.num_edit);
        noteEdit = (EditText) view.findViewById(R.id.note_edit);
        okButton = (Button) view.findViewById(R.id.ok_button);
        cancelButton = (Button) view.findViewById(R.id.cancel_button); 
        editToggleButton = (ToggleButton) view.findViewById(R.id.edit_toggle_button);
        nameEdit.setText(localInfo.name);
        numEdit.setText(String.valueOf(localInfo.num));
        noteEdit.setText(localInfo.note);
        ratingBar.setRating(localInfo.grade);
        initListeners();
        editToggleButton.setChecked(editable);
        setEditable(editable);
        getDialog().setTitle(localInfo.name);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return view;
    }
    
    private boolean checkValidInfo() {
        if (localInfo.num <= 0) {
            return false;
        }

        for (StudentInfo info : infoList) {
            if (info.num == localInfo.num) {
                return false;
            }
        }
        return true;
    }

}
