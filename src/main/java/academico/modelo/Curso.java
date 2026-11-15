package academico.modelo;

/** Representa un curso ofrecido por la institucion. */
public class Curso {

    private String codigo;
    private String nombre;
    private String descripcion;
    private String docente;
    private int cupoMaximo;

    public Curso() {
    }

    public Curso(String codigo, String nombre, String descripcion, String docente, int cupoMaximo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.docente = docente;
        this.cupoMaximo = cupoMaximo;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getDocente() { return docente; }
    public void setDocente(String docente) { this.docente = docente; }

    public int getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(int cupoMaximo) { this.cupoMaximo = cupoMaximo; }
}
