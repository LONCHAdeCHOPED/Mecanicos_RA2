import java.sql.SQLException

/*
@author Antonio De la torre
Entidades: Mecanicos, Reparaciones y Reparar
 */

data class Reparacion(
    val id_reparacion: Int? = null,
    val matricula: String,
    val descripcion: String,
    val fecha_entrada: String,
    val estado: String = "PENDIENTE",
    val coste_total: Double = 0.0
)

object ReparacionesDAO {

    // ==========================================
    // 1. MÉTODOS DE ACCESO A BASE DE DATOS (CRUD)
    // ==========================================

    fun listarReparaciones(): List<Reparacion> {
        val lista = mutableListOf<Reparacion>()
        conectarBD()?.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeQuery("SELECT * FROM reparaciones").use { rs ->
                    while (rs.next()) {
                        lista.add(
                            Reparacion(
                                id_reparacion = rs.getInt("id_reparacion"),
                                matricula = rs.getString("matricula"),
                                descripcion = rs.getString("descripcion"),
                                fecha_entrada = rs.getString("fecha_entrada"),
                                estado = rs.getString("estado"),
                                coste_total = rs.getDouble("coste_total")
                            )
                        )
                    }
                }
            }
        } ?: println("No se pudo establecer la conexión.")

        for (rep in lista) {
            println(
                "ID: ${rep.id_reparacion}, " +
                        "Matrícula: ${rep.matricula}, " +
                        "Descripción: ${rep.descripcion}, " +
                        "Fecha: ${rep.fecha_entrada}, " +
                        "Estado: ${rep.estado}, " +
                        "Coste Total: ${rep.coste_total}€"
            )
        }
        return lista
    }

    fun consultarReparacionPorId(id: Int): Reparacion? {
        var reparacion: Reparacion? = null
        conectarBD()?.use { conn ->
            conn.prepareStatement("SELECT * FROM reparaciones WHERE id_reparacion = ?").use { pstmt ->
                pstmt.setInt(1, id)
                pstmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        reparacion = Reparacion(
                            id_reparacion = rs.getInt("id_reparacion"),
                            matricula = rs.getString("matricula"),
                            descripcion = rs.getString("descripcion"),
                            fecha_entrada = rs.getString("fecha_entrada"),
                            estado = rs.getString("estado"),
                            coste_total = rs.getDouble("coste_total")
                        )
                    }
                }
            }
        } ?: println("No se pudo establecer la conexión.")
        return reparacion
    }

    fun insertarReparacion(reparacion: Reparacion) {
        conectarBD()?.use { conn ->
            conn.prepareStatement(
                "INSERT INTO reparaciones(matricula, descripcion, fecha_entrada, estado, coste_total) VALUES (?, ?, ?, ?, ?)"
            ).use { pstmt ->
                pstmt.setString(1, reparacion.matricula)
                pstmt.setString(2, reparacion.descripcion)
                pstmt.setString(3, reparacion.fecha_entrada)
                pstmt.setString(4, reparacion.estado)
                pstmt.setDouble(5, reparacion.coste_total)
                pstmt.executeUpdate()
                println("Reparación para matrícula '${reparacion.matricula}' insertada con éxito.")
            }
        } ?: println("No se pudo establecer la conexión.")
    }

    fun actualizarReparacion(reparacion: Reparacion) {
        if (reparacion.id_reparacion == null) {
            println("No se puede actualizar una reparación sin id.")
            return
        }
        conectarBD()?.use { conn ->
            conn.prepareStatement(
                "UPDATE reparaciones SET matricula = ?, descripcion = ?, fecha_entrada = ?, estado = ?, coste_total = ? WHERE id_reparacion = ?"
            ).use { pstmt ->
                pstmt.setString(1, reparacion.matricula)
                pstmt.setString(2, reparacion.descripcion)
                pstmt.setString(3, reparacion.fecha_entrada)
                pstmt.setString(4, reparacion.estado)
                pstmt.setDouble(5, reparacion.coste_total)
                pstmt.setInt(6, reparacion.id_reparacion)
                val filas = pstmt.executeUpdate()
                if (filas > 0) {
                    println("Reparación con id=${reparacion.id_reparacion} actualizada con éxito.")
                } else {
                    println("No se encontró ninguna reparación con id=${reparacion.id_reparacion}.")
                }
            }
        } ?: println("No se pudo establecer la conexión.")
    }


    // ==========================================
    // 2. MÉTODOS INTERACTIVOS DE CONSOLA (MENÚ)
    // ==========================================

    fun añadirReparacion() {
        println("Matrícula del vehículo: ")
        val matricula = readln()

        println("Descripción de la avería: ")
        val descripcion = readln()

        println("Fecha de entrada (YYYY-MM-DD): ")
        val fecha = readln()

        println("Estado (PENDIENTE / EN PROCESO / FINALIZADA): ")
        val estado = readln().uppercase().ifBlank { "PENDIENTE" }

        val reparacion = Reparacion(null, matricula, descripcion, fecha, estado, 0.0)
        insertarReparacion(reparacion)
    }

    fun modificarReparacion() {
        var idReparacion = 0
        var idValido = false

        do {
            println("Introduce el ID de la reparación a modificar:")
            val bandera = readln().toIntOrNull()
            if (bandera == null) {
                println("Error, ID no válido.")
            } else {
                idReparacion = bandera
                idValido = true
            }
        } while (!idValido)

        var costeActual = 0.0
        conectarBD()?.use { conn ->
            conn.prepareStatement("SELECT matricula, coste_total FROM reparaciones WHERE id_reparacion = ?")
                .use { stmt ->
                    stmt.setInt(1, idReparacion)
                    stmt.executeQuery().use { rs ->
                        if (!rs.next()) {
                            println("No se encontró la reparación.")
                            return
                        }
                        println("Reparación encontrada -> Matrícula: ${rs.getString("matricula")}")
                        costeActual = rs.getDouble("coste_total")
                    }
                }
        } ?: return

        println("Nueva matrícula: ")
        val matricula = readln()

        println("Nueva descripción: ")
        val descripcion = readln()

        println("Nueva fecha de entrada (YYYY-MM-DD): ")
        val fecha = readln()

        println("Nuevo estado (PENDIENTE / EN PROCESO / FINALIZADA): ")
        val estado = readln().uppercase()

        val reparacionModificada = Reparacion(idReparacion, matricula, descripcion, fecha, estado, costeActual)
        actualizarReparacion(reparacionModificada)
    }

    fun eliminarReparacion() {
        var idReparacion: Int?
        do {
            println("Introduce el ID de la reparación a eliminar:")
            idReparacion = readln().toIntOrNull()
        } while (idReparacion == null)

        conectarBD()?.use { conn ->
            // 1. Comprobar que la reparación existe
            conn.prepareStatement("SELECT matricula, descripcion FROM reparaciones WHERE id_reparacion = ?").use { stmt ->
                stmt.setInt(1, idReparacion)
                stmt.executeQuery().use { rs ->
                    if (!rs.next()) {
                        println("No se encontró la reparación.")
                        return
                    }
                    println("Reparación encontrada: ${rs.getString("matricula")} - ${rs.getString("descripcion")}")
                }
            }

            // 2. Comprobar si tiene relaciones en 'reparar'
            val tieneRelaciones = conn.prepareStatement("SELECT 1 FROM reparar WHERE id_reparacion = ?").use { stmt ->
                stmt.setInt(1, idReparacion)
                stmt.executeQuery().use { it.next() }
            }

            if (tieneRelaciones) {
                println("AVISO: Esta reparación tiene registros en la tabla 'reparar' que también se borrarán.")
            }

            // 3. Confirmar y borrar
            println("¿Confirmas el borrado? (S/N):")
            if (readln().trim().uppercase() == "S") {
                if (tieneRelaciones) {
                    conn.prepareStatement("DELETE FROM reparar WHERE id_reparacion = ?").use {
                        it.setInt(1, idReparacion)
                        it.executeUpdate()
                    }
                }
                conn.prepareStatement("DELETE FROM reparaciones WHERE id_reparacion = ?").use {
                    it.setInt(1, idReparacion)
                    it.executeUpdate()
                }
                println("Reparación eliminada correctamente.")
            } else {
                println("Operación cancelada.")
            }
        }
    }
}

