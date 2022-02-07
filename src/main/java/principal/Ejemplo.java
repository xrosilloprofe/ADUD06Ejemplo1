package principal;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Projections.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;

public class Ejemplo {

	public static void main(String[] args) {

		// Replace the uri string with your MongoDB deployment's connection string
		// exemple: String uri =//
		// "mongodb://user:pass@sample.host:27017/?maxPoolSize=20&w=majority";
		// For localhost and default port, no need to add a uri
		String uri = "mongodb://192.168.1.38:27017/";
		
		try (MongoClient cliente = MongoClients.create(uri)) {

			MongoDatabase db = cliente.getDatabase("training");
			MongoCollection<Document> col_Dino = db.getCollection("dinosaurs");

			//Consultar todos los datos de una colección con una lista			
			List<Document> consulta = col_Dino.find().into(new ArrayList<Document>());
			for (Document d : consulta) {
				System.out.println(d.toString());
			}

			//Consultar ciertas claves
			for (Document d : consulta) {

				System.out.println("Dinosaurio: " + d.getString("name")
						+ ", peso: " + d.getInteger("weight", 0) //valdrá 0 si no encuentra la clave
						+ ", excavaciones:" + ((List<Integer>) d.get("excavations")).toString());
			}

			//Consultar todos los datos de una colección con un iterable
			MongoCursor<Document> cursor = col_Dino.find().iterator();
			while (cursor.hasNext()) {
				Document d = cursor.next();
				System.out.println(d.toJson());
			}

			//Consulta con filtro
			List<Document> consulta2 = col_Dino.find(and(eq("period", 1), lt("height", 3)))
					.into(new ArrayList<Document>());
			for (Document d : consulta2) {
				System.out.println(d.toJson());
			}
			
			//Consulta con filtro y limitaciones
			consulta2 = col_Dino.find(and(eq("period", 1), lt("height", 3)))
					.sort(descending("name")).limit(2).into(new ArrayList<Document>());
			for (Document d : consulta2) {
				System.out.println(d.toJson());
			}
			
			//Inserción
			Document d1 = new Document();
			d1.put("name", "Zepalosaurus");
			d1.put("period", 2);
			d1.append("height", 7).append("weight", 1100).append("length", 12);
			List<Integer> excavations = new ArrayList<Integer>(Arrays.asList(2,4));
			d1.append("excavations", excavations);
			col_Dino.insertOne(d1);
			
			//Actualización
			UpdateResult up = col_Dino.updateMany(eq("name","Zepalosaurus"),set("period", 3));
			System.out.println("Encontrados: " + up.getMatchedCount()
					+ " elementos y " + up.getModifiedCount() + " modificados");

			//Consultar todos los datos de una colección con un iterable
			cursor = col_Dino.find().iterator();
			while (cursor.hasNext()) {
				Document d = cursor.next();
				System.out.println(d.toJson());
			}
			
			//Borrado
			DeleteResult del = col_Dino.deleteOne(eq("name","Zepalosaurus"));
			System.out.println("Borrados: " + del.getDeletedCount());
			
			//Consulta con proyección
			Bson campos = Projections.fields(
	                    Projections.include("name", "period"),
	                    Projections.excludeId());
			
			
			List<Document> consulta3 = col_Dino.find(eq("period",2)).projection(campos).into(new ArrayList<Document>());
			for (Document d : consulta3) {
				System.out.println(d.toJson());
			}
			
			

		} catch (MongoException me) {
			System.err.println("An error occurred while attempting to run a command: " + me);
		}
	}

}
