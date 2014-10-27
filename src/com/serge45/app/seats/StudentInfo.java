package com.serge45.app.seats;

public class StudentInfo implements Cloneable{
    public StudentInfo() {
        num = 0;
        name = "";
        row = -1;
        col = -1;
        grade = 0.f;
        note = "";
        status = -1;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        StudentInfo info = (StudentInfo) super.clone();
        info.name = new String(name);
        info.note = new String(note);
        info.num = num;
        info.grade = grade;
        info.col = col;
        info.row = row;
        info.status = status;
        return info;
    }

    public int num;
    public String name;
    public int row;
    public int col;
    public float grade;
    public String note;
    public int status;
}
