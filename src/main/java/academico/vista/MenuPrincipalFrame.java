package academico.vista;

import academico.servicio.AuthService;
import academico.util.Sesion;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/** Ventana principal: solo es accesible con una sesion iniciada (RF-06). */
public class MenuPrincipalFrame extends JFrame {

    private final EstudiantesPanel estudiantesPanel = new EstudiantesPanel();
    private final CursosPanel cursosPanel = new CursosPanel();
    private final MatriculasPanel matriculasPanel = new MatriculasPanel();
    private final ConsultasPanel consultasPanel = new ConsultasPanel();

    public MenuPrincipalFrame() {
        super("Gestión Académica");
        if (!Sesion.estaAutenticado()) {
            throw new IllegalStateException("Debe iniciar sesión para acceder al sistema.");
        }
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        construirInterfaz();
        setMinimumSize(new Dimension(950, 600));
        setSize(1050, 650);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        barra.add(new JLabel("Usuario: " + Sesion.getUsuarioActual().getUsername()
                + "  (" + Sesion.getUsuarioActual().getRol() + ")"), BorderLayout.WEST);
        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.addActionListener(e -> cerrarSesion());
        barra.add(btnSalir, BorderLayout.EAST);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Estudiantes", estudiantesPanel);
        pestanas.addTab("Cursos", cursosPanel);
        pestanas.addTab("Matrículas", matriculasPanel);
        pestanas.addTab("Consultas y listados", consultasPanel);
        // Al cambiar de pestaña se recargan los datos para reflejar cambios hechos en otras
        pestanas.addChangeListener(e -> {
            Object seleccionada = pestanas.getSelectedComponent();
            if (seleccionada == estudiantesPanel) {
                estudiantesPanel.refrescar();
            } else if (seleccionada == cursosPanel) {
                cursosPanel.refrescar();
            } else if (seleccionada == matriculasPanel) {
                matriculasPanel.refrescar();
            } else if (seleccionada == consultasPanel) {
                consultasPanel.refrescar();
            }
        });

        add(barra, BorderLayout.NORTH);
        add(pestanas, BorderLayout.CENTER);
    }

    private void cerrarSesion() {
        if (UIUtil.confirmar(this, "¿Desea cerrar la sesión?")) {
            new AuthService().cerrarSesion();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
