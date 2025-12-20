import UI.getConnection

data class match(val jugador1 : player, val jugador2 : player){

}
data class ResultadoMesas(
    val mesas: ArrayDeque<tables>,
    val colaRestante: ArrayDeque<player>
)

enum class stadoMatch(estado : String){
    EnProgreso("EnProgreso"),
    Finalizado("Finalizado")
}
data class tables(
    var match: match? = null,
    val id: Int,
    var estado: stadoMatch?
)







fun getMesas() : List<tables>{
    val mesas = mutableListOf<tables>()
    getConnection()?.use { conn ->
        conn.createStatement().use { stmt ->
            val rs = stmt.executeQuery("SELECT\n" +
                    "    r.id,\n" +
                    "    r.Jugador1_ID,\n" +
                    "    r.Jugador2_ID,\n" +
                    "    j1.nombre AS jugador1,\n" +
                    "    j2.nombre AS jugador2,\n" +
                    "    j1.estado AS estado1,\n" +
                    "    j2.estado AS estado2,\n" +
                    "    r.resultado,\n" +
                    "    r.EstadoMach\n" +
                    "FROM resultado r\n" +
                    "JOIN jugadores j1 ON r.Jugador1_ID = j1.id\n" +
                    "JOIN jugadores j2 ON r.Jugador2_ID = j2.id\n" +
                    "WHERE r.EstadoMach = 'EnProgreso';\n")
            while (rs.next()) {
                val id = rs.getInt("id")
                val jugador1 = rs.getString("jugador1")
                val jugador2 = rs.getString("jugador2")
                val IDjugador1 = rs.getInt("Jugador1_ID")
                val IDjugador2 = rs.getInt("Jugador2_ID")
                val Estadojugador1 = getStado(rs.getString("estado1"))
                val Estadojugador2 = getStado(rs.getString("estado2"))

                val resultado = rs.getString("resultado")
                val estado = getStadoMach(rs.getString("EstadoMach"))

                val match = match(
                    jugador1 = player(id = IDjugador1, nombre = jugador1, estado = Estadojugador1),
                    jugador2 = player(id = IDjugador2, nombre = jugador2, estado = Estadojugador2)
                )

                mesas.add(tables(id = id, match = match, estado = estado))
            }
        }
    } ?: println("La conexion no se ha podido establecer")
    return mesas
}

fun getStadoMach(estado: String): stadoMatch {
    return when (estado) {
        stadoMatch.EnProgreso.name -> stadoMatch.EnProgreso
        stadoMatch.Finalizado.name -> stadoMatch.Finalizado
        else -> stadoMatch.Finalizado
    }
}


fun insertarMach(mesa: tables) {

    val match = mesa.match
    if (match == null) {
        println("No se puede insertar una mesa sin jugadores")
        return
    }

    val j1Id = match.jugador1.id
    val j2Id = match.jugador2.id

    if (j1Id == null || j2Id == null) {
        println("Los jugadores deben tener ID para insertar la mesa")
        return
    }

    getConnection()?.use { conn ->
        val sql = """
            INSERT INTO resultado 
            (Jugador1_ID, Jugador2_ID, resultado, EstadoMach)
            VALUES (?, ?, ?, ?)
        """.trimIndent()

        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setInt(1, j1Id)
            pstmt.setInt(2, j2Id)
            pstmt.setString(3, null) // Resultado aún no decidido
            pstmt.setString(4, mesa.estado?.name ?: stadoMatch.EnProgreso.name)

            val rows = pstmt.executeUpdate()

            if (rows > 0) {
                println("Mesa insertada correctamente: ${match.jugador1.nombre} vs ${match.jugador2.nombre}")
            } else {
                println("No se pudo insertar la mesa")
            }
        }

    } ?: println("No se pudo establecer conexión con la base de datos")
}
fun updateResultado(
    mesaId: Int,
    resultado: String,
    estado: stadoMatch
) {
    getConnection()?.use { conn ->
        val sql = """
            UPDATE resultado
            SET resultado = ?, EstadoMach = ?
            WHERE id = ?
        """.trimIndent()

        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setString(1, resultado)
            pstmt.setString(2, estado.name) // EnProgreso / Finalizado
            pstmt.setInt(3, mesaId)
            pstmt.executeUpdate()
        }
    } ?: println("No se pudo establecer conexión con la base de datos")
}


fun removeAllMesas() {
    getConnection()?.use { conn ->
        val sql = "DELETE FROM resultado"
        conn.createStatement().use { stmt ->
            val deletedRows = stmt.executeUpdate(sql)
            println("Se eliminaron $deletedRows mesas de la base de datos")
        }
    } ?: println("No se pudo establecer la conexión para eliminar mesas")
}
