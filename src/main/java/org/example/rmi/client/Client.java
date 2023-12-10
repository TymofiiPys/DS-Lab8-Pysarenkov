package org.example.rmi.client;

import org.example.acdep.AcDepUtil;
import org.example.acdep.Subject;
import org.example.acdep.Teacher;
import org.example.rmi.AcDepRMI;

import javax.management.remote.rmi.RMIServer;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

public class Client {
    private AcDepRMI s;

    public Client() throws
            MalformedURLException, NotBoundException, RemoteException {
        s = (AcDepRMI) Naming.lookup("//localhost:8080/AcDep");
        System.out.println("Ye");
        menu();
    }

    public void menu() throws RemoteException {
        Scanner scanner = new Scanner(System.in);
        int op;
        while (true) {
            System.out.println("Оберіть команду:");
            System.out.println("1. Прийом на роботу нового викладача");
            System.out.println("2. Звільнення(видалення викладача)");
            System.out.println("3. Додавання нової дисципліни");
            System.out.println("4. Видалення дисципліни");
            System.out.println("5. Редагування особистих даних викладача");
            System.out.println("6. Запит кількості дисциплін у викладача");
            System.out.println("7. Пошук дисципліни за назвою");
            System.out.println("8. Отримання повного списку викладачів");
            System.out.println("9. Отримання списку дисциплін для заданого викладача");
            System.out.println("0. Вихід із програми");
            try {
                op = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                continue;
            }
            String teacherName;
            String subjectName;
            int result;
            switch (op) {
                case 1 -> {
                    System.out.print("ПІБ учителя: ");
                    teacherName = scanner.nextLine();
                    result = s.insertTeacher(teacherName);
                    System.out.println("Успішно");
                }
                case 2 -> {
                    System.out.print("ПІБ учителя: ");
                    teacherName = scanner.nextLine();
                    result = s.deleteTeacher(teacherName);
                    switch (result) {
                        case 0:
                            System.out.println("Успішно");
                        case 1:
                            System.out.println("Учителя із даним ПІБ не знайдено");
                        default:
                            System.out.println("Невідома помилка");
                    }
                }
                case 3 -> {
                    System.out.print("Назва предмету: ");
                    subjectName = scanner.nextLine();
                    System.out.print("ПІБ учителя (залиште поле пустим, якщо викладача на предмет немає):: ");
                    teacherName = scanner.nextLine();
                    result = s.insertSubject(subjectName, teacherName);
                    System.out.println("Успішно");
                }
                case 4 -> {
                    System.out.print("Назва предмету: ");
                    subjectName = scanner.nextLine();
                    result = s.deleteSubject(subjectName);
                    switch (result) {
                        case 0:
                            System.out.println("Успішно");
                        case 1:
                            System.out.println("Предмет із даною назвою не знайдено");
                        default:
                            System.out.println("Невідома помилка");
                    }
                }
                case 5 -> {
                    System.out.print("ПІБ учителя: ");
                    teacherName = scanner.nextLine();
                    System.out.print("Нове ПІБ (пусте поле - без змін): ");
                    String newName = scanner.nextLine();
                    result = s.updateTeacher(teacherName, newName);
                    switch (result) {
                        case 0:
                            System.out.println("Успішно");
                        case 1:
                            System.out.println("Учителя із даним ПІБ не знайдено");
                        default:
                            System.out.println("Невідома помилка");
                    }
                }
                case 6 -> {
                    System.out.print("ПІБ учителя: ");
                    teacherName = scanner.nextLine();
                    List<Subject> subjects = s.subjOfTeacher(teacherName);
                    if(subjects == null){
                        System.out.println("Учителя із даним ПІБ не знайдено");
                    } else if (subjects.isEmpty()) {
                        System.out.println("Учитель не викладає жодного предмету");
                    } else {
                        System.out.println("Кількість дисциплін у викладача: " + subjects.size());
                    }
                }
                case 7 -> {
                    System.out.print("Назва предмету: ");
                    subjectName = scanner.nextLine();
                    List<Subject> subjects = s.findSubj(subjectName);
                    AcDepUtil.printListS(subjects);
                }
                case 8 -> {
                    List<Teacher> teachers = s.getAllTeachers();
                    AcDepUtil.printListT(teachers);
                }
                case 9 -> {
                    System.out.print("ПІБ учителя: ");
                    teacherName = scanner.nextLine();
                    List<Subject> subjects = s.subjOfTeacher(teacherName);
                    if(subjects == null){
                        System.out.println("Учителя із даним ПІБ не знайдено");
                    } else if (subjects.isEmpty()) {
                        System.out.println("Учитель не викладає жодного предмету");
                    } else {
                        AcDepUtil.printListS(subjects);
                    }
                }
                case 0 -> {
                    return;
                }
                default -> {
                    continue;
                }
            }
        }

    }

    public static void main(String[] args) throws
            MalformedURLException, NotBoundException, RemoteException {
//        RMIServer s = (RMIServer) Naming.lookup("AcDep");
        new Client();
    }
}
