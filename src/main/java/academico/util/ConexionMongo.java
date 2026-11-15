package academico.util;

import academico.excepcion.NegocioException;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.bson.Document;

/** Administra la conexion unica a MongoDB (patron Singleton). */
public final class ConexionMongo {

    private static MongoClient cliente;
    private static MongoDatabase base;

    private ConexionMongo() {
    }

    public static synchronized MongoDatabase getBase() {
        if (base == null) {
            Properties config = cargarConfiguracion();
            String uri = config.getProperty("mongo.uri", "mongodb://localhost:27017");
            String nombreBase = config.getProperty("mongo.db", "gestion_academica");
            MongoClient nuevoCliente = null;
            try {
                MongoClientSettings ajustes = MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(uri))
                        .applyToClusterSettings(b -> b.serverSelectionTimeout(4, TimeUnit.SECONDS))
                        .build();
                nuevoCliente = MongoClients.create(ajustes);
                MongoDatabase db = nuevoCliente.getDatabase(nombreBase);
                db.runCommand(new Document("ping", 1)); // verifica que la conexion funcione
                crearIndices(db);
                cliente = nuevoCliente;
                base = db;
            } catch (MongoException | IllegalArgumentException e) {
                if (nuevoCliente != null) {
                    nuevoCliente.close();
                }
                throw new NegocioException("No se pudo conectar con MongoDB. Verifique que el servicio este iniciado "
                        + "y que config.properties sea correcto.\nDetalle: " + e.getMessage());
            }
        }
        return base;
    }

    public static synchronized void cerrar() {
        if (cliente != null) {
            cliente.close();
            cliente = null;
            base = null;
        }
    }

    private static Properties cargarConfiguracion() {
        Properties props = new Properties();
        try (InputStream in = ConexionMongo.class.getResourceAsStream("/config.properties")) {
            if (in == null) {
                throw new NegocioException("No se encontro el archivo config.properties.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new NegocioException("No se pudo leer config.properties: " + e.getMessage());
        }
        return props;
    }

    /** Garantiza la unicidad de los codigos a nivel de base de datos. */
    private static void crearIndices(MongoDatabase db) {
        IndexOptions unico = new IndexOptions().unique(true);
        db.getCollection("usuarios").createIndex(Indexes.ascending("username"), unico);
        db.getCollection("estudiantes").createIndex(Indexes.ascending("codigo"), unico);
        db.getCollection("estudiantes").createIndex(Indexes.ascending("documento"), unico);
        db.getCollection("cursos").createIndex(Indexes.ascending("codigo"), unico);
        db.getCollection("matriculas").createIndex(Indexes.ascending("codigo"), unico);
    }
}
