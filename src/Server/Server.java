package Server;

import Interface.RmiImplementation;
import Interface.RmiInterface;
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
    // String remoteObject ="remoteObject";
    static LocalTime time = LocalTime.now();

    public static void main(String[] args) {

        System.setProperty("java.rmi.server.hostname", "127.0.0.1");
        try {
            Registry reg = LocateRegistry.createRegistry(AppConstants.MAIN_SERVER_PORT);
            RmiImplementation imp = new RmiImplementation(time,0);
            reg.bind(AppConstants.SERVER_NAME, imp);
            System.out.println("Server is ready.");
            System.out.println(AppConstants.MAIN_SERVER_PORT);
            LocalTime localTime = LocalTime.parse(AppConstants.LOCAL_HOUR, formatter);
            System.out.println("Time local: " + formatter.format(localTime));
            // new BullyAlgorithm();

            // creation of servers (machines)
			RmiInterface machine1Server = createMachineServer(1);
			RmiInterface machine2Server = createMachineServer(2);
			RmiInterface machine3Server = createMachineServer(3);

			// calculate the average of the hours
			var avgDiff = generateAverageTime(localTime,
					machine1Server.getLocalTime());

			// adjust servers time
			machine1Server.adjustTime(localTime, avgDiff);
			machine2Server.adjustTime(localTime, avgDiff);
			machine3Server.adjustTime(localTime, avgDiff);
			localTime = localTime.plusNanos(avgDiff);

			System.out.println("\nUpdated schedules!");
			System.out.println("Local time: " + formatter.format(localTime));
			System.out.println("Server 1 time: " + formatter.format(machine1Server.getLocalTime()));
			System.out.println("Server 2 time: " +
					formatter.format(machine2Server.getLocalTime()));
			System.out.println("Server 3 time: " +
					formatter.format(machine3Server.getLocalTime()));
        } catch (Exception e) {
            System.out.println("Server failed: " + e);
        }

    }

    private static RmiInterface createMachineServer(int machineNumber) throws Exception {
		String serverName = AppConstants.SERVER_NAME;
		int serverPort = 0;
		if (machineNumber == 1) {
			serverPort = AppConstants.SERVER_PORT_1;
		} else if (machineNumber == 2) {
			serverPort = AppConstants.SERVER_PORT_2;
		} else if (machineNumber == 3) {
			serverPort = AppConstants.SERVER_PORT_3;
		} else {
			serverPort = -1;
		}
		Registry machineRegistry = LocateRegistry.getRegistry(serverName, serverPort);

		RmiInterface machineServerTime = (RmiInterface) machineRegistry.lookup(AppConstants.SERVER_NAME);

		LocalTime machineTime = machineServerTime.getLocalTime();
		System.out.println("Connection with the machine " + machineNumber + " successfully established. Hour: "
				+ formatter.format(machineTime));
		return machineServerTime;
	}

	private static long generateAverageTime(LocalTime localTime, LocalTime... times) {
		long nanoLocal = localTime.toNanoOfDay();
		long difServer = 0;
		for (LocalTime t : times) {
			difServer += t.toNanoOfDay() - nanoLocal;
		}
		return difServer / (times.length + 1);
	}
}
