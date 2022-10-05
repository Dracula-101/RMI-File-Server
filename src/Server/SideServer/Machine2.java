package Server.SideServer;

import Interface.RmiImplementation;
import Interface.RmiInterface;
import constants.AppConstants;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Machine2 {

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        try {
            LocalTime hour = LocalTime.parse(AppConstants.MACHINE_2_HOUR, formatter);
            RmiInterface machineServer = new RmiImplementation(hour,2);
            Registry registry = LocateRegistry.createRegistry(AppConstants.SERVER_PORT_2);
            registry.rebind("localhost", machineServer);
            System.out.println(String.format("Machine 2 started on port %d [local time: %s].",
                    AppConstants.SERVER_PORT_2,
                    formatter.format(hour)));
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

}