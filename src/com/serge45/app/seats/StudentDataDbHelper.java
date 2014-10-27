package com.serge45.app.seats;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.serge45.app.seats.StudentDataBase;
import com.serge45.app.seats.StudentDataBase.StudentData;

public class StudentDataDbHelper extends SQLiteOpenHelper {
    private String TAG = "StudentDbHelper";
    private static final String DATABASE_NAME = "student.db";
    private static final int DATABASE_VERSION = 1;

    public StudentDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(StudentDataBase.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(StudentDataBase.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    
    public long insertOrUpdateStudentData(StudentInfo info, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
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
        return newRowId;
    }
    
    public List<StudentInfo> getAllRow(SQLiteDatabase db) {
        ArrayList<StudentInfo> result = new ArrayList<StudentInfo>();
        
        Cursor cursor = db.rawQuery("select * from " + StudentData.TABLE_NAME, null);
        
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int num = cursor.getInt(cursor.getColumnIndex(StudentData._ID));
                String name = cursor.getString(cursor.getColumnIndex(StudentData.COLUMN_STUDENT_NAME));
                float grade = cursor.getFloat(cursor.getColumnIndex(StudentData.COLUMN_STUDENT_GRADE));
                int r = cursor.getInt(cursor.getColumnIndex(StudentData.COLUMN_STUDENT_SEAT_ROW));
                int c = cursor.getInt(cursor.getColumnIndex(StudentData.COLUMN_STUDENT_SEAT_COL));
                String note = cursor.getString(cursor.getColumnIndex(StudentData.COLUMN_STUDENT_NOTE));
                int status = cursor.getInt(cursor.getColumnIndex(StudentData.COLUMN_STUDENT_STATUS));

                StudentInfo info = new StudentInfo();
                info.num = num;
                info.name = name;
                info.grade = grade;
                info.row = r;
                info.col = c;
                info.note = note;
                info.status = status;

                result.add(info);
                cursor.moveToNext();
            }
        }
        
        return result;
    }

}
