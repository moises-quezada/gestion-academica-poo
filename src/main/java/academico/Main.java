package academico;

import academico.vista.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Punto de entrada de la aplicacion: abre la pantalla de inicio de sesion. */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, se usa el aspecto por defecto de Swing
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
