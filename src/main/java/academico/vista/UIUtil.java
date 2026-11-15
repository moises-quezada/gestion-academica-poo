package academico.vista;

import academico.excepcion.NegocioException;
import com.mongodb.MongoException;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/** Utilidades compartidas por las pantallas Swing. */
public final class UIUtil {

    private UIUtil() {
    }

    /** Ejecuta una accion y muestra con JOptionPane cualquier error de validacion o de MongoDB. */
    public static void ejecutar(Component padre, Runnable accion) {
        try {
            accion.run();
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(padre, e.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (MongoException e) {
            JOptionPane.showMessageDialog(padre,
                    "Error de conexión, lectura o escritura en MongoDB.\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(padre, "Ocurrió un error inesperado: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmar(Component padre, String mensaje) {
        return JOptionPane.showConfirmDialog(padre, mensaje, "Confirmación",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /** Modelo de tabla no editable. */
    public static DefaultTableModel modelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    /** Agrega una fila "etiqueta + componente" a un panel con GridBagLayout. */
    public static void fila(JPanel panel, int fila, String etiqueta, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.gridy = fila;
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(etiqueta), c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, c);
    }

    /** Extrae el codigo de un elemento de combo con formato "COD - Nombre". */
    public static String codigoDeItem(Object item) {
        if (item == null) {
            return null;
        }
        String texto = item.toString();
        int pos = texto.indexOf(" - ");
        return pos < 0 ? texto : texto.substring(0, pos);
    }
}
