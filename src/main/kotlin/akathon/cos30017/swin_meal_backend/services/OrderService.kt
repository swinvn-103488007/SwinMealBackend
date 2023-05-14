package akathon.cos30017.swin_meal_backend.services

import akathon.cos30017.swin_meal_backend.datamodel.CurrentOrderItem
import akathon.cos30017.swin_meal_backend.datamodel.OrderHistoryItem
import akathon.cos30017.swin_meal_backend.repository.OrderRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.net.SocketTimeoutException
import java.net.http.HttpResponse.ResponseInfo

@Service
class OrderService(private val orderRepository: OrderRepository) {
    fun handleNewOrder(custEmail: String, custName: String, orderHistoryItem: OrderHistoryItem,
                       currentOrderItem: CurrentOrderItem): ResponseEntity<Any> {
        try {
            orderRepository.addNewOrderHistory(custEmail, custName, orderHistoryItem)
            orderRepository.addNewCurrentOrder(custEmail, custName, currentOrderItem)
        } catch (e: SocketTimeoutException) {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Timeout exception occurred")
        }
        return ResponseEntity.ok("New order saved")
    }

    fun getAllOrderHistory(custEmail: String): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(orderRepository.getAllOrderHistory(custEmail))
    }

    fun handleOrderMarkAsReceived(custEmail: String, orderId: String): ResponseEntity<Any> {
        try{
            orderRepository.deleteReceivedFromCurrentOrder(custEmail,orderId)
            orderRepository.markOrderHistoryAsDone(custEmail,orderId)
        } catch (e: Exception) {
            // Handle exception
        }
        return ResponseEntity.ok("Food Received Update")
    }

    fun getAllCurrentOrder(custEmail: String): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.OK).body(orderRepository.getAllCurrentOrder(custEmail))
    }
}