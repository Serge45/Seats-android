package com.serge45.app.seats;

import java.util.List;

import com.serge45.app.seats.R.integer;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class StudentSelectionDialog extends DialogFragment {
    static List<StudentInfo> infoList;
    private Button okButton;
    private Button cancelButton;
    private Spinner spinner;
    private StudentInfo localInfo;
    private StudentDetailDialogDismissListener dismissListener;

    static StudentSelectionDialog newInstance(StudentInfo info) {
        StudentSelectionDialog f = new StudentSelectionDialog();
        
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
        View view = inflater.inflate(R.layout.student_select_layout, container);
        okButton = (Button) view.findViewById(R.id.student_select_ok_button);
        cancelButton = (Button) view.findViewById(R.id.student_select_cancel_button); 
        spinner = (Spinner) view.findViewById(R.id.student_list_spinner);
        getDialog().setTitle(localInfo.name);
        initSpinner();
        initListeners();
        
        return view;
    }
    
    private void initListeners() {
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localInfo.copyExceptPos(infoList.get(spinner.getSelectedItemPosition()));
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
        
    }

    private void initSpinner() {
        ArrayAdapter<CharSequence> adaptor = new ArrayAdapter<CharSequence>(getActivity(), 
                                                                            android.R.layout.simple_spinner_item);

        if (infoList != null) {
            for (StudentInfo info : infoList) {
                String item = info.num + ", " + info.name;
                adaptor.add(item);
            }
        }
        spinner.setAdapter(adaptor);
        
        int pos = 0;
        
        for (int i = 0; i < infoList.size(); ++i) {
            if (localInfo.num == infoList.get(i).num) {
                pos = i;
                break;
            }
        }

        spinner.setSelection(pos);
    }
    
}
