package academico.util;

import academico.modelo.Usuario;

/** Mantiene el usuario autenticado durante la ejecucion de la aplicacion. */
public final class Sesion {

    private static Usuario usuarioActual;

    private Sesion() {
    }

    public static void iniciar(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static void cerrar() {
        usuarioActual = null;
    }

    public static boolean estaAutenticado() {
        return usuarioActual != null;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }
}
