import UI.getConnection

data class match(val jugador1 : player, val jugador2 : player)

enum class stadoMatch(estado : String){
    EnProgreso("EnProgreso"),
    Finalizado("Finalizado")
}
data class tables(
    var match: match? = null,
    val id: Int,
    var estado: stadoMatch?
)

fun insertMesas(num: Int) {
    getConnection()?.use { conn ->
        conn.prepareStatement("INSERT OR IGNORE INTO mesas(id) VALUES(?)").use { pstmt ->
            for (i in 1..num) {
                pstmt.setInt(1, i)
                pstmt.addBatch()
            }
            pstmt.executeBatch()
        }
    } ?: println("No se pudo establecer la conexión con la base de datos")

}





fun getMesas(): List<tables> {
    val mesas = mutableListOf<tables>()

    getConnection()?.use { conn ->
        conn.createStatement().use { stmt ->
            val rs = stmt.executeQuery("""
                SELECT
                    m.id AS mesa_id,
                    r.ID AS resultado_id,
                    r.Jugador1_ID,
                    r.Jugador2_ID,
                    r.Resultado,
                    r.EstadoMach,
                    j1.Nombre AS jugador1,
                    j2.Nombre AS jugador2,
                    j1.Estado AS estado1,
                    j2.Estado AS estado2
                FROM mesas m
                LEFT JOIN resultado r
                    ON r.Id_Mesa = m.id
                   AND r.EstadoMach = 'EnProgreso'
                LEFT JOIN jugadores j1 ON r.Jugador1_ID = j1.Id
                LEFT JOIN jugadores j2 ON r.Jugador2_ID = j2.Id
                ORDER BY m.id
            """.trimIndent())

            while (rs.next()) {
                val mesaId = rs.getInt("mesa_id")

                val j1Id = rs.getInt("Jugador1_ID").takeIf { !rs.wasNull() }
                val j2Id = rs.getInt("Jugador2_ID").takeIf { !rs.wasNull() }

                val match = if (j1Id != null && j2Id != null) {
                    match(
                        player(j1Id, rs.getString("jugador1"), getStado(rs.getString("estado1"))),
                        player(j2Id, rs.getString("jugador2"), getStado(rs.getString("estado2")))
                    )
                } else null

                mesas.add(
                    tables(
                        id = mesaId,
                        match = match,
                        estado = if (match != null) stadoMatch.EnProgreso else null
                    )
                )
            }
        }
    } ?: println("No se pudo establecer la conexión")

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

    val match = mesa.match ?: return

    val j1Id = match.jugador1.id ?: return
    val j2Id = match.jugador2.id ?: return

    getConnection()?.use { conn ->
        val sql = """
            INSERT INTO resultado 
            (Jugador1_ID, Jugador2_ID, Id_Mesa, EstadoMach)
            VALUES (?, ?, ?, ?)
        """.trimIndent()

        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setInt(1, j1Id)
            pstmt.setInt(2, j2Id)
            pstmt.setInt(3, mesa.id)
            pstmt.setString(4, stadoMatch.EnProgreso.name)

            pstmt.executeUpdate()
        }
    }
}


fun updateResultado(
    mesaId: Int,
    resultado: String
) {
    getConnection()?.use { conn ->
        val sql = """
            UPDATE resultado
            SET Resultado = ?, EstadoMach = ?
            WHERE Id_Mesa = ?
        """.trimIndent()

        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setString(1, resultado)
            pstmt.setString(2, stadoMatch.Finalizado.name)
            pstmt.setInt(3, mesaId)
            pstmt.executeUpdate()
        }
    } ?: println("No se pudo establecer conexión con la base de datos")
}




fun removeAllMesas() {
    getConnection()?.use { conn ->
        var sql = "DELETE FROM resultado"
        conn.createStatement().use { stmt ->
            val deletedRows = stmt.executeUpdate(sql)
            println("Se eliminaron $deletedRows resultado de la base de datos")
        }
        sql  = "DELETE FROM mesas"
        conn.createStatement().use {
            stmt ->
            val deletedRows = stmt.executeUpdate(sql)
            println("Se eliminaron $deletedRows mesas de la base de datos")
        }

    } ?: println("No se pudo establecer la conexión para eliminar mesas")
}
