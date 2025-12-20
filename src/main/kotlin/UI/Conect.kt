package UI

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException


const val URL_BD = "jdbc:sqlite:./src/main/resources/Masnou.db"
fun getConnection(): Connection? {
    return try {
        DriverManager.getConnection(URL_BD)
    } catch (e: SQLException) {
        e.printStackTrace()
        null
    }
}