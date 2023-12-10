package org.example.rmi.server;

import org.example.acdep.AcDepDAO;
import org.example.acdep.Subject;
import org.example.acdep.Teacher;
import org.example.rmi.AcDepRMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;

public class AcDepRMIImpl extends UnicastRemoteObject implements AcDepRMI {

    private AcDepDAO dao;

    public AcDepRMIImpl() throws RemoteException {
        try {
            dao = new AcDepDAO("acdep.db");
        } catch (SQLException e) {
            throw new RuntimeException("Помилка підключення бази даних", e);
        }
    }

    @Override
    public int insertTeacher(String name) throws RemoteException {
        dao.createTeacher(t);
        return 0;
    }

    @Override
    public int deleteTeacher(String name) throws RemoteException {
        dao.deleteTeacher(t);
        return 0;
    }

    @Override
    public int insertSubject(String subjectName, String teacherName) throws RemoteException {
        return 0;
    }

    @Override
    public int deleteSubject(String name) throws RemoteException {
        return 0;
    }

    @Override
    public int updateTeacher(String oldName, String newName) throws RemoteException {
        return 0;
    }

    @Override
    public List<Subject> subjOfTeacher(String teacherName) throws RemoteException {
        return null;
    }

    @Override
    public Subject findSubj(String name) throws RemoteException {
        return null;
    }

    @Override
    public List<Teacher> getAllTeachers() throws RemoteException {
        return null;
    }
}
