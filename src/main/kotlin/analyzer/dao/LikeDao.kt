package analyzer.dao

import analyzer.clients.DbClient
import analyzer.model.Like
import analyzer.utils.Utils

/**
 * Created by Nikolay V. Petrov on 07.03.2018.
 */
object LikeDao {
    private val connection = DbClient.connection

    fun findById(id: Int): Like? {
        var like: Like? = null
        val sql = "SELECT id, list FROM likes WHERE id = $id"
        val rs = connection.createStatement().executeQuery(sql)
        with(rs) {
            if (next())
                like = Like(
                    getInt("id"),
                    Utils.stringToList(getString("list")))
        }
        return like
    }

    fun upsert(like: Like) {
        val sql =
                with (like) {
                    "INSERT INTO likes (id, list) VALUES ($id, '${Utils.listToString(list)}')" +
                            "ON CONFLICT (id) DO UPDATE SET list = '${Utils.listToString(list)}'"
                }
        connection.createStatement().executeUpdate(sql)
    }
}