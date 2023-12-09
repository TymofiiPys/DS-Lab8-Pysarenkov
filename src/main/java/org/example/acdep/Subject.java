package org.example.acdep;

public class Subject {
    public int code;
    public String name;
    public Teacher teacher;
    public Subject(int code, String name, Teacher teacher){
        this.code = code;
        this.name = name;
        this.teacher = teacher;
    }
}
