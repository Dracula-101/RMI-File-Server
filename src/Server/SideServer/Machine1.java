package Server.SideServer;
// package RMI.Server.SideServer;
// import AppConstants;
import Interface.RmiImplementation;
import Interface.RmiInterface;
import constants.AppConstants;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Machine1 {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        try {
            LocalTime hour = LocalTime.parse(AppConstants.MACHINE_1_HOUR, formatter);
            RmiInterface machineServer = new RmiImplementation(hour,1);
            Registry registry = LocateRegistry.createRegistry(AppConstants.SERVER_PORT_1);
            registry.rebind("localhost", machineServer);
            System.out.println(String.format("Machine 1 started on port %d [local time: %s].",
                    AppConstants.SERVER_PORT_1,
                    formatter.format(hour)));
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

}