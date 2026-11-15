package academico.vista;

import academico.modelo.Curso;
import academico.modelo.Estudiante;
import academico.modelo.Matricula;
import academico.servicio.CursoService;
import academico.servicio.EstudianteService;
import academico.servicio.MatriculaService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/** Gestion de matriculas: inscribir estudiantes en cursos y registrar retiros. */
public class MatriculasPanel extends JPanel {

    private final MatriculaService servicio = new MatriculaService();
    private final EstudianteService estudianteServicio = new EstudianteService();
    private final CursoService cursoServicio = new CursoService();

    private final JComboBox<String> cmbEstudiante = new JComboBox<>();
    private final JComboBox<String> cmbCurso = new JComboBox<>();
    private final JLabel lblCupos = new JLabel(" ");

    private final JTextField txtBuscar = new JTextField(15);
    private final JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"TODOS", "ACTIVA", "RETIRADA"});

    private final DefaultTableModel modelo = UIUtil.modelo("Código", "Estudiante", "Curso", "Fecha",
            "Estado", "Fecha de retiro");
    private final JTable tabla = new JTable(modelo);

    public MatriculasPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        construirInterfaz();
        refrescar();
    }

    private void construirInterfaz() {
        JPanel formulario = new JPanel(new java.awt.GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Nueva matrícula"));
        UIUtil.fila(formulario, 0, "Estudiante (activos):", cmbEstudiante);
        UIUtil.fila(formulario, 1, "Curso:", cmbCurso);
        UIUtil.fila(formulario, 2, "Disponibilidad:", lblCupos);

        JButton btnMatricular = new JButton("Matricular");
        JButton btnRetirar = new JButton("Retirar matrícula seleccionada");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnMatricular);
        botones.add(btnRetirar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(formulario, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        JButton btnBuscar = new JButton("Buscar");
        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busqueda.add(new JLabel("Buscar por código, estudiante o curso:"));
        busqueda.add(txtBuscar);
        busqueda.add(new JLabel("Estado:"));
        busqueda.add(cmbEstado);
        busqueda.add(btnBuscar);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel centro = new JPanel(new BorderLayout());
        centro.add(busqueda, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        cmbCurso.addActionListener(e -> actualizarCupos());
        btnMatricular.addActionListener(e -> matricular());
        btnRetirar.addActionListener(e -> retirar());
        btnBuscar.addActionListener(e -> buscar());
        cmbEstado.addActionListener(e -> buscar());
    }

    /** Recarga combos y tabla con los datos actuales de MongoDB. */
    public void refrescar() {
        UIUtil.ejecutar(this, () -> {
            DefaultComboBoxModel<String> estudiantes = new DefaultComboBoxModel<>();
            for (Estudiante e : estudianteServicio.listar("codigo")) {
                if (e.isActivo()) {
                    estudiantes.addElement(e.getCodigo() + " - " + e.getNombre());
                }
            }
            DefaultComboBoxModel<String> cursos = new DefaultComboBoxModel<>();
            for (Curso c : cursoServicio.listar("codigo")) {
                cursos.addElement(c.getCodigo() + " - " + c.getNombre());
            }
            cmbEstudiante.setModel(estudiantes);
            cmbCurso.setModel(cursos);
            actualizarCupos();
            llenarTabla(servicio.buscar(txtBuscar.getText(), (String) cmbEstado.getSelectedItem()));
        });
    }

    private void actualizarCupos() {
        String codigo = UIUtil.codigoDeItem(cmbCurso.getSelectedItem());
        if (codigo == null) {
            lblCupos.setText("No hay cursos registrados");
            return;
        }
        UIUtil.ejecutar(this, () -> {
            Curso curso = cursoServicio.obtener(codigo);
            long disponibles = cursoServicio.cuposDisponibles(curso);
            lblCupos.setText(disponibles + " cupos disponibles de " + curso.getCupoMaximo());
        });
    }

    private void buscar() {
        UIUtil.ejecutar(this, () -> {
            List<Matricula> resultado = servicio.buscar(txtBuscar.getText(), (String) cmbEstado.getSelectedItem());
            llenarTabla(resultado);
            if (resultado.isEmpty()) {
                UIUtil.info(this, "La consulta no devolvió resultados.");
            }
        });
    }

    private void llenarTabla(List<Matricula> matriculas) {
        modelo.setRowCount(0);
        for (Matricula m : matriculas) {
            modelo.addRow(new Object[]{m.getCodigo(), m.getCodigoEstudiante(), m.getCodigoCurso(), m.getFecha(),
                m.getEstado(), m.getFechaRetiro() == null ? "" : m.getFechaRetiro()});
        }
    }

    private void matricular() {
        UIUtil.ejecutar(this, () -> {
            Matricula m = servicio.matricular(UIUtil.codigoDeItem(cmbEstudiante.getSelectedItem()),
                    UIUtil.codigoDeItem(cmbCurso.getSelectedItem()));
            UIUtil.info(this, "Matrícula registrada correctamente. Código: " + m.getCodigo());
            refrescar();
        });
    }

    private void retirar() {
        UIUtil.ejecutar(this, () -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                UIUtil.info(this, "Seleccione en la tabla la matrícula que desea retirar.");
                return;
            }
            String codigo = modelo.getValueAt(fila, 0).toString();
            if (UIUtil.confirmar(this, "¿Retirar la matrícula " + codigo + "?")) {
                servicio.retirar(codigo);
                UIUtil.info(this, "Matrícula retirada correctamente.");
                refrescar();
            }
        });
    }
}
