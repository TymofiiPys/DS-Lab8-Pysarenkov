package org.example.socket.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Клас для надсилання команд на сервер та виведення
 * різноманітної інформації від сервера
 */
public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner scanner;
    private char splitter = '%';

    public Client() {
        try {
            System.out.println("З'єднуємось...");
            socket = new Socket("localhost", 8080);
            System.out.println("Успішно!");
        } catch (IOException e) {
            throw new RuntimeException("Не вийшло з'єднатись із сервером", e);
        }
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException("Помилка створення IO потоків", e);
        }
        scanner = new Scanner(System.in);
        System.out.println("\n===================================\n");
        menu();
    }

    private void outputSubjects(String listStr) {
        String[] list = listStr.split("#");
        for (String subj : list) {
            String[] subjInfo = subj.split(":");
            System.out.println("ID: " + subjInfo[0]);
            System.out.println("Назва: " + subjInfo[1]);
            System.out.println("ПІБ викладача: " + subjInfo[2]);
        }
    }

    private void outputTeachers(String listStr) {
        String[] list = listStr.split("#");
        for (String teacher : list) {
            String[] teacherInfo = teacher.split(":");
            System.out.println("ID: " + teacherInfo[0]);
            System.out.println("ПІБ: " + teacherInfo[1]);
        }
    }

    /**
     * Надсилання запиту на сервер та виведення відповіді
     *
     * @param command - тип запиту
     * @return статус виконання запиту. 0 - успіх, інше - невдача
     */
    public int sendCommand(int command) {
        try {
            String query = "" + command + splitter;
            String name, subj;
            switch (command) {
                case 1:
                case 2:
                case 6:
                case 9:
                    System.out.print("ПІБ учителя: ");
                    name = scanner.nextLine();
                    query += name;
                    break;
                case 5:
                    System.out.print("ПІБ учителя: ");
                    name = scanner.nextLine();
                    query += name + splitter;
                    System.out.print("Нове ПІБ (пусте поле - без змін): ");
                    String newName = scanner.nextLine();
                    query += newName.equals("") ? name : newName;
                case 4:
                case 7:
                    System.out.print("Назва предмету: ");
                    subj = scanner.nextLine();
                    query += subj;
                    break;
                case 3:
                    System.out.print("Назва предмету: ");
                    subj = scanner.nextLine();
                    query += subj + splitter;
                    System.out.print("ПІБ учителя (залиште поле пустим, якщо викладача на предмет немає):: ");
                    name = scanner.nextLine();
                    query += name;
                    break;
            }
            out.println(query);
            String[] response = in.readLine().split("%");
            if(response[0].equals("1")){
                System.out.println(response[1]);
                return 1;
            }
            switch (command) {
                case 1:
                case 3:
                    System.out.println("Успішно");
                    break;
                case 2:
                case 4:
                case 5:
                case 6:
                    System.out.println(response[1]);
                    break;
                case 7:
                case 9:
                    outputSubjects(response[1]);
                    break;
                case 8:
                    outputTeachers(response[1]);
                    break;
            }
        } catch (IOException e) {
            return 1;
        }
        return 0;
    }

    /**
     * Меню програми
     */
    public void menu() {
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
            if (op == 0) {
                return;
            } else if (op < 0 || op > 9) {
                continue;
            }
            if (sendCommand(op) != 0) {
                System.out.println("Помилка виконання запиту");
            }
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
