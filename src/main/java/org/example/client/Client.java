package org.example.client;

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
        System.out.println("\n===================================\n");
    }

    /**
     * Надсилання запиту на сервер
     *
     * @param command - тип запиту
     * @return статус виконання запиту. 0 - успіх, інше - невдача
     */
    public int sendCommand(int command) {
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
            sendCommand(op);
        }
    }
}
