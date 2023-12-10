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

    /**
     * Повертає об'єкт Teacher, що відповідає імені name, зі списку list
     * @param list список вчителів
     * @param name ім'я учителя
     * @return Об'єкт Teacher, що відповідає імені name, null якщо не знайдено вчителя з іменем name
     */
    public static Teacher getTeacher(List<Teacher> list, String name) {
        for (Teacher t : list) {
            if (t.name.equals(name)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Повертає об'єкт Subject, що відповідає імені name, зі списку list
     * @param list список предметів
     * @param name назва предмету
     * @return Об'єкт Subject, що відповідає імені name, null якщо не знайдено предмет з назвою name
     */
    public static Subject getSubject(List<Subject> list, String name) {
        for (Subject t : list) {
            if (t.name.equals(name)) {
                return t;
            }
        }
        return null;
    }

    public static String listToStringT(List<Teacher> list) {
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

    public static String listToStringS(List<Subject> list) {
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

    public static void printListT(List<Teacher> list) {
        for (Teacher t : list) {
            System.out.println("ID: " + t.code);
            System.out.println("ПІБ: " + t.name);
        }
    }

    public static void printListS(List<Subject> list) {
        for (Subject t : list) {
            System.out.println("ID: " + t.code);
            System.out.println("Назва: " + t.name);
            System.out.println("ПІБ викладача: " + t.teacher.name);
        }
    }
}
