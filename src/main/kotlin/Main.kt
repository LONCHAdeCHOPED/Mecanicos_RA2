import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


/*
@author Antonio De la torre
Entidades: Mecanicos, Reparaciones y Reparar
 */

// Constante con la ruta a la base de datos
const val URL_BD = "jdbc:mysql://localhost:3306/mecanicos"
val usuario = "root"
val contrasenya = "hola01"

// Funcion para conectar a la base de datos
fun conectarBD(): Connection? {
    return try {
        DriverManager.getConnection(URL_BD, usuario, contrasenya)
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
        println("2. CRUD Reparaciones")
        println("3. Asignar Mecanico a una Reparacion (Transaccion)")
        println("0. Salir")
        numero = readLine()!!.toInt()
        when (numero) {
            1 -> menuMecanicos()
            2 -> menuReparar()
            3 -> Transaccion.asignarMecanicoAReparacion()
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
        println("6. Calcular ingreoso de un Mecanico (Funcion)")
        println("7. Consultar reparaciones de un Mecanico (Procedimiento)")
        println("0. Volver al menú principal")
        numero = readLine()!!.toInt()
        when (numero) {
            1 -> importarCSVaBD(ruta)
            2 -> MecanicosDAO.listarMecanicos()
            3 -> MecanicosDAO.añadirMecanico()
            4 -> MecanicosDAO.modificarMecanico()
            5 -> MecanicosDAO.eliminarMecanico()
            6 -> ProcedimientosDAO.mostrarIngresosMecanico()
            7 -> ProcedimientosDAO.consultarReparacionesPorMecanico()
            0 -> println("Volviendo al menu principal..." + menuPrincipal())
        }
    } while (numero != 0)
}

fun menuReparar(){
    var numero : Int
    val ruta = Path.of("datos", "reparaciones.csv")

    do {
        println("--------------------------------")
        println("--------- CRUD REPARAR ---------")
        println("--------------------------------")
        println("1. Importar datos desde fichero CSV")
        println("2. Leer informacion")
        println("3. Añadir un registro nuevo")
        println("4. Modificar un registro existente (por ID)")
        println("5. Eliminar un registro existente (por ID)")
        println("6. Cambier estado de una reparacion (Procedimiento)")
        println("0. Volver al menú principal... ")
        numero = readLine()!!.toInt()
        when (numero) {
            1 -> importarReparacionesCSVaBD(ruta)
            2 -> ReparacionesDAO.listarReparaciones()
            3 -> ReparacionesDAO.añadirReparacion()
            4 -> ReparacionesDAO.modificarReparacion()
            5 -> ReparacionesDAO.eliminarReparacion()
            6 -> ProcedimientosDAO.cambiarEstadoReparacion()
            0 -> println("Volviendo al menu principal..." + menuPrincipal())
        }
    } while (numero != 0)

}