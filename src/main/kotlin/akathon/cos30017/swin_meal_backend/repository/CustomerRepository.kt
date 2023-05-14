package akathon.cos30017.swin_meal_backend.repository

import akathon.cos30017.swin_meal_backend.datamodel.Customer
import com.datastax.oss.driver.api.core.CqlSession
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.net.URL
import java.nio.file.Paths

@Repository
class CustomerRepository {

    @Value("\${astra.keyspace}")
    private lateinit var keyspace: String
    private val defaultPassword = "default"
    private var session = CqlSession.builder()
        .withCloudSecureConnectBundle(Paths.get("E:/spring2023/connection_bundles/secure-connect-swinmealdb.zip"))
        .withAuthCredentials("AcRCBgaqoLkRzpruuQKQWpdS", "lXLR2SszZgnO4JiF,,ugKfELqmXwP74jR4_-Qq-T,f9P4OKRnoWeB0zOos,T0qg0+85KcW3MUdmZY1Zj3sC4x3dSTZZM8vXz0Ki08J_R4MHfQS9rGHR8twLI7NWfql2Q")
        .build()

    fun addNewCustomer(customer: Customer) {
        val query = "INSERT INTO ${keyspace}.customer_by_email(email,password,name,activity_level,age,gender,height,weight)" +
                "VALUES ('${customer.email}','${customer.password}','${customer.name}','${customer.activityLevel}'," +
                "${customer.age}, '${customer.gender}', ${customer.height}, ${customer.weight})"
        session.execute(query)
    }

    fun haveUsedEmail(email: String): Boolean {
        val query = "SELECT email from ${keyspace}.customer_by_email where email='${email}' ALLOW FILTERING"
        val rs = session.execute(query)
        return (rs.one()?.getString("email").equals(email))
    }

    fun validateCredentials(email: String, password: String): Boolean {

        val query = "SELECT email, password from ${keyspace}.customer_by_email where email='${email}' and password='${password}' ALLOW FILTERING"
        val rs = session.execute(query)
        val matchedCustomer = rs.one()

        // Debug message
//        println("Customer email: ${matchedCustomer?.getString("email")}")
//        println("Customer uuid: ${matchedCustomer?.getString("password")}" )

        return (matchedCustomer?.getString("email").equals(email)
                && matchedCustomer?.getString("password").equals(password))
    }

    fun getCustomerData(email: String) : Customer{
        val query = "SELECT * from ${keyspace}.customer_by_email where email='${email}' ALLOW FILTERING"
        val rs = session.execute(query)
        val firstRow = rs.one()
        return Customer( firstRow?.getString("email")!!,
                        defaultPassword, firstRow.getString("name"),
                        firstRow.getString("gender"), firstRow.getInt("age"),
                        firstRow.getFloat("height"), firstRow.getFloat("weight"),
                        firstRow.getString("activity_level"))
    }

    fun updateCustomerHealthData(email: String, gender: String, age: Int,
                                 height: Float, weight: Float, activityLevel: String)
    {
        val query = "UPDATE ${keyspace}.customer_by_email " +
                "SET gender = '$gender', age= $age, height=$height, weight=$weight, activity_level='$activityLevel'" +
                "where email='$email'"
        session.execute(query)
    }
}