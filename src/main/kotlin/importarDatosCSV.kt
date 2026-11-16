import java.nio.file.Path

fun importarCSVaBD(ruta: Path) {
    val listaMecanicos = leerDatosCSV(ruta)

    if (listaMecanicos.isEmpty()) {
        println("No hay datos válidos en el CSV para importar.")
        return
    }

    conectarBD()?.use { conn ->
        val sqlInsert = "INSERT INTO mecanicos (nombre_mecanico, especialidad, experiencia, tarifa_hora) VALUES (?, ?, ?, ?)"

        conn.prepareStatement(sqlInsert).use { pstmt ->
            var importados = 0

            for (mecanico in listaMecanicos) {
                try {
                    pstmt.setString(1, mecanico.nombre)
                    pstmt.setString(2, mecanico.especialidad)
                    pstmt.setInt(3, mecanico.experiencia)
                    pstmt.setDouble(4, mecanico.tarifahora)

                    pstmt.executeUpdate()
                    importados++
                } catch (e: Exception) {
                    println("Error al importar al mecánico '${mecanico.nombre}': ${e.message}")
                }
            }
            println("Importación finalizada: $importados de ${listaMecanicos.size} mecánicos añadidos a la BD.")
        }
    } ?: println("Error: No se pudo conectar a la base de datos.")
}