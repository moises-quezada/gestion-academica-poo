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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/** Consultas y listados academicos (Proceso 4 de la Clave 3). */
public class ConsultasPanel extends JPanel {

    private final MatriculaService matriculaServicio = new MatriculaService();
    private final EstudianteService estudianteServicio = new EstudianteService();
    private final CursoService cursoServicio = new CursoService();

    // 4.1 Estudiantes por curso
    private final JComboBox<String> cmbCurso1 = new JComboBox<>();
    private final DefaultTableModel modeloPorCurso = UIUtil.modelo("Código", "Nombre", "Documento", "Correo");
    // 4.2 Cursos por estudiante
    private final JComboBox<String> cmbEstudiante = new JComboBox<>();
    private final DefaultTableModel modeloPorEstudiante = UIUtil.modelo("Código", "Nombre", "Docente", "Cupo máximo");
    // 4.6 Resumen
    private final DefaultTableModel modeloResumen = UIUtil.modelo("Código", "Curso", "Cupo máximo",
            "Matriculados", "Cupos disponibles");
    // 4.5 Listado general
    private final JComboBox<String> cmbListado = new JComboBox<>(new String[]{"Estudiantes", "Cursos", "Matrículas"});
    private final JComboBox<String> cmbOrdenListado = new JComboBox<>(new String[]{"Código", "Nombre"});
    private final JTable tablaListado = new JTable();

    public ConsultasPanel() {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Estudiantes por curso", panelPorCurso());
        pestanas.addTab("Cursos por estudiante", panelPorEstudiante());
        pestanas.addTab("Resumen de matrícula", panelResumen());
        pestanas.addTab("Listado general", panelListado());
        add(pestanas, BorderLayout.CENTER);
        refrescar();
    }

    private JPanel panelPorCurso() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        JButton btn = new JButton("Consultar");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Curso:"));
        top.add(cmbCurso1);
        top.add(btn);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(new JTable(modeloPorCurso)), BorderLayout.CENTER);
        btn.addActionListener(e -> UIUtil.ejecutar(this, () -> {
            modeloPorCurso.setRowCount(0);
            List<Estudiante> lista = matriculaServicio.estudiantesPorCurso(UIUtil.codigoDeItem(cmbCurso1.getSelectedItem()));
            for (Estudiante s : lista) {
                modeloPorCurso.addRow(new Object[]{s.getCodigo(), s.getNombre(), s.getDocumento(), s.getCorreo()});
            }
            if (lista.isEmpty()) {
                UIUtil.info(this, "La consulta no devolvió resultados.");
            }
        }));
        return p;
    }

    private JPanel panelPorEstudiante() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        JButton btn = new JButton("Consultar");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Estudiante:"));
        top.add(cmbEstudiante);
        top.add(btn);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(new JTable(modeloPorEstudiante)), BorderLayout.CENTER);
        btn.addActionListener(e -> UIUtil.ejecutar(this, () -> {
            modeloPorEstudiante.setRowCount(0);
            List<Curso> lista = matriculaServicio.cursosPorEstudiante(UIUtil.codigoDeItem(cmbEstudiante.getSelectedItem()));
            for (Curso c : lista) {
                modeloPorEstudiante.addRow(new Object[]{c.getCodigo(), c.getNombre(), c.getDocente(), c.getCupoMaximo()});
            }
            if (lista.isEmpty()) {
                UIUtil.info(this, "La consulta no devolvió resultados.");
            }
        }));
        return p;
    }

    private JPanel panelResumen() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        JButton btn = new JButton("Generar resumen");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(btn);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(new JTable(modeloResumen)), BorderLayout.CENTER);
        btn.addActionListener(e -> generarResumen());
        return p;
    }

    private JPanel panelListado() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        JButton btn = new JButton("Generar listado");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Listado de:"));
        top.add(cmbListado);
        top.add(new JLabel("Ordenar por:"));
        top.add(cmbOrdenListado);
        top.add(btn);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(tablaListado), BorderLayout.CENTER);
        btn.addActionListener(e -> generarListado());
        return p;
    }

    private void generarResumen() {
        UIUtil.ejecutar(this, () -> {
            modeloResumen.setRowCount(0);
            List<Object[]> filas = matriculaServicio.resumenMatricula();
            for (Object[] fila : filas) {
                modeloResumen.addRow(fila);
            }
            if (filas.isEmpty()) {
                UIUtil.info(this, "No hay cursos registrados.");
            }
        });
    }

    private void generarListado() {
        UIUtil.ejecutar(this, () -> {
            String orden = cmbOrdenListado.getSelectedIndex() == 0 ? "codigo" : "nombre";
            DefaultTableModel m;
            switch (cmbListado.getSelectedIndex()) {
                case 0 -> {
                    m = UIUtil.modelo("Código", "Nombre", "Documento", "Correo", "Estado");
                    for (Estudiante e : estudianteServicio.listar(orden)) {
                        m.addRow(new Object[]{e.getCodigo(), e.getNombre(), e.getDocumento(), e.getCorreo(),
                            e.isActivo() ? "Activo" : "Inactivo"});
                    }
                }
                case 1 -> {
                    m = UIUtil.modelo("Código", "Nombre", "Docente", "Cupo máximo", "Matriculados");
                    for (Curso c : cursoServicio.listar(orden)) {
                        m.addRow(new Object[]{c.getCodigo(), c.getNombre(), c.getDocente(), c.getCupoMaximo(),
                            cursoServicio.matriculados(c.getCodigo())});
                    }
                }
                default -> {
                    m = UIUtil.modelo("Código", "Estudiante", "Curso", "Fecha", "Estado");
                    for (Matricula x : matriculaServicio.listar()) {
                        m.addRow(new Object[]{x.getCodigo(), x.getCodigoEstudiante(), x.getCodigoCurso(),
                            x.getFecha(), x.getEstado()});
                    }
                }
            }
            tablaListado.setModel(m);
            if (m.getRowCount() == 0) {
                UIUtil.info(this, "La consulta no devolvió resultados.");
            }
        });
    }

    /** Recarga los combos con los datos actuales. */
    public void refrescar() {
        UIUtil.ejecutar(this, () -> {
            DefaultComboBoxModel<String> cursos = new DefaultComboBoxModel<>();
            for (Curso c : cursoServicio.listar("codigo")) {
                cursos.addElement(c.getCodigo() + " - " + c.getNombre());
            }
            DefaultComboBoxModel<String> estudiantes = new DefaultComboBoxModel<>();
            for (Estudiante e : estudianteServicio.listar("codigo")) {
                estudiantes.addElement(e.getCodigo() + " - " + e.getNombre());
            }
            cmbCurso1.setModel(cursos);
            cmbEstudiante.setModel(estudiantes);
        });
    }
}
