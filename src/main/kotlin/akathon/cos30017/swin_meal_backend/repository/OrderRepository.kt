package akathon.cos30017.swin_meal_backend.repository

import akathon.cos30017.swin_meal_backend.datamodel.CurrentOrderItem
import akathon.cos30017.swin_meal_backend.datamodel.OrderHistoryItem
import com.datastax.oss.driver.api.core.CqlSession
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Repository
import java.net.URL
import java.nio.file.Paths

@Repository
class OrderRepository(@Value("\${astra.secureBundlePath}") private var secureBundlePath: String,
                      @Value("\${astra.keyspace}") private var keyspace: String,
                      @Value("\${astra.clientId}") private var clientId: String,
                      @Value("\${astra.clientSecret}") private var clientSecret: String) {

    private val connectionBundle = ClassPathResource(secureBundlePath).inputStream
    private var session = CqlSession.builder()
        .withCloudSecureConnectBundle(connectionBundle)
        .withAuthCredentials(clientId, clientSecret)
        .build()
    fun addNewOrderHistory(custEmail: String, custName: String,item: OrderHistoryItem) {
        val query = "INSERT INTO " +
                "${keyspace}.order_by_customer(cust_email,cust_name,date,order_id,order_status,order_payment,price) " +
                "VALUES('$custEmail','$custName','${item.date}','${item.orderId}','${item.orderStatus}','${item.orderPayment}','${item.price}')"
        session.execute(query)
    }

    fun getAllOrderHistory(custEmail: String): List<OrderHistoryItem>{
        val query = "SELECT date,order_id,order_status,order_payment,price" +
                " FROM ${keyspace}.order_by_customer WHERE cust_email='${custEmail}' ALLOW FILTERING"
        val rs = session.execute(query)
        return rs.map{row ->
            OrderHistoryItem(
                row.getString("date")!!,
                row.getString("order_id")!!,
                row.getString("order_status")!!,
                row.getString("order_payment")!!,
                row.getString("price")!!
            )
        }.toList()
    }

    fun markOrderHistoryAsDone(custEmail: String, orderId: String) {
        val query = "UPDATE ${keyspace}.order_by_customer" +
                " SET order_status='Order Done' WHERE cust_email='$custEmail' AND order_id='${orderId}'"
        session.execute(query)
    }

    fun deleteHistoryData(custEmail: String, orderId: String) {
        val query = "DELETE FROM ${keyspace}.order_by_customer WHERE cust_email='$custEmail' AND order_id='$orderId'"
        session.execute(query)
    }

    fun addNewCurrentOrder(custEmail: String, custName: String, item: CurrentOrderItem) {
        val query = "INSERT INTO ${keyspace}.current_order_by_customer(cust_email,cust_name," +
                "item_name,item_quantity,order_id,payment_status,total_price,tax,sub_total,take_away_time) " +
                "VALUES('$custEmail','$custName','${item.orderItemNames}','${item.orderItemQuantities}'," +
                "'${item.orderId}','${item.paymentStatus}','${item.totalItemPrice}'," +
                "'${item.tax}','${item.subTotal}','${item.takeAwayTime}')"
        session.execute(query)
    }

    fun getAllCurrentOrder(custEmail: String): List<CurrentOrderItem> {
        val query = "SELECT item_name, item_quantity,order_id,payment_status,take_away_time," +
                "total_price,tax,sub_total FROM ${keyspace}.current_order_by_customer WHERE cust_email='$custEmail' ALLOW FILTERING"
        val rs = session.execute(query)
        return rs.map {row ->
            CurrentOrderItem(
                row.getString("order_id")!!,
                row.getString("take_away_time")!!,
                row.getString("payment_status")!!,
                row.getString("item_name")!!,
                row.getString("item_quantity")!!,
                row.getString("total_price")!!,
                row.getString("tax")!!,
                row.getString("sub_total")!!
            )
        }.toList()
    }

    fun markOrderHistoryAsCancel(custEmail: String, orderId: String) {
        val query = "UPDATE ${keyspace}.order_by_customer" +
                " SET order_status='Order Cancel' WHERE cust_email='$custEmail' AND order_id='${orderId}'"
        session.execute(query)
    }

    fun deleteReceivedFromCurrentOrder(custEmail: String, orderId: String) {
        val query = "DELETE FROM ${keyspace}.current_order_by_customer" +
                " WHERE cust_email='$custEmail' AND order_id='$orderId'"
        session.execute(query)
    }

}