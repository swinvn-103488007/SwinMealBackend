package akathon.cos30017.swin_meal_backend.repository

import akathon.cos30017.swin_meal_backend.datamodel.MenuItem
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.config.DefaultDriverOption
import com.datastax.oss.driver.api.core.config.DriverConfigLoader
import com.datastax.oss.driver.api.core.config.DriverExecutionProfile
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Repository
import java.io.FileInputStream
import java.nio.file.Paths
import java.time.Duration
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


    //    fun configureQueryTimeout(session: CqlSession): CqlSession {
//        val configLoader: DriverConfigLoader = session.config.default
//        val configBuilder: DriverConfigLoader.Builder = configLoader.builder()
//
//        val timeoutDuration: Duration = Duration.ofSeconds(5) // Example: 5 seconds
//        configBuilder.withDuration(DefaultDriverOption.REQUEST_TIMEOUT, timeoutDuration)
//
//        val updatedConfigLoader: DriverConfigLoader = configBuilder.build()
//        val updatedSession: CqlSession = CqlSession.builder()
//            .withConfigLoader(updatedConfigLoader)
//            .build()
//
//        return updatedSession
//    }
    fun getAllMenu() : List<MenuItem> {
        val fullMenuList = mutableListOf<MenuItem>()
        val categories = listOf<String>("bbqs","burgers","steaks","breads","fried-chicken","drinks","ice-cream",
                                        "desserts","sausages","chocolates","sandwiches","pizzas")
//        val query = "SELECT * FROM $keyspace.menu_full"
        runBlocking {
            coroutineScope {
                for (category in categories) {
                    launch {
                        val query = "SELECT * FROM $keyspace.menu_full where category='$category'"
                        val rs = session.execute(query)
                        for (row in rs) {
                            fullMenuList.add(MenuItem(
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
                            ))
                        }
                    }
                }
            }
        }
        println(fullMenuList.size)
        return fullMenuList
    }
}