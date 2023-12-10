package org.example.acdep;

import java.io.Serializable;

public class Teacher implements Serializable {
    public int code;
    public String name;

    public Teacher(int code, String name){
        this.code = code;
        this.name = name;
    }
}
