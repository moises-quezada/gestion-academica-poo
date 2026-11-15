package academico.servicio;

import academico.dao.EstudianteDAO;
import academico.dao.MatriculaDAO;
import academico.excepcion.NegocioException;
import academico.modelo.Estudiante;
import academico.util.Validador;
import java.util.List;

/** Reglas de negocio para la gestion de estudiantes. */
public class EstudianteService {

    private final EstudianteDAO estudianteDao = new EstudianteDAO();
    private final MatriculaDAO matriculaDao = new MatriculaDAO();

    public void registrar(String codigo, String nombre, String documento, String correo) {
        String cod = Validador.codigo(codigo);
        String nom = Validador.requerido(nombre, "nombre completo");
        String doc = Validador.documento(documento);
        String cor = Validador.correoOpcional(correo);

        if (estudianteDao.porCodigo(cod) != null) {
            throw new NegocioException("Ya existe un estudiante con el código " + cod + ".");
        }
        if (estudianteDao.porDocumento(doc) != null) {
            throw new NegocioException("Ya existe un estudiante con el documento " + doc + ".");
        }
        estudianteDao.insertar(new Estudiante(cod, nom, doc, cor, true));
    }

    public void modificar(String codigo, String nombre, String documento, String correo) {
        Estudiante existente = obtener(codigo);
        String nom = Validador.requerido(nombre, "nombre completo");
        String doc = Validador.documento(documento);
        String cor = Validador.correoOpcional(correo);

        Estudiante otro = estudianteDao.porDocumento(doc);
        if (otro != null && !otro.getCodigo().equals(existente.getCodigo())) {
            throw new NegocioException("El documento " + doc + " ya pertenece a otro estudiante.");
        }
        existente.setNombre(nom);
        existente.setDocumento(doc);
        existente.setCorreo(cor);
        estudianteDao.actualizar(existente);
    }

    public void eliminar(String codigo) {
        Estudiante existente = obtener(codigo);
        if (matriculaDao.contarPorEstudiante(existente.getCodigo()) > 0) {
            throw new NegocioException("No se puede eliminar al estudiante porque tiene matrículas asociadas.\n"
                    + "Puede inactivarlo para conservar su historial.");
        }
        estudianteDao.eliminar(existente.getCodigo());
    }

    public void cambiarEstado(String codigo, boolean activo) {
        Estudiante existente = obtener(codigo);
        existente.setActivo(activo);
        estudianteDao.actualizar(existente);
    }

    public Estudiante obtener(String codigo) {
        Estudiante e = estudianteDao.porCodigo(Validador.requerido(codigo, "codigo"));
        if (e == null) {
            throw new NegocioException("El estudiante no existe.");
        }
        return e;
    }

    public List<Estudiante> listar(String ordenarPor) {
        return estudianteDao.listar(ordenarPor);
    }

    public List<Estudiante> buscar(String texto, String ordenarPor) {
        if (texto == null || texto.trim().isEmpty()) {
            return estudianteDao.listar(ordenarPor);
        }
        return estudianteDao.buscar(texto.trim(), ordenarPor);
    }
}
