package academico.vista;

import academico.servicio.AuthService;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/** Pantalla de inicio de sesion (RF-01). */
public class LoginFrame extends JFrame {

    private final JTextField txtUsuario = new JTextField(18);
    private final JPasswordField txtPassword = new JPasswordField(18);
    private final JButton btnIngresar = new JButton("Iniciar sesión");

    public LoginFrame() {
        super("Gestión Académica - Inicio de sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        construirInterfaz();
        pack();
        setSize(400, getHeight() + 20);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel titulo = new JLabel("Sistema de Gestión Académica", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        raiz.add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        UIUtil.fila(formulario, 0, "Usuario:", txtUsuario);
        UIUtil.fila(formulario, 1, "Contraseña:", txtPassword);
        raiz.add(formulario, BorderLayout.CENTER);

        JPanel pie = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 0, 0, 0);
        pie.add(btnIngresar, c);
        raiz.add(pie, BorderLayout.SOUTH);

        setContentPane(raiz);
        getRootPane().setDefaultButton(btnIngresar);
        btnIngresar.addActionListener(e -> iniciarSesion());
    }

    private void iniciarSesion() {
        char[] password = txtPassword.getPassword();
        UIUtil.ejecutar(this, () -> {
            new AuthService().iniciarSesion(txtUsuario.getText(), password);
            new MenuPrincipalFrame().setVisible(true);
            dispose();
        });
        Arrays.fill(password, '\0'); // no dejar la contraseña en memoria
        txtPassword.setText("");
    }
}
