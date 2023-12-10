package org.example.acdep;

import java.util.Comparator;
import java.util.List;

/**
 * Допоміжні функції для CRUD-операцій над базою даних
 */
public class AcDepUtil {
    private static char splitter = '%';
    private static char rowSplitter = '#';
    private static char fieldSplitter = ':';
    public static int getIDT(List<Teacher> list) {
        int i = 1;
        list.sort(new Comparator<Teacher>() {
            @Override
            public int compare(Teacher t1, Teacher t2) {
                return Integer.compare(t1.code, t2.code);
            }
        });
        for (Teacher t : list) {
            if (t.code != i) {
                return i;
            }
            i++;
        }
        return i;
    }

    public static int getIDS(List<Subject> list) {
        int i = 1;
        list.sort(new Comparator<Subject>() {
            @Override
            public int compare(Subject t1, Subject t2) {
                return Integer.compare(t1.code, t2.code);
            }
        });
        for (Subject t : list) {
            if (t.code != i) {
                return i;
            }
            i++;
        }
        return i;
    }

    public static Teacher getTeacher(List<Teacher> list, String name) {
        for (Teacher t : list) {
            if (t.name.equals(name)) {
                return t;
            }
        }
        return null;
    }

    public static Subject getSubject(List<Subject> list, String name) {
        for (Subject t : list) {
            if (t.name.equals(name)) {
                return t;
            }
        }
        return null;
    }

    public static String printListT(List<Teacher> list) {
        String res = "";
        int i = 0;
        int size = list.size();
        for (Teacher t : list) {
            res += t.code;
            res += fieldSplitter;
            res += t.name;
            if(i != size - 1)
                res += rowSplitter;
            i++;
        }
        return res;
    }

    public static String printListS(List<Subject> list) {
        String res = "";
        int i = 0;
        int size = list.size();
        for (Subject s : list) {
            res += s.code;
            res += fieldSplitter;
            res += s.name;
            res += fieldSplitter;
            res += s.teacher.name;
            if(i != size - 1)
                res += rowSplitter;
            i++;
        }
        return res;
    }
}
