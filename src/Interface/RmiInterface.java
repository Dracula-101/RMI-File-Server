package Interface;

import java.rmi.*;
import java.time.LocalTime;
public interface RmiInterface extends Remote {
    void uploadFileToServer(byte[] mybyte, String serverpath, int length) throws RemoteException;

    byte[] downloadFileFromServer(String servername) throws RemoteException;

    String[] listFiles(String serverpath) throws RemoteException;

    boolean createDirectory(String serverpath) throws RemoteException;

    boolean removeDirectoryOrFile(String serverpath) throws RemoteException;

    LocalTime getLocalTime() throws RemoteException;

    int getPid() throws RemoteException;

    void setCoordinatorFlag(boolean b) throws RemoteException;

    void setDownflag(boolean b) throws RemoteException;

    boolean isDownflag() throws RemoteException;

    boolean isCoordinatorFlag() throws RemoteException;

    void adjustTime(LocalTime localTime, long avgDiff) throws RemoteException;

    void recieveRequest(int serverPort, int no_of_requests, String state) throws RemoteException;
}
