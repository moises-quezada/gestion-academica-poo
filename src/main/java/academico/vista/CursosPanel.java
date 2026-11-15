package academico.vista;

import academico.modelo.Curso;
import academico.servicio.CursoService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/** Gestion de cursos: registrar, buscar, consultar, modificar y eliminar. */
public class CursosPanel extends JPanel {

    private final CursoService servicio = new CursoService();

    private final JTextField txtCodigo = new JTextField(15);
    private final JTextField txtNombre = new JTextField(25);
    private final JTextField txtDescripcion = new JTextField(25);
    private final JTextField txtDocente = new JTextField(25);
    private final JTextField txtCupo = new JTextField(8);

    private final JTextField txtBuscar = new JTextField(18);
    private final JComboBox<String> cmbOrden = new JComboBox<>(new String[]{"Código", "Nombre"});

    private final DefaultTableModel modelo = UIUtil.modelo("Código", "Nombre", "Descripción", "Docente",
            "Cupo máximo", "Matriculados", "Cupos disponibles");
    private final JTable tabla = new JTable(modelo);

    public CursosPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        construirInterfaz();
        refrescar();
    }

    private void construirInterfaz() {
        JPanel formulario = new JPanel(new java.awt.GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del curso"));
        UIUtil.fila(formulario, 0, "Código:", txtCodigo);
        UIUtil.fila(formulario, 1, "Nombre:", txtNombre);
        UIUtil.fila(formulario, 2, "Descripción:", txtDescripcion);
        UIUtil.fila(formulario, 3, "Docente responsable:", txtDocente);
        UIUtil.fila(formulario, 4, "Cupo máximo:", txtCupo);

        JButton btnRegistrar = new JButton("Registrar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnRegistrar);
        botones.add(btnModificar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(formulario, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        JButton btnBuscar = new JButton("Buscar");
        JButton btnTodos = new JButton("Mostrar todos");
        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busqueda.add(new JLabel("Buscar por código o nombre:"));
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
        btnEliminar.addActionListener(e -> eliminar());
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
            List<Curso> resultado = servicio.buscar(txtBuscar.getText(), ordenSeleccionado());
            llenarTabla(resultado);
            if (resultado.isEmpty()) {
                UIUtil.info(this, "La consulta no devolvió resultados.");
            }
        });
    }

    private void llenarTabla(List<Curso> cursos) {
        modelo.setRowCount(0);
        for (Curso c : cursos) {
            long matriculados = servicio.matriculados(c.getCodigo());
            modelo.addRow(new Object[]{c.getCodigo(), c.getNombre(), c.getDescripcion(), c.getDocente(),
                c.getCupoMaximo(), matriculados, c.getCupoMaximo() - matriculados});
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }
        txtCodigo.setText(modelo.getValueAt(fila, 0).toString());
        txtNombre.setText(modelo.getValueAt(fila, 1).toString());
        txtDescripcion.setText(modelo.getValueAt(fila, 2).toString());
        txtDocente.setText(modelo.getValueAt(fila, 3).toString());
        txtCupo.setText(modelo.getValueAt(fila, 4).toString());
        txtCodigo.setEditable(false);
    }

    private void limpiar() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtDocente.setText("");
        txtCupo.setText("");
        txtCodigo.setEditable(true);
        tabla.clearSelection();
    }

    private void registrar() {
        UIUtil.ejecutar(this, () -> {
            servicio.registrar(txtCodigo.getText(), txtNombre.getText(), txtDescripcion.getText(),
                    txtDocente.getText(), txtCupo.getText());
            UIUtil.info(this, "Curso registrado correctamente.");
            limpiar();
            refrescar();
        });
    }

    private void modificar() {
        UIUtil.ejecutar(this, () -> {
            servicio.modificar(txtCodigo.getText(), txtNombre.getText(), txtDescripcion.getText(),
                    txtDocente.getText(), txtCupo.getText());
            UIUtil.info(this, "Curso modificado correctamente.");
            limpiar();
            refrescar();
        });
    }

    private void eliminar() {
        UIUtil.ejecutar(this, () -> {
            Curso c = servicio.obtener(txtCodigo.getText());
            if (UIUtil.confirmar(this, "¿Eliminar definitivamente el curso " + c.getNombre() + "?")) {
                servicio.eliminar(c.getCodigo());
                UIUtil.info(this, "Curso eliminado correctamente.");
                limpiar();
                refrescar();
            }
        });
    }
}
