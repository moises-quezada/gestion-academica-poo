# Sistema de Gestión Académica (Clave 3)

Proyecto de Programación Orientada a Objetos – Universidad de El Salvador en Línea.
Java 21 · Java Swing · MongoDB · Maven (NetBeans 22).

**Autor:** Moisés Geovanni Quezada Sosa (QS22005) · **Grupo:** 3

## Requisitos
- JDK 21
- NetBeans 22 (o cualquier IDE con Maven)
- MongoDB Community 7+ en ejecución (por defecto `mongodb://localhost:27017`)
- `mongosh` (para el script de inicialización)

## Instalación y ejecución
1. Iniciar el servicio de MongoDB.
2. Inicializar la base de datos con datos de prueba:
   ```
   mongosh scripts/init_db.js
   ```
   (Alternativa: abrir la aplicación; en el primer inicio de sesión se crea el usuario `admin` si no existe ninguno.)
3. Si tu MongoDB no es local, editar `src/main/resources/config.properties`
   (usa `config.properties.template` como guía; **no subas credenciales reales a GitHub**).
4. En NetBeans: *File → Open Project* → seleccionar esta carpeta → *Run* (F6).
   Por consola: `mvn compile exec:java -Dexec.mainClass=academico.Main`
   o `mvn package` y `java -jar target/proyecto-academico-1.0.jar`.

## Credenciales de prueba
- Usuario: `admin`
- Contraseña: `Admin123*`
(En la base solo se guarda el hash PBKDF2-HMAC-SHA256 con sal; nunca la contraseña en texto plano.)

## Estructura
| Paquete | Responsabilidad |
|---|---|
| `academico.modelo` | Clases de dominio: `Estudiante`, `Curso`, `Matricula`, `Usuario` |
| `academico.dao` | Persistencia en MongoDB (una clase por colección) |
| `academico.servicio` | Reglas de negocio y validaciones |
| `academico.util` | Conexión, hash de contraseñas, validador, sesión |
| `academico.vista` | Interfaces Swing |
| `academico.excepcion` | `NegocioException` para errores de validación |

## Colecciones de MongoDB
- `usuarios`: `username` (único), `passwordHash`, `rol`
- `estudiantes`: `codigo` (único), `nombre`, `documento` (único), `correo`, `activo`
- `cursos`: `codigo` (único), `nombre`, `descripcion`, `docente`, `cupoMaximo`
- `matriculas`: `codigo` (único), `codigoEstudiante`, `codigoCurso`, `fecha`, `estado` (ACTIVA/RETIRADA), `fechaRetiro`

## Reglas implementadas
- Códigos de estudiante, curso y matrícula únicos; documento de estudiante único.
- No se matricula a estudiantes/cursos inexistentes ni a estudiantes inactivos.
- No se duplica una matrícula activa del mismo estudiante en el mismo curso.
- No se supera el cupo máximo; los cupos se calculan con las matrículas activas.
- No se retira una matrícula inexistente o ya retirada (el registro se conserva como RETIRADA).
- No se eliminan estudiantes ni cursos con matrículas asociadas (se ofrece inactivar estudiantes).
- Manejo de errores de conexión, lectura y escritura en MongoDB.
