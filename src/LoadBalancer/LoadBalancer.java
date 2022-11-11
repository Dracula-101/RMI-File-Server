package LoadBalancer;

import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.List;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import constants.*;
import Interface.RmiInterface;


public class LoadBalancer extends UnicastRemoteObject implements LoadBalancerInterface {

    List<ServerNode> serverList = new ArrayList<ServerNode>();
    static int noOfRetry = 0;
    int noOfServers;
    int noOfRequests;
    int lastPortConnected = AppConstants.MAIN_SERVER_PORT;

    public LoadBalancer() throws RemoteException {
        noOfServers = 4;
        noOfRequests = 0;
        serverList.add(new ServerNode(AppConstants.MAIN_SERVER_PORT, 4));
        serverList.add(new ServerNode(AppConstants.SERVER_PORT_1, 2));
        serverList.add(new ServerNode(AppConstants.SERVER_PORT_2, 1));
        serverList.add(new ServerNode(AppConstants.SERVER_PORT_3, 1));
    }

    public int getServerPort() throws RemoteException {
        return lastPortConnected;
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
        // int serverNo = noOfRequests % noOfServers;
        noOfRetry++;
        RmiInterface server = null;
        if(noOfRetry > 4){
            System.out.println("Unable to connect to any server, Please try again later");
            noOfRetry = 0;
            return server;
        }
        
        try {
            // System.out.println("Trying to connect to server with port " + getServerPort(serverNo));
            // Registry reg = LocateRegistry.getRegistry(AppConstants.SERVER_NAME, getServerPort(serverNo));
            // server = (RmiInterface) reg.lookup(AppConstants.SERVER_NAME);
            // System.out.println("Client connected to server with port "+getServerPort(serverNo));
            for(ServerNode node : serverList){
                if(node.weightage >= 1){
                    System.out.println("Trying to connect to server with port " + getServerPort());
                    Registry reg = LocateRegistry.getRegistry(AppConstants.SERVER_NAME, getServerPort());
                    server = (RmiInterface) reg.lookup(AppConstants.SERVER_NAME);
                    System.out.println("Client connected to server with port "+getServerPort());
                    lastPortConnected = node.port;
                    --node.weightage;
                    noOfRequests++;
                    noOfRetry = 0;
                    return server;
                }
            }
        } catch (Exception e) {
            System.out.println("Unable to connect with portno ("+getServerPort()+"), Retrying next one" + e);
            noOfRetry++;
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

class ServerNode {
    
    int port;
    int weightage;

    public ServerNode(int port, int weightage) {
        this.port = port;
        this.weightage = weightage;
    }

    public int getPort() {
        return port;
    }

    public int getWeightage() {
        return weightage;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setWeightage(int weightage) {
        this.weightage = weightage;
    }

}