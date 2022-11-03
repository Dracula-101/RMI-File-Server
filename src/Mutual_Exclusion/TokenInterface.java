package Mutual_Exclusion;
import constants.AppConstants.TokenState;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TokenInterface extends Remote {
    public int[] getToken() throws RemoteException;

    public TokenState[] getQueue() throws RemoteException;

    public int getOwner() throws RemoteException;

    public int getHead() throws RemoteException;

    public int getTail() throws RemoteException;

    public void setToken(int index, int value, String state) throws RemoteException;

    public void setQueue(TokenState[] queue) throws RemoteException;

    public void setOwner(int owner) throws RemoteException;

    public void setHead(int head) throws RemoteException;

    public void setTail(int tail) throws RemoteException;

    public String getState() throws RemoteException;

    public void setState(String state) throws RemoteException;

    public boolean isFree(String state) throws RemoteException;
}