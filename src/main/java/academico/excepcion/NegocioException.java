package academico.excepcion;

/**
 * Excepcion para errores de validacion o de reglas de negocio.
 * Su mensaje esta pensado para mostrarse directamente al usuario.
 */
public class NegocioException extends RuntimeException {

    public NegocioException(String mensaje) {
        super(mensaje);
    }
}
