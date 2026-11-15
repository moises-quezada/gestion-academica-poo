package academico.dao;

import academico.modelo.Curso;
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

/** Persistencia de cursos en la coleccion "cursos". */
public class CursoDAO {

    private final MongoCollection<Document> coleccion = ConexionMongo.getBase().getCollection("cursos");
    private final Collation collation = Collation.builder().locale("es").build();

    public void insertar(Curso c) {
        coleccion.insertOne(aDocumento(c));
    }

    public void actualizar(Curso c) {
        coleccion.replaceOne(Filters.eq("codigo", c.getCodigo()), aDocumento(c));
    }

    public void eliminar(String codigo) {
        coleccion.deleteOne(Filters.eq("codigo", codigo));
    }

    public Curso porCodigo(String codigo) {
        return aCurso(coleccion.find(Filters.eq("codigo", codigo)).first());
    }

    /** @param ordenarPor "codigo" o "nombre" */
    public List<Curso> listar(String ordenarPor) {
        return consultar(new Document(), ordenarPor);
    }

    /** Busca por codigo o nombre (coincidencia parcial, sin distinguir mayusculas). */
    public List<Curso> buscar(String texto, String ordenarPor) {
        String patron = Pattern.quote(texto);
        Bson filtro = Filters.or(
                Filters.regex("codigo", patron, "i"),
                Filters.regex("nombre", patron, "i"));
        return consultar(filtro, ordenarPor);
    }

    private List<Curso> consultar(Bson filtro, String ordenarPor) {
        String campo = "nombre".equals(ordenarPor) ? "nombre" : "codigo";
        List<Curso> resultado = new ArrayList<>();
        for (Document doc : coleccion.find(filtro).collation(collation).sort(Sorts.ascending(campo))) {
            resultado.add(aCurso(doc));
        }
        return resultado;
    }

    private Document aDocumento(Curso c) {
        return new Document("codigo", c.getCodigo())
                .append("nombre", c.getNombre())
                .append("descripcion", c.getDescripcion())
                .append("docente", c.getDocente())
                .append("cupoMaximo", c.getCupoMaximo());
    }

    private Curso aCurso(Document doc) {
        if (doc == null) {
            return null;
        }
        return new Curso(
                doc.getString("codigo"),
                doc.getString("nombre"),
                doc.getString("descripcion") == null ? "" : doc.getString("descripcion"),
                doc.getString("docente"),
                doc.getInteger("cupoMaximo", 0));
    }
}
