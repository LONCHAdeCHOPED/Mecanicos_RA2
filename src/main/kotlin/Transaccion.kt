import java.sql.SQLException

/*
@author Antonio De la torre
Entidades: Mecanicos, Reparaciones y Reparar
 */

object Transaccion {

    fun asignarMecanicoAReparacion() {

        println("---------------------------------------")
        println("---ASIGNAR MECANICA A UNA REPARACION---")
        println("---------------------------------------")

        var idMecanico: Int?
        do {
            println("Introduce el ID del mecánico:")
            idMecanico = readln().toIntOrNull()
        } while (idMecanico == null)

        var idReparacion: Int?
        do {
            println("Introduce el ID de la reparación:")
            idReparacion = readln().toIntOrNull()
        } while (idReparacion == null)

        var horas: Double?
        do {
            println("Introduce las horas trabajadas:")
            horas = readln().toDoubleOrNull()?.takeIf { it > 0 }
        } while (horas == null)

        conectarBD()?.use { conn ->
            try {
                conn.autoCommit = false
                val tarifa = conn.prepareStatement("SELECT tarifa FROM mecanicos WHERE id_mecanico = ?").use { stmt ->
                    stmt.setInt(1, idMecanico)
                    stmt.executeQuery().use { rs ->
                        if (!rs.next()) {
                            println("Error: Mecánico $idMecanico no encontrado.")
                            conn.rollback()
                            return
                        }
                        rs.getDouble("tarifa")
                    }
                }
                conn.prepareStatement("SELECT id_reparacion FROM reparaciones WHERE id_reparacion = ?").use { stmt ->
                    stmt.setInt(1, idReparacion)
                    stmt.executeQuery().use { rs ->
                        if (!rs.next()) {
                            println("Error: Reparación $idReparacion no encontrada.")
                            conn.rollback()
                            return
                        }
                    }
                }

                val importe = tarifa * horas
                conn.prepareStatement("UPDATE reparaciones SET coste_total = coste_total + ? WHERE id_reparacion = ?").use { stmt ->
                    stmt.setDouble(1, importe)
                    stmt.setInt(2, idReparacion)
                    stmt.executeUpdate()
                }

                conn.prepareStatement("INSERT INTO reparar(id_mecanico, id_reparacion, horas_trabajadas, importe) VALUES (?, ?, ?, ?)").use { stmt ->
                    stmt.setInt(1, idMecanico)
                    stmt.setInt(2, idReparacion)
                    stmt.setDouble(3, horas)
                    stmt.setDouble(4, importe)
                    stmt.executeUpdate()
                }
                conn.commit()
                println("Asignación realizada con éxito (+${String.format("%.2f", importe)}€).")

            } catch (e: SQLException) {
                conn.rollback()
                if (e.message?.contains("UNIQUE constraint failed") == true || e.message?.contains("Duplicate entry") == true) {
                    println("Error: Este mecánico ya está asignado a esta reparación.")
                } else {
                    println("Error en la transacción: ${e.message}")
                }
            } finally {
                conn.autoCommit = true
            }
        } ?: println("No se pudo establecer la conexión.")
    }
}