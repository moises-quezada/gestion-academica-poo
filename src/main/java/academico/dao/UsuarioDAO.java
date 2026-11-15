package academico.dao;

import academico.modelo.Usuario;
import academico.util.ConexionMongo;
import academico.util.PasswordUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

/** Persistencia de los usuarios del sistema. */
public class UsuarioDAO {

    private final MongoCollection<Document> coleccion = ConexionMongo.getBase().getCollection("usuarios");

    public Usuario buscarPorUsername(String username) {
        Document doc = coleccion.find(Filters.eq("username", username)).first();
        if (doc == null) {
            return null;
        }
        return new Usuario(doc.getString("username"), doc.getString("passwordHash"), doc.getString("rol"));
    }

    public void insertar(Usuario usuario) {
        coleccion.insertOne(new Document("username", usuario.getUsername())
                .append("passwordHash", usuario.getPasswordHash())
                .append("rol", usuario.getRol()));
    }

    /** Si no hay usuarios, crea "admin" / "Admin123*" para poder ingresar por primera vez. */
    public void crearAdminPorDefectoSiVacio() {
        if (coleccion.countDocuments() == 0) {
            insertar(new Usuario("admin", PasswordUtil.hash("Admin123*".toCharArray()), "ADMIN"));
        }
    }
}
