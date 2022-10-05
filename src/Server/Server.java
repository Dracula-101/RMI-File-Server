package Server;

import ElectionAlgo.BullyAlgorithm;
import Interface.RmiImplementation;
import constants.AppConstants;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Server extends UnicastRemoteObject {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    protected Server() throws RemoteException {
        super();
    }

    static int portnumber = 3000;
    // String remoteObject ="remoteObject";
    static LocalTime time = LocalTime.now();

    public static void main(String[] args) throws Exception {

        System.setProperty("java.rmi.server.hostname", "127.0.0.1");
        try {
            Registry reg = LocateRegistry.createRegistry(portnumber);
            RmiImplementation imp = new RmiImplementation(time,0);
            reg.bind(AppConstants.SERVER_NAME, imp);
            System.out.println("Server is ready.");
            System.out.println(portnumber);
            LocalTime localTime = LocalTime.parse(AppConstants.LOCAL_HOUR, formatter);
            System.out.println("Time local: " + formatter.format(localTime));
            new BullyAlgorithm();

        } catch (Exception e) {
            System.out.println("Server failed: " + e);
        }

    }

}
