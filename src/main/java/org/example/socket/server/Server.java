package org.example.socket.server;

import org.example.acdep.AcDepDAO;
import org.example.acdep.Subject;
import org.example.acdep.Teacher;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

/**
 * Клас для обробки запитів клієнта та відправлення йому результатів
 */
public class Server {
    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private AcDepDAO dao;
    private char splitter = '%';
    private char rowSplitter = '#';
    private char fieldSplitter = ':';

    public Server() {
        try {
            startServer();
        } catch (IOException e) {
            throw new RuntimeException("Помилка створення сервера", e);
        }
        try {
            dao = new AcDepDAO("acdep.db");
        } catch (SQLException e) {
            throw new RuntimeException("Помилка підключення до бази даних", e);
        }
        waitAndAcceptClient();
    }

    /**
     * Запуск сервера за портом 8080
     *
     * @throws IOException
     */
    private void startServer() throws IOException {
        serverSocket = new ServerSocket(8080);
    }

    /**
     * Очікування клієнта
     */
    private void waitAndAcceptClient() {
        while (true) {
            try {
                System.out.println("Очікуємо клієнта");
                socket = serverSocket.accept();
                System.out.println("Є клієнт!");
            } catch (IOException e) {
                throw new RuntimeException("Помилка з'єднання із клієнтом", e);
            }
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException("Помилка створення IO потоків", e);
            }
            while (processQuery()) ;
        }
    }

    private int getIDT(List<Teacher> list) {
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

    private int getIDS(List<Subject> list) {
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

    private Teacher getTeacher(List<Teacher> list, String name) {
        for (Teacher t : list) {
            if (t.name.equals(name)) {
                return t;
            }
        }
        return null;
    }

    private Subject getSubject(List<Subject> list, String name) {
        for (Subject t : list) {
            if (t.name.equals(name)) {
                return t;
            }
        }
        return null;
    }

    private String printListT(List<Teacher> list) {
        String res = "";
        int i = 0;
        int size = list.size();
        for (Teacher t : list) {
            res += t.code + fieldSplitter + t.name;
            if(i != size - 1)
                res += rowSplitter;
            i++;
        }
        return res;
    }

    private String printListS(List<Subject> list) {
        String res = "";
        int i = 0;
        int size = list.size();
        for (Subject s : list) {
            res += s.code + fieldSplitter + s.name + fieldSplitter + s.teacher.name;
            if(i != size - 1)
                res += rowSplitter;
            i++;
        }
        return res;
    }

    /**
     * Обробка запитів від клієнта
     *
     * @return результат виконання обробки. true - успіх, false - неуспіх
     */
    private boolean processQuery() {
        try {
            String[] query = in.readLine().split("%");
            String response = "";
            int command = Integer.parseInt(query[0]);
            List<Teacher> teacherList;
            List<Subject> subjectList;
            switch (command) {
                case 1 -> {
                    int id = getIDT(dao.readTeachers(null));
                    dao.createTeacher(new Teacher(id, query[1]));
                    response = "" + 0;
                }
                case 2 -> {
                    Teacher del = getTeacher(dao.readTeachers(null), query[1]);
                    if (del == null) {
                        response = "" + 1 + splitter + "Учителя із даним ПІБ не знайдено";
                    } else {
                        dao.deleteTeacher(del);
                        response = "" + 0 + splitter + "Успішно";
                    }
                }
                case 3 -> {
                    int id = getIDS(dao.readSubjects(null));
                    dao.createSubject(new Subject(id, query[1], getTeacher(dao.readTeachers(null), query[2])));
                    response = "" + 0;
                }
                case 4 -> {
                    Subject del = getSubject(dao.readSubjects(null), query[1]);
                    if (del == null) {
                        response = "" + 1 + splitter + "Предмет із даною назвою не знайдено";
                    } else {
                        dao.deleteSubject(del);
                        response = "" + 0 + splitter + "Успішно";
                    }
                }
                case 5 -> {
                    Teacher upd = getTeacher(dao.readTeachers(null), query[1]);
                    if (upd == null) {
                        response = "" + 1 + splitter + "Учителя із даним ПІБ не знайдено";
                    } else {
                        upd.name = query[2];
                        dao.updateTeachers(upd);
                        response = "" + 0 + splitter + "Успішно";
                    }
                }
                case 6 -> {
                    Teacher teacher = getTeacher(dao.readTeachers(null), query[1]);
                    if (teacher == null) {
                        response = "" + 1 + splitter + "Учителя із даним ПІБ не знайдено";
                    } else {
                        subjectList = dao.readSubjects("SELECT * FROM Предмети WHERE Викладач = " + teacher.code);
                        response = "" + 0 + splitter + "Кількість дисциплін у викладача: " + subjectList.size();
                    }
                }
                case 7 -> {
                    subjectList = dao.readSubjects("SELECT * FROM Предмети WHERE Назва = " + query[1]);
                    if(subjectList.isEmpty()) {
                        response = "" + 1 + splitter + "Не знайдено такого предмету";
                    } else {
                        response = "" + 0 + splitter + printListS(subjectList);
                    }
                }
                case 8 -> {
                    teacherList = dao.readTeachers(null);
                    response = "" + 0 + splitter + printListT(teacherList);
                }
                case 9 -> {
                    Teacher teacher = getTeacher(dao.readTeachers(null), query[1]);
                    if (teacher == null) {
                        response = "" + 1 + splitter + "Учителя із даним ПІБ не знайдено";
                    } else {
                        subjectList = dao.readSubjects("SELECT * FROM Предмети WHERE Викладач = " + teacher.code);
                        response = "" + 0 + splitter + printListS(subjectList);
                    }
                }
                default -> {
                    out.write("1" + splitter + "Невідома команда");
                    return false;
                }
            }
            out.write(response);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
