package org.example.acdep;

import java.io.Serializable;

public class Subject implements Serializable {
    public int code;
    public String name;
    public Teacher teacher;
    public Subject(int code, String name, Teacher teacher){
        this.code = code;
        this.name = name;
        this.teacher = teacher;
    }
}
