package Mutual_Exclusion;

import constants.AppConstants;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Token extends UnicastRemoteObject implements TokenInterface {
    int token[];
    int queue[];
    int owner;
    int head;
    int tail;

    public Token() throws RemoteException {
        token = new int[4];
        queue = new int[100];
        owner = -1;
        head = 0;
        tail = 0;
    }

    public int[] getToken() throws RemoteException {
        return token;
    }

    public int[] getQueue() throws RemoteException {
        return queue;
    }

    public int getOwner() throws RemoteException {
        return owner;
    }

    public int getHead() throws RemoteException {
        return head;
    }

    public int getTail() throws RemoteException {
        return tail;
    }

    public void setToken(int index, int value) throws RemoteException {
        this.token[index] = value;
    }

    public void setQueue(int[] queue) throws RemoteException {
        this.queue = queue;
    }

    public void setOwner(int owner) throws RemoteException {
        this.owner = owner;
    }

    public void setHead(int head) throws RemoteException {
        this.head = head;
    }

    public void setTail(int tail) throws RemoteException {
        this.tail = tail;
    }

    public static void main(String args[]) throws RemoteException {
        Token token = new Token();
        try {
            // bind with token object
            Registry registry = LocateRegistry.createRegistry(AppConstants.TOKEN_SERVER);
            registry.bind(AppConstants.TOKEN, token);
            System.out.println("Token Service is started.");

        } catch (Exception e) {
            System.out.println("Exception" + e);
        }
    }
}