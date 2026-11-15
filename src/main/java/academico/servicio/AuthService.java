package academico.servicio;

import academico.dao.UsuarioDAO;
import academico.excepcion.NegocioException;
import academico.modelo.Usuario;
import academico.util.PasswordUtil;
import academico.util.Sesion;

/** Logica de inicio y cierre de sesion. */
public class AuthService {

    private final UsuarioDAO usuarioDao = new UsuarioDAO();

    public AuthService() {
        usuarioDao.crearAdminPorDefectoSiVacio();
    }

    public void iniciarSesion(String username, char[] password) {
        if (username == null || username.trim().isEmpty()) {
            throw new NegocioException("Ingrese su nombre de usuario.");
        }
        if (password == null || password.length == 0) {
            throw new NegocioException("Ingrese su contraseña.");
        }
        Usuario usuario = usuarioDao.buscarPorUsername(username.trim());
        // Mismo mensaje para usuario inexistente o clave incorrecta: no se revela cual fallo.
        if (usuario == null || !PasswordUtil.verificar(password, usuario.getPasswordHash())) {
            throw new NegocioException("Usuario o contraseña incorrectos.");
        }
        Sesion.iniciar(usuario);
    }

    public void cerrarSesion() {
        Sesion.cerrar();
    }
}
