import java.nio.file.Files
import java.nio.file.Path
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader

/*
@author Antonio De la torre
Entidades: Mecanicos, Reparaciones y Reparar
 */

fun leerReparacionesCSV(ruta: Path): List<Reparacion> {
    var reparaciones: List<Reparacion> = emptyList()

    if (!Files.isReadable(ruta)) {
        println("Error: No se puede leer el fichero en la ruta: $ruta")

    } else {
        val reader = csvReader() {
            delimiter = ';'
        }

        val filas: List<List<String>> = reader.readAll(ruta.toFile()).drop(1)

        reparaciones = filas.mapNotNull { columnas ->
            if (columnas.size >= 6) {
                try {
                    val idReparacion = columnas[0].toInt()
                    val matricula = columnas[1]
                    val descripcion = columnas[2]
                    val fechaEntrada = columnas[3]
                    val estado = columnas[4]
                    val costeTotal = columnas[5].toDouble()
                    Reparacion(idReparacion, matricula, descripcion, fechaEntrada, estado, costeTotal)
                } catch (e: Exception) {
                    println("Fila inválida ignorada: $columnas -> Error: ${e.message}")
                    null
                }
            } else {
                println("Fila con formato incompleto ignorada: $columnas")
                null
            }
        }
    }

    for (reparacion in reparaciones) {
        println("ID: ${reparacion.id_reparacion}, " +
                "Matrícula: ${reparacion.matricula}, " +
                "Descripción: ${reparacion.descripcion}, " +
                "Fecha: ${reparacion.fecha_entrada}, " +
                "Estado: ${reparacion.estado}, " +
                "Coste Total: ${reparacion.coste_total}")
    }
    return reparaciones
}

fun importarReparacionesCSVaBD(ruta: Path) {
    val listaReparaciones = leerReparacionesCSV(ruta)

    if (listaReparaciones.isEmpty()) {
        println("No hay datos válidos en el CSV para importar.")
        return
    }

    conectarBD()?.use { conn ->
        // Vaciamos la tabla intermedia y la principal antes de importar (MySQL)
        conn.createStatement().use { stmt ->
            stmt.executeUpdate("DELETE FROM reparar")
            stmt.executeUpdate("DELETE FROM reparaciones")
            stmt.executeUpdate("ALTER TABLE reparaciones AUTO_INCREMENT = 1")
        }

        val sqlInsert = "INSERT INTO reparaciones (matricula, descripcion, fecha_entrada, estado, coste_total) VALUES (?, ?, ?, ?, ?)"

        conn.prepareStatement(sqlInsert).use { pstmt ->
            var importados = 0

            for (reparacion in listaReparaciones) {
                try {
                    pstmt.setString(1, reparacion.matricula)
                    pstmt.setString(2, reparacion.descripcion)
                    pstmt.setString(3, reparacion.fecha_entrada)
                    pstmt.setString(4, reparacion.estado)
                    pstmt.setDouble(5, reparacion.coste_total)

                    pstmt.executeUpdate()
                    importados++
                } catch (e: Exception) {
                    println("Error al importar la reparación '${reparacion.matricula}': ${e.message}")
                }
            }
            println("Importación finalizada: $importados de ${listaReparaciones.size} reparaciones añadidas a la BD.")
        }
    } ?: println("Error: No se pudo conectar a la base de datos.")
}