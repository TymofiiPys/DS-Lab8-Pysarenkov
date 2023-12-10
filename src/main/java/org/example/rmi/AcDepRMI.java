package org.example.rmi;

import org.example.acdep.Teacher;
import org.example.acdep.Subject;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface AcDepRMI extends Remote {
    int insertTeacher(String name) throws RemoteException;
    int deleteTeacher(String name) throws RemoteException;
    int insertSubject(String subjectName, String teacherName) throws RemoteException;
    int deleteSubject(String name) throws RemoteException;
    int updateTeacher(String oldName, String newName) throws RemoteException;
    List<Subject> subjOfTeacher(String teacherName) throws RemoteException;
    List<Subject> findSubj(String name) throws RemoteException;
    List<Teacher> getAllTeachers() throws RemoteException;
}
