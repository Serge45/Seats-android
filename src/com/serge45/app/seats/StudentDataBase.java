package com.serge45.app.seats;

import android.provider.BaseColumns;

public class StudentDataBase {
    public StudentDataBase() {}
    
    public static abstract class StudentData implements BaseColumns {
        public static final String TABLE_NAME = "students";
        public static final String COLUMN_STUDENT_NAME = "name";
        public static final String COLUMN_STUDENT_SEAT_ROW = "row";
        public static final String COLUMN_STUDENT_SEAT_COL = "col";
        public static final String COLUMN_STUDENT_GRADE = "grade";
        public static final String COLUMN_STUDENT_NOTE = "note";
        public static final String COLUMN_STUDENT_STATUS = "status";
    }
    
    private static final String TEXT_TYPE = " TEXT";
    private static final String DECIMAL_TYPE = " REAL";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_ENTRIES = 
            "CREATE TABLE " + StudentData.TABLE_NAME + " (" +
            StudentData._ID + INT_TYPE + " PRIMARY KEY" + COMMA_SEP +
            StudentData.COLUMN_STUDENT_NAME + TEXT_TYPE + COMMA_SEP +
            StudentData.COLUMN_STUDENT_SEAT_ROW + INT_TYPE + COMMA_SEP +
            StudentData.COLUMN_STUDENT_SEAT_COL + INT_TYPE + COMMA_SEP +
            StudentData.COLUMN_STUDENT_GRADE + DECIMAL_TYPE + COMMA_SEP +
            StudentData.COLUMN_STUDENT_NOTE + TEXT_TYPE + COMMA_SEP +
            StudentData.COLUMN_STUDENT_STATUS + INT_TYPE + 
            " )";
    
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + StudentData.TABLE_NAME;
}
