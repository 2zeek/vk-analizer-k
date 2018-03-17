package analyzer.dao

import analyzer.clients.DbClient
import analyzer.model.Post

/**
 * Created by Nikolay V. Petrov on 07.03.2018.
 */
object PostDao {
    private val connection = DbClient.connection

    fun findById(id: Int): Post? {
        var post: Post? = null
        val sql = "SELECT id, likes, reposts, comments FROM posts WHERE id = $id"
        val rs = connection.createStatement().executeQuery(sql)
        with(rs) {
            if(next())
                post = Post(
                        getInt("id"),
                        getInt("likes"),
                        getInt("reposts"),
                        getInt("comments"))
        }
        return post
    }

    fun upsert(post: Post) {
        val sql =
                with (post) {
                    "INSERT INTO posts (id, likes, reposts, comments) VALUES ($id, $likes, $reposts, $comments)" +
                            "ON CONFLICT (id) DO UPDATE SET likes = $likes, reposts = $reposts, comments = $comments"
                }
        connection.createStatement().executeUpdate(sql)
    }
}