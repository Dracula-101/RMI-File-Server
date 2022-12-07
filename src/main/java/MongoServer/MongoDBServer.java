package MongoServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.bson.Document;
import org.bson.types.Binary;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import Encryption.AesEncryption;


public class MongoDBServer {
    
    String mongoUrl = "mongodb+srv://root:root@server1.egacxjj.mongodb.net/?retryWrites=true&w=majority";
    static MongoClient server;
    static MongoDatabase mainServer, sideServer;
    static Scanner sc = new Scanner(System.in);
    
    public MongoDBServer(){
        try {
            server = MongoClients.create(mongoUrl);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            mainServer = server.getDatabase("mainserver");
            sideServer = server.getDatabase("sideserver");
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void uploadFile(File filepath, boolean isSideServer){
        byte[] mydata = new byte[(int) filepath.length()];
        byte [] outputData=null;
        try{
            FileInputStream clientFileStream = new FileInputStream(filepath);
            clientFileStream.read(mydata, 0, mydata.length);
            clientFileStream.close();
            outputData = AesEncryption.encrypt(mydata);
            
        } catch (IOException e) {
            System.out.println("Error: " + e);
        } 
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String uploadDate = formatter.format(date);
        formatter = new SimpleDateFormat("HH:mm:ss");
        String uploadTime = formatter.format(date);
        sideServer.getCollection("data").insertOne(new Document("name", filepath.getName()).append("uploaded_date",uploadDate).append("uploaded_time", uploadTime ).append("data", outputData));
        
        System.out.println("File uploaded successfully to MongoDB Server"); 
    }

    public File downloadFile(String filename,Document doc, boolean isSideServer){
        Binary bin = doc.get("data", org.bson.types.Binary.class);
        byte[] data = bin.getData();
        byte[] outputData = AesEncryption.decrypt(data);
        // create MongoDb folder
        File folder = new File(System.getProperty("user.dir") + "\\MongoDB");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File downloadedFile = new File(System.getProperty("user.dir") + "\\MongoDB\\" + filename);
        try {
            downloadedFile.createNewFile();
            java.io.FileOutputStream fos = new java.io.FileOutputStream(downloadedFile);
            fos.write(outputData);
            fos.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
        System.out.println("File downloaded successfully from MongoDB Server and saved in MongoDB dir");
        return downloadedFile;
    }

    public void updateFile(String filename, boolean isSideServer){
        String newFileName = System.getProperty("user.dir") + "\\"+ filename;
        File filePath = new File(newFileName);
        if (filePath.exists()) {
            byte[] mydata = new byte[(int) filePath.length()];
            byte[] outputData=null;
            try{
                FileInputStream clientFileStream = new FileInputStream(filePath);
                clientFileStream.read(mydata, 0, mydata.length);
                clientFileStream.close();
                outputData = AesEncryption.encrypt(mydata);
            }
            catch (IOException e) {
                System.out.println("Error: " + e);
            }
            sideServer.getCollection("data").updateOne(new Document("name", filename), new Document("$set", new Document("data", outputData)));
            // System.out.print("Do you want to download the file? (y/n)");
            // String choice = sc.nextLine();
            // if (choice.equals("y")) {
            //     downloadFile(filename, sideServer.getCollection("data").find(new Document("name", filename)).first(), isSideServer);
            // } else {
            //     System.out.println("File not found");
            // }
            System.out.println("File updated successfully to MongoDB Server");
            sc.close();
        } else {
            System.out.println("File not found.\nMake sure you have the file locally in your root dir");
        }
    }

    public void startServer() {


        MongoDBServer server = new MongoDBServer();
        boolean shouldExit = false;
        System.out.println("\n\n------------------------------------");
        System.out.println("Data Consistency Using MongoDB");
        System.out.println("------------------------------------");
        while (!shouldExit) {
            System.out.print(
                    "\n1.Upload File\n2.Download File\n3.Update File\n4.Exit\n\nEnter the choice: ");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    System.out.print("Enter the file name in root dir: ");
                    String fileName = System.getProperty("user.dir") + "\\"+sc.next();
                    File file = new File(fileName);
                    if (file.exists()) {
                        server.uploadFile(file, false);  
                    } else {
                        System.out.println("File not found");
                    }
                    break;
                case 2:
                    System.out.print("Enter the file name to be downloaded from server: ");
                    String fileName2 = sc.next();
                    Document doc = mainServer.getCollection("data").find(new Document("name", fileName2)).first();
                    if (doc != null) {
                        server.downloadFile(fileName2, doc, false);
                        
                    } else {
                        System.out.println("File not found on MongoDB Server");
                    }
                    break;
                case 3:
                    System.out.print("Enter the file name to be updated: ");
                    String fileName3 = sc.next();
                    Document docs = sideServer.getCollection("data").find(new Document("name", fileName3)).first();
                    if (docs != null) {
                        server.updateFile(fileName3, false);
   
                    } else {
                        System.out.println("File not found on MongoDB Server");
                    }
                    break;
                case 4:
                    shouldExit = true;
                    break;
                default:
                    System.out.println("Please enter a valid number");
            }

        }
        sc.close();
    }


    
}
