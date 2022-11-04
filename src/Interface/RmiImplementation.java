package Interface;

import Encryption.AesEncryption;
import Mutual_Exclusion.Token;
import Mutual_Exclusion.TokenInterface;
import constants.AppConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
public class RmiImplementation extends UnicastRemoteObject implements RmiInterface {
    public int pid;
    boolean isProcessCoordinator = false, isProcessDown = false;
    private LocalTime localTime;
    private String folderName = System.getProperty("user.dir") + "\\Storage";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    int RN[];
    int no_of_requests;
    boolean critical= false;
    // TokenInterface token;
    TokenInterface dtoken;

    public RmiImplementation(LocalTime localTime, int pid) throws RemoteException {
        File storageDir = new File(folderName);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        this.localTime = localTime;
        this.pid = pid;
        setupToken();
    }

    public RmiImplementation(Token token) throws RemoteException {
        this.dtoken = token;
    }
    public void uploadFileToServer(byte[] mydata, String serverpath, int length) throws RemoteException {
        try {
            if (dtoken.getOwner() == -1) {
                dtoken.setOwner(pid);
                System.out.println("No owner");
                no_of_requests++;
                RN[pid]++;
            } else {
                sendRequest();
            }
            while (dtoken.getCritical());
            System.out.println("Token for File Upload");
            dtoken.setCritical(true);
            File serverpathfile = new File(serverpath);
            FileOutputStream out = new FileOutputStream(serverpathfile);
            byte[] data = AesEncryption.encrypt(mydata);
            out.write(data);
            //sleep for 20 secs
            if(pid==0)
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            out.flush();
            out.close();
            dtoken.setCritical(false);
            releaseToken();
            System.out.println("Done writing data...");
            System.out.println("Synchronized Time is : " + localTime.format(formatter));

        } catch (IOException e) {

            e.printStackTrace();

            System.out.println("Couldn't write data...");
        }

    }

    public byte[] downloadFileFromServer(String serverpath) throws RemoteException {
        if (dtoken.getOwner() == -1) {
            dtoken.setOwner(pid);
            System.out.println("No owner");
            no_of_requests++;
            RN[pid]++;
        } else {
            sendRequest();
        }
        while (dtoken.getCritical())
            ;
        System.out.println("Token for File Download");
        dtoken.setCritical(true);
        byte[] mydata;
        File serverpathfile = new File(serverpath);
        System.out.println(serverpath);
        System.out.println(serverpathfile);
        mydata = new byte[(int) serverpathfile.length()];
        FileInputStream in;
        try {
            in = new FileInputStream(serverpathfile);
            try {
                in.read(mydata, 0, mydata.length);
            } catch (IOException e) {

                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {

                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        dtoken.setCritical(false);
        releaseToken();
        System.out.println("Token released");
        System.out.println("Synchronized Time is : " + localTime.format(formatter));
        return AesEncryption.decrypt(mydata);

    }

    public String[] listFiles(String serverpath) throws RemoteException {
        File serverpathdir = new File(serverpath);
        System.out.println("Synchronized Time is : " + localTime.format(formatter));

        return serverpathdir.list();

    }

    public boolean createDirectory(String serverpath) throws RemoteException {
        File serverpathdir = new File(serverpath);
        System.out.println("Synchronized Time is : " + localTime.format(formatter));

        return serverpathdir.mkdir();

    }

    public boolean removeDirectoryOrFile(String serverpath) throws RemoteException {
        File serverpathdir = new File(serverpath);
        System.out.println("Synchronized Time is : " + localTime.format(formatter));

        return deleteDir(serverpathdir);

    }

    static boolean deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        return file.delete();
    }

    @Override
    public LocalTime getLocalTime() throws RemoteException {
        return localTime;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public boolean isCoordinatorFlag() {
        return isProcessCoordinator;
    }

    public void setCoordinatorFlag(boolean isProcessCoordinator) {
        this.isProcessCoordinator = isProcessCoordinator;
    }

    public boolean isDownflag() {
        return isProcessDown;
    }

    public void setDownflag(boolean downflag) {
        this.isProcessDown = downflag;
    }

    @Override
    public void adjustTime(LocalTime localTime, long avgDiff) throws RemoteException {
        long localTimeNanos = localTime.toNanoOfDay();
        long thisNanos = getLocalTime().toNanoOfDay();
        var newNanos = thisNanos - localTimeNanos;
        newNanos = newNanos * -1 + avgDiff + thisNanos;
        LocalTime newLocalTime = LocalTime.ofNanoOfDay(newNanos);
        this.localTime = newLocalTime;
        System.out.println("Updated time: " + formatter.format(newLocalTime));

    }

    public void setupToken() {
        RN = new int[4];
        no_of_requests = 0;
        // try {
        //     dtoken.setCritical(false);
        // } catch (RemoteException e1) {
        //     e1.printStackTrace();
        // }
        System.out.print("\nToken is created\n");
        try {
            // lookup token
            Registry registry = LocateRegistry.getRegistry(AppConstants.SERVER_NAME, AppConstants.TOKEN_SERVER);
            dtoken = (TokenInterface) registry.lookup(AppConstants.TOKEN);
        } catch (Exception e) {

            System.out.println("Exception in this token message : " + e.getMessage());
        }
    }

    public void sendRequest() throws RemoteException {
        Registry myreg;
        no_of_requests++;
        for (int i = 0; i < 4; i++) {
            try {
                myreg = LocateRegistry.getRegistry(AppConstants.SERVER_NAME, getPort(pid));
                RmiInterface server = (RmiInterface) myreg.lookup(AppConstants.SERVER_NAME);
                server.recieveRequest(pid, no_of_requests);
            } catch (Exception e) {

                System.out.println("Exception occurred : " + e.getMessage());
            }
        }
    }

    int getPort(int pid){
        switch (pid){
            case 0:
                return AppConstants.MAIN_SERVER_PORT;
            case 1:
                return AppConstants.SERVER_PORT_1;
            case 2:
                return AppConstants.SERVER_PORT_2;
            case 3:
                return AppConstants.SERVER_PORT_3;
        }
        return 0;
    }

    @Override
    public void recieveRequest(int serverPort, int no_of_requests) throws RemoteException {
        System.out.println("Recieved request from " + serverPort);
        if (RN[serverPort] <= no_of_requests) {
            RN[serverPort] = no_of_requests;
            if (dtoken.getToken()[serverPort] == RN[serverPort]) {// removed +1
                // if (dtoken.getOwner() == pid) {
                      
                // }
                if (dtoken.getCritical()) {
                    // token.queue = i;
                    System.out.println("Add to queue");
                    dtoken.getQueue()[dtoken.getTail()] = serverPort;
                    dtoken.setTail(dtoken.getTail() + 1);
                } else {
                    System.out.println("Queue empty, setting owner");
                    dtoken.setOwner(serverPort);
                }

                
            }
            
        }
    }

    public void releaseToken() throws RemoteException {
        dtoken.setToken(pid, RN[pid]);
        if (dtoken.getHead() != dtoken.getTail()) {
            System.out.println("Release token");
            dtoken.setOwner(dtoken.getQueue()[dtoken.getHead()]);
            System.out.println("New owner" + dtoken.getOwner());
            dtoken.setHead(dtoken.getHead() + 1);
        }
    }
}
