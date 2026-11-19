# Gestor de Taller - Mecánicos y Reparaciones (MySQL)

## 1. De qué va el proyecto

Es una aplicación de consola en **Kotlin** con **Gradle** conectada a **MySQL** para gestionar **mecánicos, reparaciones y las asignaciones entre ambos**. El objetivo es practicar JDBC, transacciones manuales, integridad referencial y rutinas almacenadas en la base de datos.

La base de datos cuenta con tres tablas relacionadas (N:M):

* **`mecanicos`**: Datos de los trabajadores (`nombre_mecanico`, `especialidad`, `experiencia` y `tarifa_hora`).

* **`reparaciones`**: Datos de cada vehículo y avería (`matricula`, `descripcion`, `fecha_entrada`, `estado` y `coste_total`).

* **`reparar`**: Tabla intermedia que relaciona mecánicos y reparaciones junto con las `horas_trabajadas` y el `importe` generado.

Además de los CRUD completos y la importación desde CSV (`mecanicos.csv` y `reparaciones.csv` vaciando previamente las tablas para evitar duplicados), se utilizan **transacciones** y **procedimientos/funciones SQL**.

## 2. Estructura y lógica principal

### **Modelos**

Para manejar los registros en Kotlin se utilizan estas `data class`:

```kotlin
data class Mecanico(
    val id_mecanico: Int? = null,
    val nombre: String,
    val especialidad: String,
    val experiencia: Int,
    val tarifahora: Double
)

data class Reparacion(
    val id_reparacion: Int? = null,
    val matricula: String,
    val descripcion: String,
    val fecha_entrada: String,
    val estado: String = "PENDIENTE",
    val coste_total: Double = 0.0
)
```

### **Conexión, DAOs y Rutinas SQL**

El acceso a datos está organizado en varios archivos para mantener el código limpio y separado del menú principal:

* **Conexión centralizada (`conectarBD()`):** Centraliza la URL, el usuario y la contraseña de MySQL para reutilizarla en todas las consultas.

* **DAOs (`MecanicosDAO`, `ReparacionesDAO`):** Separan las operaciones CRUD y la importación/lectura de CSV de cada entidad.

* **Transacciones (`Transaccion.kt`):** Gestiona la asignación N:M mediante `autoCommit = false`, `commit()` y `rollback()`. Verifica que existan ambos IDs, calcula el importe según la tarifa del mecánico, inserta en `reparar` y suma el importe al `coste_total` de la reparación.

* **Borrado seguro:** Antes de eliminar un mecánico o reparación, comprueba (`SELECT 1`) si tiene registros asociados en `reparar`. Si existen, avisa por consola y, tras confirmación, elimina primero las relaciones y luego el registro principal.

* **Funciones y Procedimientos (`ProcedimientosDAO.kt`):** Emplea `CallableStatement` para ejecutar una función SQL que calcula los ingresos totales de un mecánico, un procedimiento que consulta sus reparaciones asignadas y otro que actualiza el estado de una reparación.

## 3. Cómo ejecutarlo

* **Requisitos:** Tener instalado **JDK 17+**, un servidor **MySQL** activo con las tablas, la función y los procedimientos creados.

* **Pasos:**

  1. Descomprimir el proyecto y abrir la carpeta con **IntelliJ IDEA**.

  2. Esperar a que **Gradle** descargue las dependencias (`mysql-connector-j` y `kotlin-csv`).

  3. Revisar en la función de conexión que el usuario, contraseña y URL coincidan con tu servidor MySQL.

  4. Comprobar que existen los ficheros `mecanicos.csv` y `reparaciones.csv` (delimitados por `;`) en la carpeta de datos.

  5. Ejecutar la función `main` y navegar por las opciones del menú de consola.