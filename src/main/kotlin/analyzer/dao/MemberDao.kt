package analyzer.dao

import analyzer.clients.DbClient
import analyzer.model.Member

/**
 * Created by Nikolay V. Petrov on 07.03.2018.
 */
object MemberDao {
    private val connection = DbClient.connection

    fun findById(id: Int): Member? {
        var member: Member? = null
        val sql = "SELECT id, first_name, last_name FROM members where id = $id"
        val rs = connection.createStatement().executeQuery(sql)
        with(rs) {
            if (next())
                member = Member(
                        getInt("id"),
                        getString("first_name"),
                        getString("last_name")
                )
        }
        return member
    }

    fun getAllMembers(): List<Member>? {
        val list: MutableList<Member> = mutableListOf()
        val sql = "SELECT id, first_name, last_name FROM members"
        val rs = connection.createStatement().executeQuery(sql)
        with(rs) {
            while (next()) {
                list.add(
                        Member(
                                getInt("id"),
                                getString("first_name"),
                                getString("last_name")))
            }
        }
        return list
    }

    fun insert(member: Member) {
        val sql =
                with (member) {
                    "INSERT INTO members (id, first_name, last_name) " +
                            "VALUES ($id, '$firstName', '$lastName')"
                }
        connection.createStatement().executeUpdate(sql)
    }

    fun delete(member: Member) {
        val sql = "DELETE FROM members WHERE id = ${member.id}"
        connection.createStatement().executeUpdate(sql)
    }

}