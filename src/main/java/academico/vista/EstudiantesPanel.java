package academico.vista;

import academico.modelo.Estudiante;
import academico.modelo.Matricula;
import academico.servicio.EstudianteService;
import academico.servicio.MatriculaService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/** Gestion de estudiantes: registrar, buscar, consultar, modificar, inactivar y eliminar. */
public class EstudiantesPanel extends JPanel {

    private final EstudianteService servicio = new EstudianteService();
    private final MatriculaService matriculaServicio = new MatriculaService();

    private final JTextField txtCodigo = new JTextField(15);
    private final JTextField txtNombre = new JTextField(25);
    private final JTextField txtDocumento = new JTextField(15);
    private final JTextField txtCorreo = new JTextField(25);

    private final JTextField txtBuscar = new JTextField(18);
    private final JComboBox<String> cmbOrden = new JComboBox<>(new String[]{"Código", "Nombre"});

    private final DefaultTableModel modelo = UIUtil.modelo("Código", "Nombre", "Documento", "Correo", "Estado");
    private final JTable tabla = new JTable(modelo);

    public EstudiantesPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        construirInterfaz();
        refrescar();
    }

    private void construirInterfaz() {
        JPanel formulario = new JPanel(new java.awt.GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del estudiante"));
        UIUtil.fila(formulario, 0, "Código:", txtCodigo);
        UIUtil.fila(formulario, 1, "Nombre completo:", txtNombre);
        UIUtil.fila(formulario, 2, "Documento:", txtDocumento);
        UIUtil.fila(formulario, 3, "Correo (opcional):", txtCorreo);

        JButton btnRegistrar = new JButton("Registrar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEstado = new JButton("Activar / Inactivar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnInfo = new JButton("Ver información académica");
        JButton btnLimpiar = new JButton("Limpiar");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnRegistrar);
        botones.add(btnModificar);
        botones.add(btnEstado);
        botones.add(btnEliminar);
        botones.add(btnInfo);
        botones.add(btnLimpiar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(formulario, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        JButton btnBuscar = new JButton("Buscar");
        JButton btnTodos = new JButton("Mostrar todos");
        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busqueda.add(new JLabel("Buscar por código, documento o nombre:"));
        busqueda.add(txtBuscar);
        busqueda.add(btnBuscar);
        busqueda.add(btnTodos);
        busqueda.add(new JLabel("Ordenar por:"));
        busqueda.add(cmbOrden);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel centro = new JPanel(new BorderLayout());
        centro.add(busqueda, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccion();
            }
        });
        btnRegistrar.addActionListener(e -> registrar());
        btnModificar.addActionListener(e -> modificar());
        btnEstado.addActionListener(e -> cambiarEstado());
        btnEliminar.addActionListener(e -> eliminar());
        btnInfo.addActionListener(e -> verInformacion());
        btnLimpiar.addActionListener(e -> limpiar());
        btnBuscar.addActionListener(e -> buscar());
        btnTodos.addActionListener(e -> {
            txtBuscar.setText("");
            refrescar();
        });
        cmbOrden.addActionListener(e -> buscar());
    }

    private String ordenSeleccionado() {
        return cmbOrden.getSelectedIndex() == 0 ? "codigo" : "nombre";
    }

    public void refrescar() {
        UIUtil.ejecutar(this, () -> llenarTabla(servicio.buscar(txtBuscar.getText(), ordenSeleccionado())));
    }

    private void buscar() {
        UIUtil.ejecutar(this, () -> {
            List<Estudiante> resultado = servicio.buscar(txtBuscar.getText(), ordenSeleccionado());
            llenarTabla(resultado);
            if (resultado.isEmpty()) {
                UIUtil.info(this, "La consulta no devolvió resultados.");
            }
        });
    }

    private void llenarTabla(List<Estudiante> estudiantes) {
        modelo.setRowCount(0);
        for (Estudiante e : estudiantes) {
            modelo.addRow(new Object[]{e.getCodigo(), e.getNombre(), e.getDocumento(), e.getCorreo(),
                e.isActivo() ? "Activo" : "Inactivo"});
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        txtCodigo.setText(modelo.getValueAt(fila, 0).toString());
        txtNombre.setText(modelo.getValueAt(fila, 1).toString());
        txtDocumento.setText(modelo.getValueAt(fila, 2).toString());
        txtCorreo.setText(modelo.getValueAt(fila, 3).toString());
        txtCodigo.setEditable(false); // el código no se modifica
    }

    private void limpiar() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDocumento.setText("");
        txtCorreo.setText("");
        txtCodigo.setEditable(true);
        tabla.clearSelection();
    }

    private void registrar() {
        UIUtil.ejecutar(this, () -> {
            servicio.registrar(txtCodigo.getText(), txtNombre.getText(), txtDocumento.getText(), txtCorreo.getText());
            UIUtil.info(this, "Estudiante registrado correctamente.");
            limpiar();
            refrescar();
        });
    }

    private void modificar() {
        UIUtil.ejecutar(this, () -> {
            servicio.modificar(txtCodigo.getText(), txtNombre.getText(), txtDocumento.getText(), txtCorreo.getText());
            UIUtil.info(this, "Estudiante modificado correctamente.");
            limpiar();
            refrescar();
        });
    }

    private void cambiarEstado() {
        UIUtil.ejecutar(this, () -> {
            Estudiante e = servicio.obtener(txtCodigo.getText());
            boolean nuevoEstado = !e.isActivo();
            if (UIUtil.confirmar(this, "¿Desea " + (nuevoEstado ? "activar" : "inactivar") + " al estudiante " + e.getNombre() + "?")) {
                servicio.cambiarEstado(e.getCodigo(), nuevoEstado);
                UIUtil.info(this, "Estado actualizado correctamente.");
                limpiar();
                refrescar();
            }
        });
    }

    private void eliminar() {
        UIUtil.ejecutar(this, () -> {
            Estudiante e = servicio.obtener(txtCodigo.getText());
            if (UIUtil.confirmar(this, "¿Eliminar definitivamente al estudiante " + e.getNombre() + "?")) {
                servicio.eliminar(e.getCodigo());
                UIUtil.info(this, "Estudiante eliminado correctamente.");
                limpiar();
                refrescar();
            }
        });
    }

    /** Proceso 1.6: datos generales del estudiante y sus matrículas asociadas. */
    private void verInformacion() {
        UIUtil.ejecutar(this, () -> {
            Estudiante e = servicio.obtener(txtCodigo.getText());
            StringBuilder sb = new StringBuilder();
            sb.append("Código: ").append(e.getCodigo()).append('\n');
            sb.append("Nombre: ").append(e.getNombre()).append('\n');
            sb.append("Documento: ").append(e.getDocumento()).append('\n');
            sb.append("Correo: ").append(e.getCorreo().isEmpty() ? "(no registrado)" : e.getCorreo()).append('\n');
            sb.append("Estado: ").append(e.isActivo() ? "Activo" : "Inactivo").append("\n\nMatrículas:\n");
            List<Matricula> matriculas = matriculaServicio.matriculasDeEstudiante(e.getCodigo());
            if (matriculas.isEmpty()) {
                sb.append("  (sin matrículas)");
            }
            for (Matricula m : matriculas) {
                sb.append("  ").append(m.getCodigo()).append(" | curso ").append(m.getCodigoCurso())
                        .append(" | ").append(m.getFecha()).append(" | ").append(m.getEstado()).append('\n');
            }
            JTextArea area = new JTextArea(sb.toString(), 14, 40);
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Información académica",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
