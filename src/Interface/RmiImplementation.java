package Interface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class RmiImplementation extends UnicastRemoteObject implements RmiInterface{
    public int pid;
    boolean isProcessCoordinator = false, isProcessDown = false;
    private LocalTime localTime;
    private String folderName = System.getProperty("user.dir") + "\\Storage";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public RmiImplementation(LocalTime localTime, int pid) throws RemoteException {
        File storageDir = new File(folderName);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        this.localTime = localTime;
        this.pid = pid;
    }

    public RmiImplementation() throws RemoteException {
        super();
    }

    public void uploadFileToServer(byte[] mydata, String serverpath, int length) throws RemoteException {

        try {
            File serverpathfile = new File(serverpath);
            FileOutputStream out = new FileOutputStream(serverpathfile);
            byte[] data = mydata;
            out.write(data);
            out.flush();
            out.close();

            System.out.println("Done writing data...");
            System.out.println("Synchronized Time is : " + localTime.format(formatter));

        } catch (IOException e) {

            e.printStackTrace();

            System.out.println("Couldn't write data...");
        }

    }

    public byte[] downloadFileFromServer(String serverpath) throws RemoteException {

        byte[] mydata;

        File serverpathfile = new File(serverpath);
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

        System.out.println("Synchronized Time is : " + localTime.format(formatter));
        return mydata;

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
}
