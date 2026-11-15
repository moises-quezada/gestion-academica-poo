package academico.dao;

import academico.modelo.Estudiante;
import academico.util.ConexionMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.bson.Document;
import org.bson.conversions.Bson;

/** Persistencia de estudiantes en la coleccion "estudiantes". */
public class EstudianteDAO {

    private final MongoCollection<Document> coleccion = ConexionMongo.getBase().getCollection("estudiantes");
    private final Collation collation = Collation.builder().locale("es").build();

    public void insertar(Estudiante e) {
        coleccion.insertOne(aDocumento(e));
    }

    public void actualizar(Estudiante e) {
        coleccion.replaceOne(Filters.eq("codigo", e.getCodigo()), aDocumento(e));
    }

    public void eliminar(String codigo) {
        coleccion.deleteOne(Filters.eq("codigo", codigo));
    }

    public Estudiante porCodigo(String codigo) {
        return aEstudiante(coleccion.find(Filters.eq("codigo", codigo)).first());
    }

    public Estudiante porDocumento(String documento) {
        return aEstudiante(coleccion.find(Filters.eq("documento", documento)).first());
    }

    /** @param ordenarPor "codigo" o "nombre" */
    public List<Estudiante> listar(String ordenarPor) {
        return consultar(new Document(), ordenarPor);
    }

    /** Busca por codigo, documento o nombre (coincidencia parcial, sin distinguir mayusculas). */
    public List<Estudiante> buscar(String texto, String ordenarPor) {
        String patron = Pattern.quote(texto);
        Bson filtro = Filters.or(
                Filters.regex("codigo", patron, "i"),
                Filters.regex("documento", patron, "i"),
                Filters.regex("nombre", patron, "i"));
        return consultar(filtro, ordenarPor);
    }

    private List<Estudiante> consultar(Bson filtro, String ordenarPor) {
        String campo = "nombre".equals(ordenarPor) ? "nombre" : "codigo";
        List<Estudiante> resultado = new ArrayList<>();
        for (Document doc : coleccion.find(filtro).collation(collation).sort(Sorts.ascending(campo))) {
            resultado.add(aEstudiante(doc));
        }
        return resultado;
    }

    private Document aDocumento(Estudiante e) {
        return new Document("codigo", e.getCodigo())
                .append("nombre", e.getNombre())
                .append("documento", e.getDocumento())
                .append("correo", e.getCorreo())
                .append("activo", e.isActivo());
    }

    private Estudiante aEstudiante(Document doc) {
        if (doc == null) {
            return null;
        }
        return new Estudiante(
                doc.getString("codigo"),
                doc.getString("nombre"),
                doc.getString("documento"),
                doc.getString("correo") == null ? "" : doc.getString("correo"),
                doc.getBoolean("activo", true));
    }
}
