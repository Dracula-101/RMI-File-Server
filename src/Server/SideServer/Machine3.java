package Server.SideServer;
import Interface.RmiImplementation;
import Interface.RmiInterface;
import constants.AppConstants;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Representation of machine 3 to have its time set.
 */
public class Machine3 {

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        try {
            LocalTime hour = LocalTime.parse(AppConstants.MACHINE_3_HOUR, formatter);
            RmiInterface machineServer = new RmiImplementation(hour, 3);
            Registry registry = LocateRegistry.createRegistry(AppConstants.SERVER_PORT_3);
            registry.rebind(AppConstants.SERVER_NAME, machineServer);
            System.out.println(String.format("Machine 3 started on port %d [local time: %s].",
                    AppConstants.SERVER_PORT_3,
                    formatter.format(hour)));
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

}
