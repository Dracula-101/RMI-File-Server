package Client;

import Interface.RmiInterface;
import constants.AppConstants;

import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client implements Serializable {
    static String environment;
    static String hostname;
    static int portnumber;
    static String clientpath = System.getProperty("user.dir");
    static Scanner sc = new Scanner(System.in);
    static final String serverpath = System.getProperty("user.dir") + "\\Storage\\";
    static String upload = "upload";
    static String download = "download";
    static String dir = "dir";
    static String mkdir = "mkdir";
    static String rmdir = "rmdir";
    static String rm = "rm";
    static String shutdown = "shutdown";
    static RmiInterface inter;

    static void startServer() throws NotBoundException {
        environment = System.getenv("SERVER_PORT");
        environment = "127.0.0.1:";
        System.out.println(environment);
        System.out.println("Enter the port number");
        portnumber = sc.nextInt();
        environment = environment + portnumber;
        hostname = environment.split(":")[0];

        portnumber = Integer.parseInt(environment.split(":")[1]);
        System.out.println("seeking connection on:" + environment);

        Registry myreg;
        try {
            myreg = LocateRegistry.getRegistry(hostname, portnumber);
            inter = (RmiInterface) myreg.lookup(AppConstants.SERVER_NAME);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    static void upload(String serverpath) throws IOException {
        File clientpathfile = new File(clientpath);
        byte[] mydata = new byte[(int) clientpathfile.length()];
        FileInputStream in;
        try {
            in = new FileInputStream(clientpathfile);
            System.out.println("Uploading to server...");
            in.read(mydata, 0, mydata.length);
            inter.uploadFileToServer(mydata, serverpath, (int) clientpathfile.length());
            in.close();
            System.out.println("Upload complete.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void download() {

        try {
            byte[] mydata = inter.downloadFileFromServer(serverpath);
            System.out.println(mydata);
            System.out.println("Downloading...");
            File clientpathfile = new File(clientpath);
            FileOutputStream out = new FileOutputStream(clientpathfile);
            out.write(mydata);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void listFiles() {
        String[] filelist;
        try {
            filelist = inter.listFiles(serverpath);
            for (String i : filelist) {
                System.out.println("->> " + i);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    static void createDir(String serverpath) {
        boolean bool;
        try {
            System.out.println(serverpath);
            bool = inter.createDirectory(serverpath);

            System.out.println("directory created :" + bool);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    static void deleteDir(String serverpath) {
        boolean bool;
        try {
            bool = inter.removeDirectoryOrFile(serverpath);
            System.out.println("directory deleted :" + bool);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    static String getName(String showString, String serverpath) {
        System.out.print("\n" + showString);
        String path = serverpath + sc.nextLine();
        return path;
    }

    static String getInput() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter the Client path:");
        clientpath = sc.nextLine();
        System.out.print("\nEnter the Server path:");
        String folderName = sc.nextLine();
        sc.close();
        return serverpath + "\\" + folderName;
        // sc.close();

    }

    static void shutDownClient() {
        System.exit(0);
        System.out.println("Client has shutdown. Close the console");
    }

    public static void main(String[] args) {
        try {
            startServer();
        } catch (NotBoundException e1) {
            e1.printStackTrace();
        }
        boolean shouldExit = false;
        Scanner sc = new Scanner(System.in);
        System.out.println("\nFILE DATA SERVER\n");
        while (!shouldExit) {
            System.out.print(
                    "\n1.Upload File\n2.Download File\n3.Make Directory\n4.Remove Directory\n5.List Directories\n6.Exit\n\nEnter the choice: ");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    try {
                        upload(
                                getInput()

                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 2:
                    getInput();

                    download();

                    break;

                case 3:
                    createDir(
                            getName("Enter the folder name: ", serverpath));
                    break;

                case 4:

                    deleteDir(getName("Enter the folder name to be deleted: ", serverpath));
                    break;

                case 5:
                    System.out.print("\nThe List of the files in database are: \n");
                    listFiles();
                    break;

                case 6:
                    shutDownClient();
                    sc.close();
                    break;
                default:
                    System.out.println("Please enter a valid number");
            }

        }
    }

}
