package academico.util;

import academico.excepcion.NegocioException;
import java.util.regex.Pattern;

/** Validaciones reutilizables para los datos ingresados por el usuario. */
public final class Validador {

    private static final Pattern CODIGO = Pattern.compile("^[A-Za-z0-9_-]{2,15}$");
    private static final Pattern DOCUMENTO = Pattern.compile("^[A-Za-z0-9-]{5,20}$");
    private static final Pattern CORREO = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[A-Za-z]{2,}$");

    private Validador() {
    }

    /** Verifica que el texto no sea nulo, vacio ni solo espacios. Devuelve el texto sin espacios sobrantes. */
    public static String requerido(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new NegocioException("El campo \"" + campo + "\" es obligatorio y no puede estar vacio.");
        }
        return valor.trim();
    }

    public static String codigo(String valor) {
        String limpio = requerido(valor, "codigo");
        if (!CODIGO.matcher(limpio).matches()) {
            throw new NegocioException("El codigo debe tener de 2 a 15 caracteres (letras, numeros, guion o guion bajo), sin espacios.");
        }
        return limpio;
    }

    public static String documento(String valor) {
        String limpio = requerido(valor, "documento de identificacion");
        if (!DOCUMENTO.matcher(limpio).matches()) {
            throw new NegocioException("El documento debe tener de 5 a 20 caracteres (letras, numeros o guiones). Ejemplo: 01234567-8");
        }
        return limpio;
    }

    /** El correo es opcional; si se escribe, debe tener formato valido. Devuelve "" si viene vacio. */
    public static String correoOpcional(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "";
        }
        String limpio = valor.trim();
        if (!CORREO.matcher(limpio).matches()) {
            throw new NegocioException("El correo electronico no tiene un formato valido. Ejemplo: nombre@dominio.com");
        }
        return limpio;
    }

    public static int enteroPositivo(String valor, String campo) {
        String limpio = requerido(valor, campo);
        int numero;
        try {
            numero = Integer.parseInt(limpio);
        } catch (NumberFormatException e) {
            throw new NegocioException("El campo \"" + campo + "\" debe ser un numero entero valido (sin decimales ni letras).");
        }
        if (numero <= 0) {
            throw new NegocioException("El campo \"" + campo + "\" debe ser un numero entero mayor que cero.");
        }
        return numero;
    }
}
