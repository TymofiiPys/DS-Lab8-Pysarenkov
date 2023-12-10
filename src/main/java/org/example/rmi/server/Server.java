package org.example.rmi.server;

import org.example.rmi.AcDepRMI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) throws RemoteException {
        AcDepRMI acDepRMI = new AcDepRMIImpl();
        Registry registry = LocateRegistry.createRegistry(8080);
        registry.rebind("AcDep", acDepRMI);
        System.out.println("Сервер запущено!");
    }

}
