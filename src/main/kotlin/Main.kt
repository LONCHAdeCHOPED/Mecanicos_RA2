import MecanicosDAO.actualizarMecanico
import MecanicosDAO.insertarMecanico
import MecanicosDAO.listarMecanicos
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

// Constante con la ruta a la base de datos
const val URL_BD = "jdbc:sqlite:datos/mecanicos.sqlite"

// Funcion para conectar a la base de datos
fun conectarBD(): Connection? {
    return try {
        DriverManager.getConnection(URL_BD)
    } catch (e: SQLException) {
        e.printStackTrace()
        null
    }
}

fun main() {
    conectarBD()?.use { conn ->
        println("Conectado a la BD")

        conn.createStatement().use { stmt ->
            stmt.executeQuery("SELECT * FROM mecanicos").use { rs ->
                while (rs.next()) {
                    println(rs.getString("nombre_mecanico"))
                }
            }
        }
    } ?: println("No se pudo conectar a la base de datos")

    var numero : Int
    val ruta = Path.of("datos", "mecanicos.csv")
    val conexion = conectarBD()

    do {
        println("--------------------------------")
        println("-------- MENU PRINCIPAL --------")
        println("--------------------------------")
        println("1. Importar datos desde fichero CSV")
        println("2. Leer información")
        println("3. Añadir un registro nuevo")
        println("4. Modificar un registro existente (por ID)")
        println("5. Eliminar un registro existente (por ID)")
        println("0. Volver al menú principal")
        numero = readLine()!!.toInt()
        when (numero) {
            1 -> leerDatosCSV(ruta)
            2 -> listarMecanicos()
            3 -> añadirMecanico()
            4 -> modificarMecanico()
            0 -> println("Volviendo al menu principal...")
        }
    } while (numero != 0)
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