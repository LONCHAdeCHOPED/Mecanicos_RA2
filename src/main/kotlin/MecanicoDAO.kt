data class Mecanico(
    val id_mecanico: Int? = null, // lo genera SQLite automáticamente
    val nombre: String,
    val especialidad: String,
    val experiencia: Int,
    val tarifahora: Double
)

/*
@author Antonio De la torre
Entidades: Mecanicos, Reparaciones y Reparar
 */

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

    fun añadirMecanico(){

        println("Nombre del mecanico: ")
        val nombre = readLine()!!

        println("Especialidad: ")
        val especialidad = readLine()!!

        var experienciaValida = false
        var experiencia = 0
        do {
            println("Dime la experiencia del Mecanico")
            val banderaExp = readln().toIntOrNull()
            if (banderaExp == null) {
                println("Error, experiencia del Mecanico no valida")
            } else {
                println("Experiencia del Mecanico valida")
                experiencia = banderaExp
                experienciaValida = true
            }
        } while (!experienciaValida)

        var tarifaValida = false
        var tarifa = 0.0
        do {
            println("Dime la tarifa del Mecanico")
            val tarifaHoraBandera = readln().toDoubleOrNull()
            if (tarifaHoraBandera == null) {
                println("Error, tarifa del Mecanico no valida")
            } else {
                println("Tarifa del Mecanico valida")
                tarifa = tarifaHoraBandera
                tarifaValida = true
            }
        } while (!tarifaValida)

        val mecanico = Mecanico(null, nombre, especialidad, experiencia, tarifa)
        insertarMecanico(mecanico)
    }

    fun modificarMecanico(){
        var idMecanico = 0
        var idValido = false

        do {
            println("Introduce el ID del mecánico a modificar")
            val bandera = readln().toInt()
            if (bandera == null) {
                println("Error, ID no valida")
            } else {
                idMecanico = bandera
                idValido = true
            }
        } while (!idValido)

        conectarBD()?.use { conn ->
            conn.prepareStatement("SELECT nombre_mecanico FROM mecanicos WHERE id_mecanico = ?").use { stmt ->
                stmt.setInt(1, idMecanico)
                stmt.executeQuery().use {rs ->
                    if (!rs.next()) {
                        println("No se encontró al mecanico")
                    }
                    println("Mecanico: ${rs.getString("nombre_mecanico")}")
                }
            }
        }

        println("Nuevo nombre del mecanico")
        val nombre = readLine()!!
        println("Especialidad del Mecanico: ")
        val especialidad = readLine()!!
        println("Experiencia del mecanico [años]")
        var experiencia = readln().toIntOrNull()
        while (experiencia == null) {
            println("Error, experiencia del Mecanico no valida")
            experiencia = readLine()!!.toIntOrNull()
        }
        println("Tarfia del mecanico: ")
        var tarifa = readln().toDoubleOrNull()
        while (tarifa == null) {
            println("Error, tarifa del Mecanico no valida")
            tarifa = readLine()!!.toDoubleOrNull()
        }
        val mecanicoModificado = Mecanico(idMecanico, nombre, especialidad, experiencia, tarifa)
        actualizarMecanico(mecanicoModificado)
        println("Mecanico actualizado")
    }

    fun eliminarMecanico() {
        var idMecanico: Int?
        do {
            println("Introduce el ID del mecánico a eliminar:")
            idMecanico = readln().toIntOrNull()
        } while (idMecanico == null)

        conectarBD()?.use { conn ->
            // 1. Comprobar que el mecánico existe
            conn.prepareStatement("SELECT nombre_mecanico FROM mecanicos WHERE id_mecanico = ?").use { stmt ->
                stmt.setInt(1, idMecanico)
                stmt.executeQuery().use { rs ->
                    if (!rs.next()) {
                        println("No se encontró el mecánico.")
                        return
                    }
                    println("Mecánico encontrado: ${rs.getString("nombre_mecanico")}")
                }
            }

            // 2. Comprobar si tiene relaciones en 'reparar'
            val tieneRelaciones = conn.prepareStatement("SELECT 1 FROM reparar WHERE id_mecanico = ?").use { stmt ->
                stmt.setInt(1, idMecanico)
                stmt.executeQuery().use { it.next() }
            }

            if (tieneRelaciones) {
                println("AVISO: Este mecánico tiene registros en la tabla 'reparar' que también se borrarán.")
            }

            // 3. Confirmar y borrar
            println("¿Confirmas el borrado? (S/N):")
            if (readln().trim().uppercase() == "S") {
                if (tieneRelaciones) {
                    conn.prepareStatement("DELETE FROM reparar WHERE id_mecanico = ?").use {
                        it.setInt(1, idMecanico)
                        it.executeUpdate()
                    }
                }
                conn.prepareStatement("DELETE FROM mecanicos WHERE id_mecanico = ?").use {
                    it.setInt(1, idMecanico)
                    it.executeUpdate()
                }
                println("Mecánico eliminado correctamente.")
            } else {
                println("Operación cancelada.")
            }
        }
    }
}


