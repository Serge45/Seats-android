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
    private String name;
    private int num;
    private SeatsActivity.ButtonWithInformation target;
    
    static StudentDetailDialog newInstance(String name, int num) {
        StudentDetailDialog f = new StudentDetailDialog();
        
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putInt("num", num);
        f.setArguments(args);
        return f;
    }
    
    private void setListeners() {
        okButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                target.name = name;
                target.button.setText(name);
                target.note = noteEdit.getText().toString();
                target.rating = ratingBar.getRating();
                target.number = Integer.parseInt(numEdit.getText().toString());
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
                    num = n;
                } catch (NumberFormatException e) {
                    name = "";
                    nameEdit.setText(name);
                    return;
                }
                String tmpName = null;

                if ((tmpName = numToName.get(n)) != null) {
                    name = tmpName;
                } else {
                    name = "";
                }
                nameEdit.setText(name);
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
                name = s.toString();
            }
        });
    }
    
    public void setTarget(SeatsActivity.ButtonWithInformation b) {
        target = b;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Dialog;
        setStyle(style, theme);
        
        name = getArguments().getString("name");
        num = getArguments().getInt("num");
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
        nameEdit.setText(name);
        numEdit.setText(String.valueOf(num));
        noteEdit.setText(target.note);
        ratingBar.setRating(target.rating);
        setListeners();
        getDialog().setTitle(name);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return view;
    }

}
