package org.example.rmi.server;

import org.example.acdep.AcDepDAO;
import org.example.acdep.AcDepUtil;
import org.example.acdep.Subject;
import org.example.acdep.Teacher;
import org.example.rmi.AcDepRMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;

public class AcDepRMIImpl extends UnicastRemoteObject implements AcDepRMI {

    private final AcDepDAO dao;

    public AcDepRMIImpl() throws RemoteException {
        try {
            dao = new AcDepDAO("acdep.db");
        } catch (SQLException e) {
            throw new RuntimeException("Помилка підключення бази даних", e);
        }
    }

    @Override
    public int insertTeacher(String name) throws RemoteException {
        int id = AcDepUtil.getIDT(dao.readTeachers(null));
        dao.createTeacher(new Teacher(id, name));
        return 0;
    }

    @Override
    public int deleteTeacher(String name) throws RemoteException {
        Teacher del = AcDepUtil.getTeacher(dao.readTeachers(null), name);
        if (del == null) {
            return 1;
        } else {
            dao.deleteTeacher(del);
            return 0;
        }
    }

    @Override
    public int insertSubject(String subjectName, String teacherName) throws RemoteException {
        int id = AcDepUtil.getIDS(dao.readSubjects(null));
        dao.createSubject(new Subject(id, subjectName, AcDepUtil.getTeacher(dao.readTeachers(null), teacherName)));
        return 0;
    }

    @Override
    public int deleteSubject(String name) throws RemoteException {
        Subject del = AcDepUtil.getSubject(dao.readSubjects(null), name);
        if (del == null) {
            return 1;
        } else {
            dao.deleteSubject(del);
            return 0;
        }
    }

    @Override
    public int updateTeacher(String oldName, String newName) throws RemoteException {
        Teacher upd = AcDepUtil.getTeacher(dao.readTeachers(null), oldName);
        if (upd == null) {
            return 1;
        } else {
            upd.name = newName;
            dao.updateTeachers(upd);
            return 0;
        }
    }

    @Override
    public List<Subject> subjOfTeacher(String teacherName) throws RemoteException {
        Teacher teacher = AcDepUtil.getTeacher(dao.readTeachers(null), teacherName);
        if (teacher == null) {
            return null;
        } else {
            return dao.readSubjects("SELECT * FROM Предмети WHERE Викладач = " + teacher.code);
        }
    }

    @Override
    public List<Subject> findSubj(String name) throws RemoteException {
        List<Subject> subjectList = dao.readSubjects("SELECT * FROM Предмети WHERE Назва = " + name);
        if(subjectList.isEmpty()) {
            return null;
        } else {
            return subjectList;
        }
    }

    @Override
    public List<Teacher> getAllTeachers() throws RemoteException {
        return dao.readTeachers(null);
    }
}
