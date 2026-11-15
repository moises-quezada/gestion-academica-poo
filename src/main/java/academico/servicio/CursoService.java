package academico.servicio;

import academico.dao.CursoDAO;
import academico.dao.MatriculaDAO;
import academico.excepcion.NegocioException;
import academico.modelo.Curso;
import academico.util.Validador;
import java.util.List;

/** Reglas de negocio para la gestion de cursos. */
public class CursoService {

    private final CursoDAO cursoDao = new CursoDAO();
    private final MatriculaDAO matriculaDao = new MatriculaDAO();

    public void registrar(String codigo, String nombre, String descripcion, String docente, String cupoTexto) {
        String cod = Validador.codigo(codigo);
        String nom = Validador.requerido(nombre, "nombre del curso");
        String doc = Validador.requerido(docente, "docente responsable");
        int cupo = Validador.enteroPositivo(cupoTexto, "cupo máximo");

        if (cursoDao.porCodigo(cod) != null) {
            throw new NegocioException("Ya existe un curso con el código " + cod + ".");
        }
        cursoDao.insertar(new Curso(cod, nom, descripcion == null ? "" : descripcion.trim(), doc, cupo));
    }

    public void modificar(String codigo, String nombre, String descripcion, String docente, String cupoTexto) {
        Curso existente = obtener(codigo);
        String nom = Validador.requerido(nombre, "nombre del curso");
        String doc = Validador.requerido(docente, "docente responsable");
        int cupo = Validador.enteroPositivo(cupoTexto, "cupo máximo");

        long activas = matriculaDao.contarActivasPorCurso(existente.getCodigo());
        if (cupo < activas) {
            throw new NegocioException("El cupo máximo no puede ser menor que los estudiantes ya matriculados (" + activas + ").");
        }
        existente.setNombre(nom);
        existente.setDescripcion(descripcion == null ? "" : descripcion.trim());
        existente.setDocente(doc);
        existente.setCupoMaximo(cupo);
        cursoDao.actualizar(existente);
    }

    public void eliminar(String codigo) {
        Curso existente = obtener(codigo);
        if (matriculaDao.contarPorCurso(existente.getCodigo()) > 0) {
            throw new NegocioException("No se puede eliminar el curso porque tiene matrículas asociadas.");
        }
        cursoDao.eliminar(existente.getCodigo());
    }

    public Curso obtener(String codigo) {
        Curso c = cursoDao.porCodigo(Validador.requerido(codigo, "codigo"));
        if (c == null) {
            throw new NegocioException("El curso no existe.");
        }
        return c;
    }

    public List<Curso> listar(String ordenarPor) {
        return cursoDao.listar(ordenarPor);
    }

    public List<Curso> buscar(String texto, String ordenarPor) {
        if (texto == null || texto.trim().isEmpty()) {
            return cursoDao.listar(ordenarPor);
        }
        return cursoDao.buscar(texto.trim(), ordenarPor);
    }

    public long matriculados(String codigoCurso) {
        return matriculaDao.contarActivasPorCurso(codigoCurso);
    }

    public long cuposDisponibles(Curso curso) {
        return curso.getCupoMaximo() - matriculados(curso.getCodigo());
    }
}
