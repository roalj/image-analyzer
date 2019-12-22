package services;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Iterator;

@ApplicationScoped
public class AnalyzerBean {

    @Inject
    MongoClient mongoClient;

    public void getAll() {
        MongoDatabase database = mongoClient.getDatabase("image-analyzer");
        MongoCollection<Document> collection = database.getCollection("analysis");
        FindIterable<Document> iterDoc = collection.find();
        int i = 1;

        // Getting the iterator
        Iterator it = iterDoc.iterator();

        while (it.hasNext()) {
            System.out.println(it.next());
            i++;
        }
    }

    public void createOne() {
        MongoDatabase database = mongoClient.getDatabase("image-analyzer");
        MongoCollection<Document> collection = database.getCollection("analysis");
        Document document = new Document("title", "MongoDB")
                .append("id", 1)
                .append("description", "database")
                .append("likes", 100)
                .append("url", "http://www.tutorialspoint.com/mongodb/")
                .append("by", "tutorials point");
        collection.insertOne(document);
    }

}
