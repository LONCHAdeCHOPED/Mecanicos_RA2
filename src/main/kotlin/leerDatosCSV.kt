import java.nio.file.Files
import java.nio.file.Path
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader


fun leerDatosCSV(ruta: Path): List<Mecanico> {
    var mecanicos: List<Mecanico> = emptyList()

    if (!Files.isReadable(ruta)) {
        println("Error: No se puede leer el fichero en la ruta: $ruta")

    } else {
        val reader = csvReader(){
            delimiter = ';'
        }

        val filas: List<List<String>> = reader.readAll(ruta.toFile())

        mecanicos = filas.mapNotNull { columnas ->
            if (columnas.size >= 5){
                try {
                    val idMecanico = columnas[0].toInt()
                    val nombreMecanico = columnas[1]
                    val especialidadMecanico = columnas[2]
                    val experienciaMecanico = columnas[3].toInt()
                    val tarifaHoraMecanico = columnas[4].toDouble()
                    Mecanico(idMecanico, nombreMecanico, especialidadMecanico, experienciaMecanico, tarifaHoraMecanico)
                } catch (e: Exception){
                    println("Fila inválida ignorada: $columnas -> Error: ${e.message}")
                    null
                }
            } else {
                println("Fila con formato incompleto ignorada: $columnas")
                null
            }

        }
    }
    for (mecanicos in mecanicos){
        println("ID: ${mecanicos.id_mecanico}, " +
                "Nombre: ${mecanicos.nombre}, " +
                "Especialidad: ${mecanicos.especialidad}, " +
                "Experiencia: ${mecanicos.experiencia}, " +
                "Tarifa: ${mecanicos.tarifahora}")
    }
    return mecanicos

}