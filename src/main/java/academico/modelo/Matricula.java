package academico.modelo;

import java.time.LocalDate;

/** Representa la inscripcion de un estudiante en un curso. */
public class Matricula {

    public static final String ESTADO_ACTIVA = "ACTIVA";
    public static final String ESTADO_RETIRADA = "RETIRADA";

    private String codigo;
    private String codigoEstudiante;
    private String codigoCurso;
    private LocalDate fecha;
    private String estado;
    private LocalDate fechaRetiro;

    public Matricula() {
    }

    public Matricula(String codigo, String codigoEstudiante, String codigoCurso, LocalDate fecha, String estado) {
        this.codigo = codigo;
        this.codigoEstudiante = codigoEstudiante;
        this.codigoCurso = codigoCurso;
        this.fecha = fecha;
        this.estado = estado;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getCodigoEstudiante() { return codigoEstudiante; }
    public void setCodigoEstudiante(String codigoEstudiante) { this.codigoEstudiante = codigoEstudiante; }

    public String getCodigoCurso() { return codigoCurso; }
    public void setCodigoCurso(String codigoCurso) { this.codigoCurso = codigoCurso; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDate getFechaRetiro() { return fechaRetiro; }
    public void setFechaRetiro(LocalDate fechaRetiro) { this.fechaRetiro = fechaRetiro; }

    public boolean estaActiva() {
        return ESTADO_ACTIVA.equals(estado);
    }
}
