package ElectionAlgo;

import Interface.*;
import constants.AppConstants;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;
import java.time.format.DateTimeFormatter;
public class BullyAlgorithm extends UnicastRemoteObject{
    final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static RmiInterface leader;

    public BullyAlgorithm() throws Exception {

        super();
        // store the servers in a list
        ArrayList<RmiInterface> sl = new ArrayList<RmiInterface>();

        RmiInterface mc0 = createMachineServer(0);
        RmiInterface mc1 = createMachineServer(1);
        RmiInterface mc2 = createMachineServer(2);
        RmiInterface mc3 = createMachineServer(3);

        sl.add(mc1);
        sl.add(mc2);
        sl.add(mc3);
        sl.add(mc0);
        System.out.println("----------------------------");
        // initially select a leader
        System.out.println("Initial Election Started...");
        leader = initialElection(sl, sl.size());
        System.out.println("Coordinator is : P" + leader.getPid());
        System.out.println("----------------------------");

        // processes communicate with the leader. If leader is down, then election
        // process is executed.
        pingLeader(sl, leader);
        while (ProcessElection.isElectionFlag()) {
            election(sl, leader);
        }
        ProcessElection.setPingLeaderFlag(true);
    }


    public static RmiInterface createMachineServer(int machineNumber) throws Exception {
        String serverName = AppConstants.SERVER_NAME;

        int serverPort = 0;
        switch (machineNumber) {

            case 0:
                serverPort = AppConstants.MAIN_SERVER_PORT;
                break;
            case 1:
                serverPort = AppConstants.SERVER_PORT_1;
                break;
            case 2:
                serverPort = AppConstants.SERVER_PORT_2;
                break;
            case 3:
                serverPort = AppConstants.SERVER_PORT_3;
                break;
            default:
                serverPort = -1;
        }

        Registry machineRegistry = LocateRegistry.getRegistry(serverName, serverPort);
        RmiInterface machineServerTime = (RmiInterface) machineRegistry.lookup(AppConstants.SERVER_NAME);
        LocalTime machineTime = machineServerTime.getLocalTime();
        System.out.println("-----------------------------------------");
        System.out.println("Connection with machine " + machineNumber + " successfully established. Hour: "
                + formatter.format(machineTime));
        return machineServerTime;
    }

    // initially select a leader
    public static RmiInterface initialElection(ArrayList<RmiInterface> sl, int no_of_processes) throws RemoteException {
        LocalTime localTime = LocalTime.parse(AppConstants.LOCAL_HOUR, formatter);
        RmiInterface temp = new RmiImplementation(localTime, 0);
        for (int i = 0; i < sl.size(); i++) {
            RmiInterface p = (RmiInterface) sl.get(i);
            if (temp.getPid() < p.getPid())
                temp = p;
        }
        temp.setCoordinatorFlag(true);
        return temp;
    }

    // elect new leader
    public static void election(ArrayList<RmiInterface> pl, RmiInterface leader) throws RemoteException {
        RmiInterface ed = ProcessElection.getElectionInitiator();
        if ((ed.getPid()) == leader.getPid()) {
            RmiInterface oldLeader = leader;
            (oldLeader).setDownflag(false);
            leader = pl.get(ed.getPid() - 2);
            ProcessElection.setElectionFlag(false);
            leader.setCoordinatorFlag(true);
            System.out.println("-----------------------------------------");
            System.out.println("\nNew Coordinator is : P" + leader.getPid());
        } else {
            System.out.print("\n");
            for (int i = ed.getPid() + 1; i <= pl.size(); i++) {
                System.out.println("P" + ed.getPid() + ": Sending message to P" + i);
            }
            // get next higher node
            RmiInterface a = pl.get(ed.getPid());
            ProcessElection.setElectionInitiator(a);
        }

    }

    // ping leader to check if its active
    public static void pingLeader(ArrayList<RmiInterface> pl, RmiInterface leader) throws RemoteException {
        Random random = new Random();
        int r = random.nextInt(4) + 1;
        int j = 0;
        System.out.println("-----------------------------------------");
        while (ProcessElection.isPingLeaderFlag()) {
            for (int i = 0; i < pl.size(); i++) {
                RmiInterface p = pl.get(i);
                if (!(p.isCoordinatorFlag())) {
                    System.out.println("Process Id ( " + p.getPid() + " ) : Coordinator, are you present?");
                    j++;
                    if (j == r)
                        leader.setDownflag(true);
                    if (!(leader.isDownflag()))
                        System.out.println("Process Id ( " + leader.getPid() + " ) : Yes");
                    else {
                        ProcessElection.setElectionFlag(true);
                        ProcessElection.setElectionInitiator(p);
                        System.out.println(
                                "Process Id ( " + p.getPid() + " ) : Coordinator is down.\nInitiating new election");
                        ProcessElection.setPingLeaderFlag(false);
                        break;
                    }
                }
            }
        }
        System.out.println("-----------------------------------------");
    }
}

class ProcessElection {
    private static boolean isElection = false;
    private static boolean isPingLeader = true;
    public static RmiInterface electionInitiator;

    public static RmiInterface getElectionInitiator() {
        return electionInitiator;
    }

    public static void setElectionInitiator(RmiInterface electionInitiator) {
        ProcessElection.electionInitiator = electionInitiator;
    }

    public static boolean isPingLeaderFlag() {
        return isPingLeader;
    }

    public static void setPingLeaderFlag(boolean pingLeaderFlag) {
        ProcessElection.isPingLeader = pingLeaderFlag;
    }

    public static boolean isElectionFlag() {
        return isElection;
    }

    public static void setElectionFlag(boolean electionFlag) {
        ProcessElection.isElection = electionFlag;
    }

}
