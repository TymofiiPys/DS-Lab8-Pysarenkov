package org.example.mq.server;

import com.rabbitmq.client.*;
import org.example.acdep.AcDepDAO;
import org.example.acdep.AcDepUtil;
import org.example.acdep.Subject;
import org.example.acdep.Teacher;

import java.sql.SQLException;
import java.util.List;

public class Server {
    private static final String RPC_QUEUE_NAME = "rpc_queue";
    private static final char splitter = '%';
    private static AcDepDAO dao;

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
        channel.queuePurge(RPC_QUEUE_NAME);

        channel.basicQos(1);

        try {
            dao = new AcDepDAO("acdep.db");
        } catch (SQLException e) {
            throw new RuntimeException("Помилка підключення до бази даних", e);
        }

        System.out.println("Сервер запущено. Очікую запити");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();

            String response = "";
            try {
                String message = new String(delivery.getBody(), "UTF-8");
                String[] query = message.split("" + splitter);
                int command = Integer.parseInt(query[0]);
                List<Teacher> teacherList;
                List<Subject> subjectList;
                switch (command) {
                    case 1 -> {
                        int id = AcDepUtil.getIDT(dao.readTeachers(null));
                        dao.createTeacher(new Teacher(id, query[1]));
                        response = "" + 0;
                    }
                    case 2 -> {
                        int id = AcDepUtil.getIDS(dao.readSubjects(null));
                        dao.createSubject(new Subject(id, query[1], AcDepUtil.getTeacher(dao.readTeachers(null), query[2])));
                        response = "" + 0;
                    }
                    case 3 -> {
                        Subject del = AcDepUtil.getSubject(dao.readSubjects(null), query[1]);
                        if (del == null) {
                            response = "" + 1 + splitter + "Предмет із даною назвою не знайдено";
                        } else {
                            dao.deleteSubject(del);
                            response = "" + 0 + splitter + "Успішно";
                        }
                    }
                    case 4 -> {
                        teacherList = dao.readTeachers(null);
                        response = "" + 0 + splitter + AcDepUtil.listToStringT(teacherList);
                    }
                    case 5 -> {
                        Teacher teacher = AcDepUtil.getTeacher(dao.readTeachers(null), query[1]);
                        if (teacher == null) {
                            response = "" + 1 + splitter + "Учителя із даним ПІБ не знайдено";
                        } else {
                            subjectList = dao.readSubjects("SELECT * FROM Предмети WHERE Викладач = " + teacher.code);
                            response = "" + 0 + splitter + AcDepUtil.listToStringS(subjectList);
                        }
                    }
                    default -> {
                        response = "1" + splitter + "Невідома команда";
                    }
                }
            } catch (RuntimeException e) {
                System.out.println(" [.] " + e);
            } finally {
                channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> {
        }));
    }
}