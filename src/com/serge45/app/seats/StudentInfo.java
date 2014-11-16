package com.serge45.app.seats;

public class StudentInfo implements Cloneable, Comparable<StudentInfo> {
    public int num;
    public String name;
    public int row;
    public int col;
    public float grade;
    public String note;
    public int status;

    public StudentInfo() {
        num = 0;
        name = "";
        row = -1;
        col = -1;
        grade = 0.f;
        note = "";
        status = -1;
    }
    
    public void copyExceptPos(StudentInfo info) {
        num = info.num;
        name = info.name;
        grade = info.grade;
        note = info.note;
        status = info.status;
    }
    
    public void swapPosition(StudentInfo info) {
        int r = info.row;
        int c = info.col;
        info.col = col;
        info.row = row;
        col = c;
        row = r;
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

    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(this.getClass())) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(StudentInfo another) {
        if (num < another.num) {
            return -1;
        } else if (num > another.num) {
            return 1;
        }
        return 0;
    }
}
