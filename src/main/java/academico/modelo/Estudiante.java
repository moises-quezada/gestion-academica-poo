package academico.modelo;

/** Representa a un estudiante de la institucion. */
public class Estudiante {

    private String codigo;
    private String nombre;
    private String documento;
    private String correo;
    private boolean activo;

    public Estudiante() {
    }

    public Estudiante(String codigo, String nombre, String documento, String correo, boolean activo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.documento = documento;
        this.correo = correo;
        this.activo = activo;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
