package akathon.cos30017.swin_meal_backend.repository

import akathon.cos30017.swin_meal_backend.datamodel.Customer
import com.datastax.oss.driver.api.core.CqlSession
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.nio.file.Paths

@Repository
class CustomerRepository {
    @Value("\${astra.keyspace}")
    private lateinit var keyspace: String

    private var session = CqlSession.builder()
        .withCloudSecureConnectBundle(Paths.get("E:/spring2023/connection_bundles/secure-connect-swinmealdb.zip"))
        .withAuthCredentials("AcRCBgaqoLkRzpruuQKQWpdS", "lXLR2SszZgnO4JiF,,ugKfELqmXwP74jR4_-Qq-T,f9P4OKRnoWeB0zOos,T0qg0+85KcW3MUdmZY1Zj3sC4x3dSTZZM8vXz0Ki08J_R4MHfQS9rGHR8twLI7NWfql2Q")
        .build()

    fun addNewCustomer(customer: Customer) {
        val query = "INSERT INTO ${keyspace}.customer_by_uuid(email,id,password,name,activity_level,age,gender,height,job,weight)" +
                "VALUES ('${customer.email}', ${customer.id}, '${customer.password}','${customer.name}','${customer.activityLevel}'," +
                "${customer.age}, '${customer.gender}', ${customer.height}, '${customer.job}', ${customer.weight})"
        session.execute(query)
    }

    fun haveUsedEmail(email: String): Boolean {
        val query = "SELECT email from ${keyspace}.customer_by_uuid where email='${email}' ALLOW FILTERING"
        val rs = session.execute(query)
        return (rs.one()?.getString("email").equals(email))
    }

    fun validateCredentials(email: String, password: String): Boolean {

        val query = "SELECT email, password from ${keyspace}.customer_by_uuid where email='${email}' and password='${password}' ALLOW FILTERING"
        val rs = session.execute(query)
        val matchedCustomer = rs.one()

        // Debug message
        println("Customer email: ${matchedCustomer?.getString("email")}")
        println("Customer uuid: ${matchedCustomer?.getString("password")}" )

        return (matchedCustomer?.getString("email").equals(email)
                && matchedCustomer?.getString("password").equals(password))
    }
}