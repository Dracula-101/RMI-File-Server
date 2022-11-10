package LoadBalancer;

import java.rmi.Remote;
import java.rmi.RemoteException;

import Interface.RmiInterface;

public interface LoadBalancerInterface extends Remote{
    
    public RmiInterface getServer() throws RemoteException;
    public int getServerPort() throws RemoteException;

}