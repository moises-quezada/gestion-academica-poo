package academico.dao;

import academico.modelo.Matricula;
import academico.util.ConexionMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.bson.Document;
import org.bson.conversions.Bson;

/** Persistencia de matriculas en la coleccion "matriculas". */
public class MatriculaDAO {

    private final MongoCollection<Document> coleccion = ConexionMongo.getBase().getCollection("matriculas");

    public void insertar(Matricula m) {
        coleccion.insertOne(new Document("codigo", m.getCodigo())
                .append("codigoEstudiante", m.getCodigoEstudiante())
                .append("codigoCurso", m.getCodigoCurso())
                .append("fecha", m.getFecha().toString())
                .append("estado", m.getEstado())
                .append("fechaRetiro", null));
    }

    public void retirar(String codigo, LocalDate fechaRetiro) {
        coleccion.updateOne(Filters.eq("codigo", codigo), Updates.combine(
                Updates.set("estado", Matricula.ESTADO_RETIRADA),
                Updates.set("fechaRetiro", fechaRetiro.toString())));
    }

    public Matricula porCodigo(String codigo) {
        return aMatricula(coleccion.find(Filters.eq("codigo", codigo)).first());
    }

    public boolean existeActiva(String codigoEstudiante, String codigoCurso) {
        return coleccion.countDocuments(Filters.and(
                Filters.eq("codigoEstudiante", codigoEstudiante),
                Filters.eq("codigoCurso", codigoCurso),
                Filters.eq("estado", Matricula.ESTADO_ACTIVA))) > 0;
    }

    public long contarActivasPorCurso(String codigoCurso) {
        return coleccion.countDocuments(Filters.and(
                Filters.eq("codigoCurso", codigoCurso),
                Filters.eq("estado", Matricula.ESTADO_ACTIVA)));
    }

    /** Cuenta todas las matriculas (activas o retiradas) de un estudiante. */
    public long contarPorEstudiante(String codigoEstudiante) {
        return coleccion.countDocuments(Filters.eq("codigoEstudiante", codigoEstudiante));
    }

    /** Cuenta todas las matriculas (activas o retiradas) de un curso. */
    public long contarPorCurso(String codigoCurso) {
        return coleccion.countDocuments(Filters.eq("codigoCurso", codigoCurso));
    }

    public List<Matricula> listar() {
        return consultar(new Document());
    }

    public List<Matricula> activasPorCurso(String codigoCurso) {
        return consultar(Filters.and(Filters.eq("codigoCurso", codigoCurso),
                Filters.eq("estado", Matricula.ESTADO_ACTIVA)));
    }

    public List<Matricula> activasPorEstudiante(String codigoEstudiante) {
        return consultar(Filters.and(Filters.eq("codigoEstudiante", codigoEstudiante),
                Filters.eq("estado", Matricula.ESTADO_ACTIVA)));
    }

    public List<Matricula> porEstudiante(String codigoEstudiante) {
        return consultar(Filters.eq("codigoEstudiante", codigoEstudiante));
    }

    /** Busca por codigo, estudiante, curso o estado; filtra ademas por estado si no es "TODOS". */
    public List<Matricula> buscar(String texto, String estado) {
        List<Bson> condiciones = new ArrayList<>();
        if (texto != null && !texto.isBlank()) {
            String patron = Pattern.quote(texto.trim());
            condiciones.add(Filters.or(
                    Filters.regex("codigo", patron, "i"),
                    Filters.regex("codigoEstudiante", patron, "i"),
                    Filters.regex("codigoCurso", patron, "i")));
        }
        if (estado != null && !"TODOS".equals(estado)) {
            condiciones.add(Filters.eq("estado", estado));
        }
        return consultar(condiciones.isEmpty() ? new Document() : Filters.and(condiciones));
    }

    /** Genera un codigo unico con el formato MAT-0001. */
    public String siguienteCodigo() {
        long numero = coleccion.countDocuments() + 1;
        String codigo = String.format("MAT-%04d", numero);
        while (coleccion.countDocuments(Filters.eq("codigo", codigo)) > 0) {
            numero++;
            codigo = String.format("MAT-%04d", numero);
        }
        return codigo;
    }

    private List<Matricula> consultar(Bson filtro) {
        List<Matricula> resultado = new ArrayList<>();
        for (Document doc : coleccion.find(filtro).sort(Sorts.ascending("codigo"))) {
            resultado.add(aMatricula(doc));
        }
        return resultado;
    }

    private Matricula aMatricula(Document doc) {
        if (doc == null) {
            return null;
        }
        Matricula m = new Matricula(
                doc.getString("codigo"),
                doc.getString("codigoEstudiante"),
                doc.getString("codigoCurso"),
                LocalDate.parse(doc.getString("fecha")),
                doc.getString("estado"));
        String retiro = doc.getString("fechaRetiro");
        if (retiro != null) {
            m.setFechaRetiro(LocalDate.parse(retiro));
        }
        return m;
    }
}
