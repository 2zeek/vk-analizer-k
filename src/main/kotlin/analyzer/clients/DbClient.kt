package analyzer.clients

import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection


/**
 * Created by Nikolay V. Petrov on 07.03.2018.
 */
class DbClient {

    companion object {
        val connection: Connection = DbClient().createConnection()
    }

    fun createConnection(): Connection {
        val ds = HikariDataSource()
        ds.jdbcUrl = "jdbc:postgresql://127.0.0.1:5432/db1"
        ds.username = "postgres"
        ds.password = "postgres"
        ds.driverClassName = "org.postgresql.Driver"
        return ds.connection
    }

}