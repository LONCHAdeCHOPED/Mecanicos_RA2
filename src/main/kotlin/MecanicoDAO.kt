data class Mecanico(
    val id_mecanico: Int? = null, // lo genera SQLite automáticamente
    val nombre: String,
    val especialidad: String,
    val experiencia: Int,
    val tarifahora: Double
)

object MecanicosDAO {
    fun listarMecanicos(): List<Mecanico> {
        val lista = mutableListOf<Mecanico>()
        conectarBD()?.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeQuery("SELECT * FROM mecanicos").use { rs ->
                    while (rs.next()) {
                        lista.add(
                            Mecanico(
                                id_mecanico = rs.getInt("id_mecanico"),
                                nombre = rs.getString("nombre_mecanico"),
                                especialidad = rs.getString("especialidad"),
                                experiencia = rs.getInt("experiencia"),
                                tarifahora = rs.getDouble("tarifa_hora")
                            )
                        )
                    }
                }
            }
        } ?: println("No se pudo establecer la conexión.")
        for (mecanicos in lista){
            println("ID: ${mecanicos.id_mecanico}, " +
                    "Nombre: ${mecanicos.nombre}, " +
                    "Especialidad: ${mecanicos.especialidad}, " +
                    "Experiencia: ${mecanicos.experiencia}, " +
                    "Tarifa: ${mecanicos.tarifahora} ")
        }
        return lista
    }

    fun consultarMecanicoPorId(id: Int): Mecanico? {
        var mecanico: Mecanico? = null
        conectarBD()?.use { conn ->
            conn.prepareStatement("SELECT * FROM mecanicos WHERE id_mecanico = ?").use { pstmt ->
                pstmt.setInt(1, id)
                pstmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        mecanico = Mecanico(
                            id_mecanico = rs.getInt("id_mecanico"),
                            nombre = rs.getString("nombre_mecanico"),
                            especialidad = rs.getString("especialidad"),
                            experiencia = rs.getInt("experiencia"),
                            tarifahora = rs.getDouble("tarifa_hora")
                        )
                    }
                }
            }
        } ?: println("No se pudo establecer la conexión.")
        return mecanico
    }


    fun insertarMecanico(mecanico: Mecanico) {
        conectarBD()?.use { conn ->
            conn.prepareStatement(
                "INSERT INTO mecanicos(nombre_mecanico, especialidad, experiencia, tarifa_hora) VALUES (?, ?, ?, ?)"
            ).use { pstmt ->
                pstmt.setString(1, mecanico.nombre)
                pstmt.setString(2, mecanico.especialidad)
                pstmt.setInt(3, mecanico.experiencia)
                pstmt.setDouble(4, mecanico.tarifahora)
                pstmt.executeUpdate()
                println("Mecanico '${mecanico.nombre}' insertado con éxito.")
            }
        } ?: println("No se pudo establecer la conexión.")
    }

    fun actualizarMecanico(mecanico: Mecanico) {
        if (mecanico.id_mecanico == null) {
            println("No se puede actualizar un mecanico sin id.")
            return
        }
        conectarBD()?.use { conn ->
            conn.prepareStatement(
                "UPDATE mecanicos SET nombre_mecanico = ?, especialidad = ?, experiencia = ?, tarifa_hora = ? WHERE id_mecanico = ?"
            ).use { pstmt ->
                pstmt.setString(1, mecanico.nombre)
                pstmt.setString(2, mecanico.especialidad)
                pstmt.setInt(3, mecanico.experiencia)
                pstmt.setDouble(4, mecanico.tarifahora)
                pstmt.setInt(5, mecanico.id_mecanico)
                val filas = pstmt.executeUpdate()
                if (filas > 0) {
                    println("Mecanico con id=${mecanico.id_mecanico} actualizado con éxito.")
                } else {
                    println("No se encontró ningun mecanico con id=${mecanico.id_mecanico}.")
                }
            }
        } ?: println("No se pudo establecer la conexión.")
    }

    fun eliminarMecanico(id: Int) {
        conectarBD()?.use { conn ->
            conn.prepareStatement("DELETE FROM mecanicos WHERE id_mecanico = ?").use { pstmt ->
                pstmt.setInt(1, id)
                val filas = pstmt.executeUpdate()
                if (filas > 0) {
                    println("Mecanico con id=$id eliminado correctamente.")
                } else {
                    println("No se encontró ningun mecanico con id=$id.")
                }
            }
        } ?: println("No se pudo establecer la conexión.")
    }
}


