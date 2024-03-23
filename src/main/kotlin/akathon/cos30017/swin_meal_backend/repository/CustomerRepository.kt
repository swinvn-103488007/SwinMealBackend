package akathon.cos30017.swin_meal_backend.repository

import akathon.cos30017.swin_meal_backend.datamodel.Customer
import com.datastax.oss.driver.api.core.CqlSession
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Repository

@Repository
class CustomerRepository(@Value("\${astra.secureBundlePath}") private var secureBundlePath: String,
                         @Value("\${astra.keyspace}") private var keyspace: String,
                         @Value("\${astra.clientId}") private var clientId: String,
                         @Value("\${astra.clientSecret}") private var clientSecret: String)
{
    private val defaultPassword = "default"
    private val connectionBundle = ClassPathResource(secureBundlePath).inputStream
    private var session = CqlSession.builder()
        .withCloudSecureConnectBundle(connectionBundle)
        .withAuthCredentials(clientId, clientSecret)
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