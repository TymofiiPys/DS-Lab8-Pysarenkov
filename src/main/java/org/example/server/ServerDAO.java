package org.example.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Клас для обробки запитів клієнта та відправлення йому результатів
 */
public class ServerDAO {
    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ServerDAO() {
        try {
            serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            throw new RuntimeException("Помилка створення сервера", e);
        }

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
    }
}
