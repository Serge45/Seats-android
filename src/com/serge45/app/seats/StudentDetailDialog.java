package com.serge45.app.seats;

import com.serge45.app.seats.SeatsActivity.ButtonWithInformation;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

public class StudentDetailDialog extends DialogFragment {
    public static SparseArray<String> numToName; 
    private RatingBar ratingBar;
    private EditText nameEdit;
    private EditText numEdit;
    private EditText noteEdit;
    private Button okButton;
    private Button cancelButton;
    private StudentDetailDialogDismissListener dismissListener = null;
    private StudentInfo localInfo;

    static StudentDetailDialog newInstance(StudentInfo info) {
        StudentDetailDialog f = new StudentDetailDialog();
        
        Bundle args = new Bundle();
        args.putString("name", info.name);
        args.putString("note", info.note);
        args.putInt("num", info.num);
        args.putInt("row", info.row);
        args.putInt("col", info.col);
        args.putFloat("grade", info.grade);
        args.putInt("status", info.status);
        f.setArguments(args);
        return f;
    }
    
    public void setOnDismissListener(StudentDetailDialogDismissListener l) {
        dismissListener = l;
    }
    
    private void setListeners() {
        okButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                localInfo.grade = ratingBar.getRating();
                dismissListener.onDismiss(localInfo);
                getDialog().dismiss();
            }
        });
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        
        numEdit.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int n = 0;
                
                try {
                    n = Integer.valueOf(s.toString());
                    localInfo.num = n;
                } catch (NumberFormatException e) {
                    localInfo.name = "";
                    nameEdit.setText(localInfo.name);
                    return;
                }
                String tmpName = null;

                if ((tmpName = numToName.get(n)) != null) {
                    localInfo.name = tmpName;
                } else {
                    localInfo.name = "";
                }
                nameEdit.setText(localInfo.name);
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        
        nameEdit.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                localInfo.name = s.toString();
            }
        });
        
        noteEdit.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                localInfo.note = s.toString();
            }
        });
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
        nameEdit.setText(localInfo.name);
        numEdit.setText(String.valueOf(localInfo.num));
        noteEdit.setText(localInfo.note);
        ratingBar.setRating(localInfo.grade);
        setListeners();
        getDialog().setTitle(localInfo.name);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return view;
    }

}
