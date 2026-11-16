import MecanicosDAO.actualizarMecanico
import MecanicosDAO.añadirMecanico
import MecanicosDAO.eliminarMecanico
import MecanicosDAO.insertarMecanico
import MecanicosDAO.listarMecanicos
import MecanicosDAO.modificarMecanico
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
    menuPrincipal()
}


fun menuPrincipal(){
    var numero : Int

    do {
        println("--------------------------------")
        println("-------- MENU PRINCIPAL --------")
        println("--------------------------------")
        println("1. CRUD Mecanicos")
        println("2. CRUD Escuderia")
        println("3. Transaccion")
        println("0. Salir")
        numero = readLine()!!.toInt()
        when (numero) {
            1 -> menuMecanicos()
            2 -> menuReparar()
            3 -> añadirMecanico()
            0 -> println("Cerrando la aplicación...")
        }
    } while (numero != 0)
}

fun menuMecanicos(){
    val ruta = Path.of("datos", "mecanicos.csv")
    var numero : Int
    do {
        println("--------------------------------")
        println("-------- CRUD MECANICOS --------")
        println("--------------------------------")
        println("1. Importar datos desde fichero CSV")
        println("2. Leer información")
        println("3. Añadir un registro nuevo")
        println("4. Modificar un registro existente (por ID)")
        println("5. Eliminar un registro existente (por ID)")
        println("0. Volver al menú principal")
        numero = readLine()!!.toInt()
        when (numero) {
            1 -> importarCSVaBD(ruta)
            2 -> listarMecanicos()
            3 -> añadirMecanico()
            4 -> modificarMecanico()
            5 -> eliminarMecanico()
            0 -> println("Volviendo al menu principal..." + menuPrincipal())
        }
    } while (numero != 0)
}

fun menuReparar(){
    var numero : Int
    do {
        println("--------------------------------")
        println("-------- CRUD REPARAR --------")
        println("--------------------------------")
        println("1. Leer informacion")
        println("2. Añadir un registro nuevo")
        println("3. Modificar un registro existente (por ID)")
        println("4. Eliminar un registro existente (por ID)")
        println("0. Volver al menú principal... " + menuPrincipal())
        numero = readLine()!!.toInt()
        when (numero) {
            1 ->
            2 ->
            3 ->
            4 ->
            5 ->
            0 -> println("Volviendo al menu principal..." + menuPrincipal())
        }
    } while (numero != 0)

}