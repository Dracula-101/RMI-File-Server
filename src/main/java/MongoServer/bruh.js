package MongoServer;

public exports = function(changeEvent) {
  
    // A Database Trigger will always call a function with a changeEvent.
    // Documentation on ChangeEvents: https://docs.mongodb.com/manual/reference/change-events/

    // Access the _id of the changed document:
    const docId = changeEvent.documentKey._id;

    // Access the latest version of the changed document
    // (with Full Document enabled for Insert, Update, and Replace operations):
    const fullDocument = changeEvent.fullDocument;

    const updateDescription = changeEvent.updateDescription;

    const mainServer = context.services.get("Server1").db("mainserver").collection("data");

    // upload the updated document
   try {
    // check if the doc exists in main server
    const doc = mainServer.findOne({_id: docId});
    if (doc) {
      // update the doc
      mainServer.updateOne({
        _id: docId
      }, {
        $set: fullDocument
      });
    } else {
      // insert the doc
      mainServer.insertOne(fullDocument);
      
  
    } catch (error) {
      
    }


  
};
