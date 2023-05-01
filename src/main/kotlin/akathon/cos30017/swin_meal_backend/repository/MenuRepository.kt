package akathon.cos30017.swin_meal_backend.repository

import akathon.cos30017.swin_meal_backend.datamodel.MenuItem
import com.datastax.oss.driver.api.core.CqlSession
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.io.FileInputStream
import java.nio.file.Paths
import kotlin.io.path.Path

@Repository
class MenuRepository() {

    @Value("\${astra.keyspace}")
    private lateinit var keyspace: String
    private var session = CqlSession.builder()
            .withCloudSecureConnectBundle(Paths.get("E:/spring2023/connection_bundles/secure-connect-swinmealdb.zip"))
            .withAuthCredentials("AcRCBgaqoLkRzpruuQKQWpdS", "lXLR2SszZgnO4JiF,,ugKfELqmXwP74jR4_-Qq-T,f9P4OKRnoWeB0zOos,T0qg0+85KcW3MUdmZY1Zj3sC4x3dSTZZM8vXz0Ki08J_R4MHfQS9rGHR8twLI7NWfql2Q")
            .build()

    fun getAllMenu() : List<MenuItem> {
        val query = "SELECT * FROM $keyspace.menu"
        val rs =  session.execute(query)
        return rs.map{ row ->
            MenuItem(
                row.getString("id"),
                row.getString("img"),
                row.getString("name"),
                row.getFloat("price"),
                row.getString("dsc"),
                row.getString("category"),
                row.getFloat("rate"),
            )
        }.toList()
    }
}