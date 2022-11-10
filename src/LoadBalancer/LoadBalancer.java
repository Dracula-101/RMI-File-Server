package LoadBalancer;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import constants.*;
import Interface.RmiInterface;


public class LoadBalancer extends UnicastRemoteObject implements LoadBalancerInterface {
    static int noOfRetry = 0;
    int noOfServers;
    int noOfRequests;

    public LoadBalancer() throws RemoteException {
        noOfServers = 4;
        noOfRequests = 0;
    }

    public int getServerPort() throws RemoteException {
        int serverNo = noOfRequests % noOfServers;
        return getServerPort(serverNo);
    }

    public static void main(String[] args) throws RemoteException {
        try {
            Registry reg = LocateRegistry.createRegistry(AppConstants.LOAD_BALANCER_PORT);
            reg.rebind(AppConstants.LOAD_BALANCER_NAME, new LoadBalancer());
            System.out.println("Load Balancer is running on port " + AppConstants.LOAD_BALANCER_PORT);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public RmiInterface getServer() throws RemoteException {
        int serverNo = noOfRequests % noOfServers;
        noOfRetry++;
        RmiInterface server = null;
        
        if(noOfRetry > 4){
            System.out.println("Unable to connect to any server, Please try again later");
            noOfRetry = 0;
            return server;
        }
        
        try {
            System.out.println("Trying to connect to server with port " + getServerPort(serverNo));
            Registry reg = LocateRegistry.getRegistry(AppConstants.SERVER_NAME, getServerPort(serverNo));
            server = (RmiInterface) reg.lookup(AppConstants.SERVER_NAME);
            noOfRequests++;
        } catch (Exception e) {
            System.out.println("Unable to connect with portno ("+getServerPort(serverNo)+"), Retrying next one" + e);
            server = this.getServer();
        }
        return server;
    }

    int getServerPort(int serverNo){
        switch (serverNo) {
            case 0:
                return AppConstants.MAIN_SERVER_PORT;
            case 1:
                return AppConstants.SERVER_PORT_1;
            case 2:
                return AppConstants.SERVER_PORT_2;
            case 3:
                return AppConstants.SERVER_PORT_3;
            default:
                return AppConstants.MAIN_SERVER_PORT;
        }
    }

}