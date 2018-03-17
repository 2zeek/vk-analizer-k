package analyzer.dao

import analyzer.clients.DbClient
import analyzer.utils.Utils
import analyzer.model.Repost

/**
 * Created by Nikolay V. Petrov on 07.03.2018.
 */
object RepostDao {
    private val connection = DbClient.connection

    fun findById(id: Int): Repost? {
        var repost: Repost? = null
        val sql = "SELECT id, list FROM reposts WHERE id = $id"
        val rs = connection.createStatement().executeQuery(sql)
        with(rs) {
            if (next())
                repost = Repost(
                        getInt("id"),
                        Utils.stringToList(getString("list"))
                )
        }
        return repost
    }

    fun upsert(repost: Repost) {
        val sql =
                with (repost) {
                    "INSERT INTO reposts (id, list) VALUES ($id, '${Utils.listToString(list)}') " +
                            "ON CONFLICT (id) DO UPDATE SET list = ${Utils.listToString(list)}"
                }
        connection.createStatement().executeUpdate(sql)

    }
}