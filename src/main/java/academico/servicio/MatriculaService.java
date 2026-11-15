package academico.servicio;

import academico.dao.CursoDAO;
import academico.dao.EstudianteDAO;
import academico.dao.MatriculaDAO;
import academico.excepcion.NegocioException;
import academico.modelo.Curso;
import academico.modelo.Estudiante;
import academico.modelo.Matricula;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Reglas de negocio para matriculas, retiros y consultas academicas. */
public class MatriculaService {

    private final MatriculaDAO matriculaDao = new MatriculaDAO();
    private final EstudianteDAO estudianteDao = new EstudianteDAO();
    private final CursoDAO cursoDao = new CursoDAO();

    /** Valida y registra una matricula nueva. */
    public Matricula matricular(String codigoEstudiante, String codigoCurso) {
        if (codigoEstudiante == null || codigoEstudiante.isBlank() || codigoCurso == null || codigoCurso.isBlank()) {
            throw new NegocioException("Seleccione un estudiante y un curso.");
        }
        Estudiante estudiante = estudianteDao.porCodigo(codigoEstudiante);
        if (estudiante == null) {
            throw new NegocioException("El estudiante no existe.");
        }
        Curso curso = cursoDao.porCodigo(codigoCurso);
        if (curso == null) {
            throw new NegocioException("El curso no existe.");
        }
        if (!estudiante.isActivo()) {
            throw new NegocioException("El estudiante está inactivo y no puede matricularse.");
        }
        if (matriculaDao.existeActiva(codigoEstudiante, codigoCurso)) {
            throw new NegocioException("El estudiante ya tiene una matrícula activa en este curso.");
        }
        long activas = matriculaDao.contarActivasPorCurso(codigoCurso);
        if (activas > curso.getCupoMaximo()) {
            throw new NegocioException("Inconsistencia: el curso " + codigoCurso + " tiene más matrículas ("
                    + activas + ") que su cupo máximo (" + curso.getCupoMaximo() + ").");
        }
        if (activas >= curso.getCupoMaximo()) {
            throw new NegocioException("El curso no tiene cupos disponibles.");
        }
        Matricula matricula = new Matricula(matriculaDao.siguienteCodigo(), codigoEstudiante, codigoCurso,
                LocalDate.now(), Matricula.ESTADO_ACTIVA);
        matriculaDao.insertar(matricula);
        return matricula;
    }

    /** Retira una matricula activa (el registro se conserva con estado RETIRADA). */
    public void retirar(String codigoMatricula) {
        if (codigoMatricula == null || codigoMatricula.isBlank()) {
            throw new NegocioException("Seleccione la matrícula que desea retirar.");
        }
        Matricula matricula = matriculaDao.porCodigo(codigoMatricula);
        if (matricula == null) {
            throw new NegocioException("La matrícula no existe.");
        }
        if (!matricula.estaActiva()) {
            throw new NegocioException("La matrícula ya fue retirada anteriormente.");
        }
        matriculaDao.retirar(codigoMatricula, LocalDate.now());
    }

    public List<Matricula> buscar(String texto, String estado) {
        return matriculaDao.buscar(texto, estado);
    }

    public List<Matricula> listar() {
        return matriculaDao.listar();
    }

    public List<Matricula> matriculasDeEstudiante(String codigoEstudiante) {
        return matriculaDao.porEstudiante(codigoEstudiante);
    }

    /** Estudiantes con matricula activa en el curso indicado. */
    public List<Estudiante> estudiantesPorCurso(String codigoCurso) {
        List<Estudiante> resultado = new ArrayList<>();
        for (Matricula m : matriculaDao.activasPorCurso(codigoCurso)) {
            Estudiante e = estudianteDao.porCodigo(m.getCodigoEstudiante());
            if (e != null) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    /** Cursos en los que el estudiante tiene matricula activa. */
    public List<Curso> cursosPorEstudiante(String codigoEstudiante) {
        List<Curso> resultado = new ArrayList<>();
        for (Matricula m : matriculaDao.activasPorEstudiante(codigoEstudiante)) {
            Curso c = cursoDao.porCodigo(m.getCodigoCurso());
            if (c != null) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    /** Filas: codigo, nombre, cupo maximo, matriculados, cupos disponibles. */
    public List<Object[]> resumenMatricula() {
        List<Object[]> filas = new ArrayList<>();
        for (Curso c : cursoDao.listar("codigo")) {
            long activas = matriculaDao.contarActivasPorCurso(c.getCodigo());
            filas.add(new Object[]{c.getCodigo(), c.getNombre(), c.getCupoMaximo(), activas,
                c.getCupoMaximo() - activas});
        }
        return filas;
    }
}
