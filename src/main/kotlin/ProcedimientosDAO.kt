import java.sql.Types

/*
@author Antonio De la torre
Entidades: Mecanicos, Reparaciones y Reparar
 */

object ProcedimientosDAO {

    // Funcion calcular ingresos mecanico
    fun mostrarIngresosMecanico() {
        var idMecanico: Int?
        do {
            println("Introduce el ID del mecánico para calcular sus ingresos:")
            idMecanico = readln().toIntOrNull()
        } while (idMecanico == null)

        conectarBD()?.use { conn ->
            conn.prepareCall("{ ? = call fn_calcular_ingresos_mecanico(?) }").use { cstmt ->
                cstmt.registerOutParameter(1, Types.DOUBLE)
                cstmt.setInt(2, idMecanico)
                cstmt.execute()

                val total = cstmt.getDouble(1)
                println("El mecánico con ID $idMecanico ha generado un total de: ${String.format("%.2f", total)}€")
            }
        } ?: println("No se pudo establecer la conexión.")
    }

    // Consulta reparaciones
    fun consultarReparacionesPorMecanico() {
        var idMecanico: Int?
        do {
            println("Introduce el ID del mecánico para ver sus reparaciones:")
            idMecanico = readln().toIntOrNull()
        } while (idMecanico == null)

        conectarBD()?.use { conn ->
            conn.prepareCall("{ call sp_consultar_reparaciones_mecanico(?) }").use { cstmt ->
                cstmt.setInt(1, idMecanico)
                cstmt.executeQuery().use { rs ->
                    var hayDatos = false
                    while (rs.next()) {
                        hayDatos = true
                        println(
                            "ID Rep: ${rs.getInt("id_reparacion")} | " +
                                    "Matrícula: ${rs.getString("matricula")} | " +
                                    "Avería: ${rs.getString("descripcion")} | " +
                                    "Estado: ${rs.getString("estado")} | " +
                                    "Horas: ${rs.getDouble("horas_trabajadas")}h | " +
                                    "Importe: ${String.format("%.2f", rs.getDouble("importe"))}€"
                        )
                    }
                    if (!hayDatos) {
                        println("El mecánico con ID $idMecanico no tiene reparaciones asignadas.")
                    }
                }
            }
        } ?: println("No se pudo establecer la conexión.")
    }

    // Cambiar estado de una reparacion
    fun cambiarEstadoReparacion() {
        var idReparacion: Int?
        do {
            println("Introduce el ID de la reparación a actualizar:")
            idReparacion = readln().toIntOrNull()
        } while (idReparacion == null)

        println("Introduce el nuevo estado (PENDIENTE / EN PROCESO / FINALIZADA):")
        val nuevoEstado = readln().trim().uppercase()

        conectarBD()?.use { conn ->
            conn.prepareCall("{ call sp_actualizar_estado_reparacion(?, ?) }").use { cstmt ->
                cstmt.setInt(1, idReparacion)
                cstmt.setString(2, nuevoEstado)
                val filas = cstmt.executeUpdate()

                if (filas > 0) {
                    println("Estado de la reparación $idReparacion actualizado a '$nuevoEstado'.")
                } else {
                    println("No se encontró ninguna reparación con ID $idReparacion.")
                }
            }
        } ?: println("No se pudo establecer la conexión.")
    }
}