package akathon.cos30017.swin_meal_backend.repository

import akathon.cos30017.swin_meal_backend.datamodel.MenuItem
import com.datastax.oss.driver.api.core.CqlSession
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Repository
import java.io.FileInputStream
import java.nio.file.Paths
import kotlin.io.path.Path

@Repository
class MenuRepository(@Value("\${astra.secureBundlePath}") private var secureBundlePath: String,
                     @Value("\${astra.keyspace}") private var keyspace: String,
                     @Value("\${astra.clientId}") private var clientId: String,
                     @Value("\${astra.clientSecret}") private var clientSecret: String) {

    private val connectionBundle = ClassPathResource(secureBundlePath).inputStream
    private var session = CqlSession.builder()
        .withCloudSecureConnectBundle(connectionBundle)
        .withAuthCredentials(clientId, clientSecret)
        .build()

    fun getAllMenu() : List<MenuItem> {
        val query = "SELECT * FROM $keyspace.menu_full"
        val rs =  session.execute(query)
        return rs.map{ row ->
            MenuItem(
                row.getInt("id"),
                row.getString("img"),
                row.getString("name"),
                row.getFloat("price"),
                row.getString("dsc"),
                row.getString("category"),
                row.getFloat("rate"),
                row.getFloat("calories"),
                row.getFloat("protein"),
                row.getFloat("carbohydrate"),
                row.getFloat("fat")
            )
        }.toList()
    }
}