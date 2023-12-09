package org.example.socket.server;

import org.example.acdep.AcDepDAO;
import org.example.acdep.Subject;
import org.example.acdep.Teacher;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
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
            while (processQuery());
        }
    }

    /**
     * Обробка запитів від клієнта
     * @return результат виконання обробки. true - успіх, false - неуспіх
     */
    private boolean processQuery() {
        try {
            int query = in.readInt();
            List<Teacher> teacherList;
            List<Subject> subjectList;
            switch (query) {
                case 1 -> {
                    int id = getIDT(dao.readTeachers(null));
                    dao.createTeacher(new Teacher(id, name));
                }
                case 2 -> {

                    Teacher del = getTeacher(dao.readTeachers(null), name);
                    if (del == null) {
                        System.out.println("Учителя із даним ПІБ не знайдено");
                        continue;
                    }
                    dao.deleteTeacher(del);
                }
                case 3 -> {
                    System.out.print("Назва предмету: ");
                    String name = scanner.nextLine();
                    System.out.print("ПІБ учителя (залиште поле пустим, якщо викладача на предмет немає): ");
                    String teacherName = scanner.nextLine();
                    int id = getIDS(dao.readSubjects(null));
                    dao.createSubject(new Subject(id, name, getTeacher(dao.readTeachers(null), teacherName)));
                }
                case 4 -> {
                    System.out.print("Назва предмету: ");
                    String name = scanner.nextLine();
                    Subject del = getSubject(dao.readSubjects(null), name);
                    if (del == null) {
                        System.out.println("Предмет із даною назвою не знайдено");
                        continue;
                    }
                    dao.deleteSubject(del);
                }
                case 5 -> {
                    System.out.print("ПІБ учителя: ");
                    String name = scanner.nextLine();
                    Teacher upd = getTeacher(dao.readTeachers(null), name);
                    if (upd == null) {
                        System.out.println("Учителя із даним ПІБ не знайдено");
                        continue;
                    }
                    System.out.print("Нове ПІБ (пусте поле - без змін): ");
                    String newName = scanner.nextLine();
                    if (!newName.isBlank())
                        upd.name = newName;
                    dao.updateTeachers(upd);
                }
                case 6 -> {
                    int leftMargin;
                    try {
                        System.out.print("Введіть ID викладача");
                        leftMargin = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    subjectList = dao.readSubjects("SELECT * FROM Предмети WHERE Викладач >= " + leftMargin + "AND Викладач <= " + leftMargin);
                    printListS(subjectList);
                }
                case 7 -> {
                    subjectList = dao.readSubjects(null);
                    printListS(subjectList);
                }
                case 8 -> {
                    teacherList = dao.readTeachers(null);
                    printListT(teacherList);
                }
                case 9 -> {
                    int leftMargin;
                    try {
                        System.out.print("Введіть ID викладача");
                        leftMargin = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    subjectList = dao.readSubjects("SELECT * FROM Предмети WHERE Викладач >= " + leftMargin + "AND Викладач <= " + leftMargin);
                    printListS(subjectList);
                }
                default -> {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
