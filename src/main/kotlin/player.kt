import UI.getConnection
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.sql.SQLException


data class player(val id: Int?, val nombre:String, var estado: MutableState<stadoPrimary>, var puntos: Double? = 0.0)

enum class stadoPrimary(estado : String){
    Activo("Activo"),
    Inactivo("Inactivo"),
    Cola("En Cola")
}


fun getListaPlayers() : List<player>{
    val tipos = mutableListOf<player>()
    getConnection()?.use { conn ->
        conn.createStatement().use { stmt ->
            val rs = stmt.executeQuery("SELECT * FROM jugadores")
            while (rs.next()) {
                val id = rs.getInt("ID")
                val nombre = rs.getString("Nombre")
                val estado = getStado(rs.getString("Estado"))
                tipos.add(player(id, nombre, estado))
            }
        }
    } ?: println("La conexion no se ha podido establecer")
    return tipos
}

fun getPlayer(id: Int): player? {
    var result: player? = null
    getConnection()?.use { conn ->
        val sql = "SELECT * FROM jugadores WHERE id = ?"
        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setInt(1, id)
            val rs = pstmt.executeQuery()
            if (rs.next()) {
                val nombre = rs.getString("nombre")
                val estado = getStado(rs.getString("Estado"))
                result = player(id = id, nombre = nombre, estado = estado)
            }
        }
    } ?: println("No se pudo establecer la conexión")
    return result
}

fun insertarJugador(name: String): Int? {
    var generatedId: Int? = null
    getConnection()?.use { conn ->
        val sql = "INSERT INTO jugadores (nombre, Estado) VALUES (?, ?)"
        conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { pstmt ->
            pstmt.setString(1, name)
            pstmt.setString(2, stadoPrimary.Cola.name)
            val affectedRows = pstmt.executeUpdate()

            if (affectedRows == 0) {
                throw SQLException("No se pudo insertar el jugador")
            }

            // Recuperar el ID generado
            pstmt.generatedKeys.use { rs ->
                if (rs.next()) {
                    generatedId = rs.getInt(1)
                }
            }
        }
    } ?: println("No se pudo establecer la conexión")
    return generatedId
}


fun getStado(estado: String): MutableState<stadoPrimary> {
    return when (estado) {
        stadoPrimary.Inactivo.name -> mutableStateOf(stadoPrimary.Inactivo)
        stadoPrimary.Activo.name -> mutableStateOf(stadoPrimary.Activo)
        else -> mutableStateOf(stadoPrimary.Cola)
    }
}

fun updatePlayer(player: player) {
    if (player.id == null) return

    getConnection()?.use { conn ->
        val sql = """
            UPDATE jugadores 
            SET Nombre = ?, Estado = ?, Puntuacion = ?
            WHERE ID = ?
        """.trimIndent()

        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setString(1, player.nombre)
            pstmt.setString(2, player.estado.value.name)
            pstmt.setDouble(3, player.puntos ?: 0.0)
            pstmt.setInt(4, player.id)
            pstmt.executeUpdate()
        }
    }
}



fun removeAllPlayers() {
    getConnection()?.use { conn ->
        val sql = "DELETE FROM jugadores"
        conn.createStatement().use { stmt ->
            val deletedRows = stmt.executeUpdate(sql)
            println("Se eliminaron $deletedRows jugadores de la base de datos")
        }
    } ?: println("No se pudo establecer la conexión para eliminar jugadores")
}

