package MongoServer;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;


public class MongoDBServer {
    
    String mongoUrl = "mongodb+srv://root:root@server1.egacxjj.mongodb.net/?retryWrites=true&w=majority";
    MongoClient server;
    MongoDatabase mainServer, sideServer;
    
    MongoDBServer(){
        try {
            server = MongoClients.create(mongoUrl);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            mainServer = server.getDatabase("dataserver1");
            sideServer = server.getDatabase("dataserver2");
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }


    public MongoIterable<String> getMainServerCollections(){
        return mainServer.listCollectionNames();
    }

    public MongoIterable<String> getSideServerCollections(){
        return sideServer.listCollectionNames();
    }

    public MongoDatabase getMainServer(){
        return mainServer;
    }

    public MongoDatabase getSideServer(){
        return sideServer;
    }

    public void closeServer(){
        server.close();
    }

    // public void addTriggers(){
    //     server.tri
    // }

    public static void main(String[] args) {
        MongoDBServer server = new MongoDBServer();
        // Get access of first collection
        // Get access of second collection
        // Write something in first collection
        // Automatically copy to second collection
        // check if the update is same for the file and update the second collection
        server.closeServer();
    }


    
}
