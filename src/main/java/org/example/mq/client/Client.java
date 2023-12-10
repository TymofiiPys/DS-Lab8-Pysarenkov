package org.example.mq.client;

/*
 * Варіант 17
 * Предметна область   Кафедра університету
 * Об'єкти             Викладачі, Дисципліни
 * Примітка            На кафедрі існує множина викладачів. Для кожного викладача
 *                     задано множину дисциплін.
 * Необхідні операції  1. Прийом на роботу нового викладача
 *                     2. Додавання нової дисципліни
 *                     3. Видалення дисципліни
 *                     4. Отримання повного списку викладачів
 *                     5. Отримання списку дисциплін для заданого викладача
 * Формат повідомлень  Рядок з роздільником
 */

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.*;

public class Client implements AutoCloseable {

    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";
    private final char splitter = '%';
    private final String rowSplitter = "#";
    private final String fieldSplitter = ":";

    public Client() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    private void outputSubjects(String listStr) {
        String[] list = listStr.split(rowSplitter);
        for (String subj : list) {
            String[] subjInfo = subj.split(fieldSplitter);
            System.out.println("ID: " + subjInfo[0]);
            System.out.println("Назва: " + subjInfo[1]);
            System.out.println("ПІБ викладача: " + subjInfo[2]);
        }
    }

    private void outputTeachers(String listStr) {
        String[] list = listStr.split(rowSplitter);
        for (String teacher : list) {
            String[] teacherInfo = teacher.split(fieldSplitter);
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
    private int sendCommand(int command) throws ExecutionException, InterruptedException {
        try {
            Scanner scanner = new Scanner(System.in);
            String query = "" + command + splitter;
            String name, subj;
            switch (command) {
                case 1:
                case 5:
                    System.out.print("ПІБ учителя: ");
                    name = scanner.nextLine();
                    query += name;
                    break;
                case 3:
                case 2:
                    System.out.print("Назва предмету: ");
                    subj = scanner.nextLine();
                    query += subj + splitter;
                    System.out.print("ПІБ учителя (залиште поле пустим, якщо викладача на предмет немає):: ");
                    name = scanner.nextLine();
                    query += name;
                    break;
            }
            String[] response = call(query).split("%");
            if (response[0].equals("1")) {
                System.out.println(response[1]);
                return 1;
            }
            switch (command) {
                case 1:
                case 2:
                    System.out.println("Успішно");
                    break;
                case 3:
                    System.out.println(response[1]);
                    break;
                case 4:
                    outputTeachers(response[1]);
                    break;
                case 5:
                    outputSubjects(response[1]);
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
    public void menu() throws ExecutionException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        int op;
        while (true) {
            System.out.println("Оберіть команду:");
            System.out.println("1. Прийом на роботу нового викладача");
            System.out.println("2. Додавання нової дисципліни");
            System.out.println("3. Видалення дисципліни");
            System.out.println("4. Отримання повного списку викладачів");
            System.out.println("5. Отримання списку дисциплін для заданого викладача");
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

    public static void main(String[] argv) {
        try (Client client = new Client()) {
            client.menu();
        } catch (IOException | TimeoutException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public String call(String message) throws IOException, InterruptedException, ExecutionException {
        final String corrId = UUID.randomUUID().toString();

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

        final CompletableFuture<String> response = new CompletableFuture<>();

        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.complete(new String(delivery.getBody(), "UTF-8"));
            }
        }, consumerTag -> {
        });

        String result = response.get();
        channel.basicCancel(ctag);
        return result;
    }

    public void close() throws IOException {
        connection.close();
    }
}
